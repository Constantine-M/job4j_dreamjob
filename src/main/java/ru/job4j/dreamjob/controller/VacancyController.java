package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.MemoryVacancyRepository;
import ru.job4j.dreamjob.repository.VacancyRepository;

import java.time.LocalDateTime;

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

    private final VacancyRepository vacancyRepository = MemoryVacancyRepository.getInstance();

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
        model.addAttribute("vacancies", vacancyRepository.findAll());
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
    public String getCreationPage() {
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
        vacancyRepository.save(vacancy);
        return "redirect:/vacancies";
    }
}
