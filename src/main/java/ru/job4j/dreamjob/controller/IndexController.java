package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.dreamjob.model.User;

@Controller
public class IndexController {

    /**
     * В данном методе примечательно то,
     * что мы можем создать объект
     * {@link User} с анонимным пользователем.
     *
     * Благодаря Spring, мы можем получить
     * объект {@link HttpSession} как параметр.
     */
    @GetMapping({"/", "/index"})
    public String getIndex(Model model,
                           HttpSession session) {
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        model.addAttribute("user", user);
        return "index";
    }
}
