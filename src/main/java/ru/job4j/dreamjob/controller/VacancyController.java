package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.repository.MemoryVacancyRepository;
import ru.job4j.dreamjob.repository.VacancyRepository;

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
}
