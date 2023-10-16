package ru.job4j.dreamjob.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.job4j.dreamjob.model.User;

import java.io.IOException;

/**
 * Данный класс описывает фильтр,
 * в котором мы привязываем пользователя
 * к сессии.
 */
@Component
@Order(2)
public class SessionFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        var session = request.getSession();
        addUserToSession(session, request);
        chain.doFilter(request, response);
    }

    /**
     * Обрати внимание, что доступа к объекту
     * типа {@link org.springframework.ui.Model}
     * у нас нет. Дело в том, что сервлеты - это
     * более низкий уровень, а Spring MVC
     * более высокий уровень абстракции
     * с точки зрения реализации MVC.
     *
     * Model существует только в Spring MVC.
     * Под капотом Spring MVC использует Servlet API.
     *
     * Если в {@link HttpSession} нет объекта
     * {@link User}, то мы создаем объект User
     * с анонимным пользователем (т.е. пользователь
     * становится гостем).
     */
    private void addUserToSession(HttpSession session, HttpServletRequest request) {
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        request.setAttribute("user", user);
    }
}
