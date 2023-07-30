package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

/**
 * Данный класс описывает контроллер.
 *
 * <p>Controller (Контроллер) обрабатывает
 * запрос пользователя, создаёт
 * соответствующую Модель и передаёт
 * её для отображения в Вид.
 *
 * <p>В нашем случае контроллер заполняет
 * {@link Model} и передает два объекта
 * в Thymeleaf – Model и View(vacancies.html).
 * Thymeleaf генерирует HTML и
 * возвращает ее клиенту.
 */
@Controller
@RequestMapping("/vacancies") /* Работать с кандидатами будем по URI /vacancies */
public class VacancyController {

    /**
     * Все обращения в контроллере теперь
     * адресуются к сервису. Ранее же
     * было обращение напрямую к репозиторию.
     */
    private final VacancyService vacancyService;

    private final CityService cityService;

    public VacancyController(VacancyService vacancyService, CityService cityService) {
        this.vacancyService = vacancyService;
        this.cityService = cityService;
    }

    /**
     * Данный метод принимает объект Model.
     * Он используется Thymeleaf для поиска
     * объектов, которые нужны отобразить на виде.
     *
     * <p>В Model мы добавляем объект vacancies.
     *
     * @param model модель, которую необходимо
     *              отобразить на виде.
     */
    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("vacancies", vacancyService.findAll());
        return "vacancies/list";
    }

    /**
     * В данном методе аннотация @GetMapping
     * здесь говорит SpringBoot, что по
     * заданному URI (/vacancies/create)
     * нужно обслужить HTTP GET запрос.
     *
     * <p>GET метод браузер использует
     * при открытии страниц.
     */
    @GetMapping("/create")
    public String getCreationPage(Model model) {
        model.addAttribute("cities", cityService.findAll());
        return "vacancies/create";
    }

    /**
     * Данный метод обрабатывает запрос
     * на создание вакансии.
     *
     * <p>Этот метод можно реализовать по-разному.
     *
     * <p>1.Используя интерфейс
     * {@link HttpServletRequest}, мы получили
     * введенные пользователем данные через метод
     * {@link HttpServletRequest#getParameter}.
     *
     * <p>2.Используя аннотацию {@link ModelAttribute}.
     * @ModelAttribute сообщаем Spring,
     * чтобы тот собрал объект {@link Vacancy}
     * из параметров запроса. При этом
     * следует НЕ ЗАБЫВАТЬ создавать пустой
     * конструктор в классе модели и
     * проинициализировать поля, которые
     * не участвуют при маппинге.
     *
     * <p>Также обрати внимание на то, что
     * здесь мы уже используем аннотацию
     * {@link PostMapping}.
     *
     * <p>Ключевое слово redirect, которое
     * сообщает Spring, чтобы после выполнения
     * метода create перейти к обработке ссылки
     * /vacancies GET, то есть вывести
     * таблицу со списком всех вакансий.
     *
     * @return возврат к странице с вакансиями.
     */
    @PostMapping("/create")
    public String create(@ModelAttribute Vacancy vacancy) {
        vacancyService.save(vacancy);
        return "redirect:/vacancies";
    }

    /**
     * Данный метод обрабатывает запрос
     * на поиск вакансии по идентификатору,
     * извлекает вакансию из репозитория
     * и возвращает на страницу.
     *
     * <p>Здесь мы используем аннотацию
     * {@link PathVariable}, которая
     * позволяет работать с параметрами,
     * передаваемыми через адрес запроса.
     *
     * @return страница просмотра вакансии.
     */
    @GetMapping("/{id}")
    public String getByID(Model model, @PathVariable int id) {
        var vacancyOptional = vacancyService.findById(id);
        if (vacancyOptional.isEmpty()) {
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        model.addAttribute("vacancy", vacancyOptional.get());
        model.addAttribute("cities", cityService.findAll());
        return "vacancies/one";
    }

    /**
     * Производит обновление и если
     * оно произошло, то делает
     * перенаправление на страницу
     * со всеми вакансиями.
     *
     * @return страница со всеми вакансиями.
     */
    @PostMapping("/update")
    public String update(@ModelAttribute Vacancy vacancy, Model model) {
        var isUpdated = vacancyService.update(vacancy);
        if (!isUpdated) {
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }

    /**
     * Производит удаление и если
     * оно произошло, то делает
     * перенаправление на страницу
     * со всеми вакансиями.
     *
     * @return страница со всеми вакансиями.
     */
    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        var isDeleted = vacancyService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }
}
