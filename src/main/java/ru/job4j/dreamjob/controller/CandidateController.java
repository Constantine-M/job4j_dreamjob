package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.FileService;

@Controller
@RequestMapping("/candidates")
public class CandidateController {

    /**
     * Все обращения в контроллере теперь
     * адресуются к сервису. Ранее же
     * было обращение напрямую к репозиторию.
     */
    private final CandidateService candidateService;

    private final CityService cityService;

    public CandidateController(CandidateService candidateService, CityService cityService, FileService fileService) {
        this.candidateService = candidateService;
        this.cityService = cityService;
    }

    @GetMapping
    public String getAll(Model model, HttpSession session) {
        attachUserToSession(model, session);
        model.addAttribute("candidates", candidateService.findAll());
        return "candidates/list";
    }

    /**
     * В данном методе аннотация @GetMapping
     * здесь говорит SpringBoot, что по
     * заданному URI (/candidates/create)
     * нужно обслужить HTTP GET запрос.
     *
     * GET метод браузер использует
     * при открытии страниц.
     */
    @GetMapping("/create")
    public String getCreationPage(Model model, HttpSession session) {
        attachUserToSession(model, session);
        model.addAttribute("cities", cityService.findAll());
        return "candidates/create";
    }

    /**
     * Данный метод обрабатывает запрос
     * на создание кандидата.
     *
     * 1.Используя аннотацию {@link ModelAttribute}.
     * @ModelAttribute сообщаем Spring,
     * чтобы тот собрал объект {@link Candidate}
     * из параметров запроса. При этом
     * следует НЕ ЗАБЫВАТЬ создавать пустой
     * конструктор в классе модели и
     * проинициализировать поля, которые
     * не участвуют при маппинге.
     *
     * 2.Используя аннотацию {@link RequestParam}
     * и класс {@link MultipartFile}
     * мы получаем файл из формы. Название
     * параметра соответствует name из формы.
     *
     * 3.Благодаря конструкции
     * <li>new FileDto(file.getOriginalFilename(), file.getBytes())</li>
     * мы передаем "упакованные" в DTO данные
     * для обработки в сервисе.
     *
     * Также обрати внимание на то, что
     * здесь мы уже используем аннотацию
     * {@link PostMapping}.
     */
    @PostMapping("/create")
    public String create(@ModelAttribute Candidate candidate,
                         @RequestParam MultipartFile file,
                         Model model,
                         HttpSession session) {
        try {
            candidateService.save(candidate, new FileDto(file.getOriginalFilename(), file.getBytes()));
            return "redirect:/candidates";
        } catch (Exception exception) {
            model.addAttribute(exception.getMessage());
            return "/errors/404";
        }
    }

    @GetMapping("/{id}")
    public String getByID(Model model,
                          @PathVariable int id,
                          HttpSession session) {
        attachUserToSession(model, session);
        var candidateOptional = candidateService.findById(id);
        if (candidateOptional.isEmpty()) {
            model.addAttribute("message", "Кандидат с указанным идентификатором не найден");
            return "errors/404";
        }
        model.addAttribute("candidate", candidateOptional.get());
        model.addAttribute("cities", cityService.findAll());
        return "candidates/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Candidate candidate,
                         @RequestParam MultipartFile file,
                         Model model) {
        try {
            var isUpdated = candidateService.update(candidate, new FileDto(file.getOriginalFilename(), file.getBytes()));
            if (!isUpdated) {
                model.addAttribute("message", "Кандидат с указанным идентификатором не найден");
                return "errors/404";
            }
            return "redirect:/candidates";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        var isDeleted = candidateService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Кандидат с указанным идентификатором не найден");
            return "errors/404";
        }
        return "redirect:/candidates";
    }

    /**
     * Данный метод прикрепляет пользователя
     * к сессии.
     *
     * Если в {@link HttpSession} нет объекта
     * {@link User}, то мы создаем объект User
     * с анонимным пользователем (т.е. пользователь
     * становится гостем).
     */
    private void attachUserToSession(Model model,
                                     HttpSession session) {
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        model.addAttribute("user", user);
    }
}
