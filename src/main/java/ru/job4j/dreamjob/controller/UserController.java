package ru.job4j.dreamjob.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

@Controller
@RequestMapping("/users") /* Работать с пользователями будем по URI /users*/
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Данный метод обрабатывает запрос,
     * согласно которому мы получаем
     * непосредственно сверстанную
     * html страничку.
     */
    @GetMapping("/register")
    public String getRegistrationPage() {
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           Model model) {
        var savedUser = userService.save(user);
        if (savedUser.isEmpty()) {
            model.addAttribute("message", "Пользователь с такой почтой уже существует!");
            return "users/register";
        }
        return "redirect:/vacancies";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    /**
     * Данный метод обрабатывает запрос на
     * авторизацию пользователя.
     *
     * Чтобы закрепить открытую сессию
     * за пользователем, воспользуемся
     * интерфейсом {@link HttpServletRequest}
     * и получим сессию.
     *
     * Метод {@link HttpServletRequest#getSession()}
     * вернет нам объект {@link HttpSession}.
     *
     * А затем добавим данные в сессию
     * с помощью метода {@link HttpSession#setAttribute}.
     * Например, добавим туда пользователя.
     * Т.о. мы закрепили пользователя за сессией.
     * Данные сессии привязываются к клиенту
     * и доступны во всем приложении.
     *
     * Обратите внимание, что внутри HttpSession
     * используется многопоточная коллекция
     * ConcurrentHashMap. Это связано с многопоточным
     * окружением. Увидеть это можно в реализации
     * модуля catalina -> session
     * {@link org.apache.catalina.session.StandardSession}.
     */
    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user,
                            Model model,
                            HttpSession session) {
        var userOptional = userService.findByEmailAndPassword(user.getEmail(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Почта или пароль введены неверно");
            return "users/login";
        }
        session.setAttribute("user", userOptional.get());
        return "redirect:/vacancies";
    }

    /**
     * Данный метод обрабатывает запрос
     * пользователя, который закрывает сессию
     * (разлогинивается).
     *
     * В данном случае, чтобы удалить
     * все данные, связанные с пользователем,
     * нужно использовать метод
     * {@link HttpSession#invalidate()}.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }
}
