package ru.job4j.dreamjob.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Данный класс описывает фильтр
 * для авторизации пользователя.
 *
 * Аннотация {@link Order} с номером 1
 * указывает, что данный фильтр
 * должен запуститься перед другим фильтром -
 * в нашем случае перед {@link SessionFilter}.
 */
@Component
@Order(1)
public class AuthorizationFilter extends HttpFilter {

    /**
     * Данный метод производит непосредственно
     * фильтрацию запросов.
     *
     * 1.Если пользователь обращается к
     * общедоступным адресам - пропускаем
     * запрос.
     * 2.Если пользователь обращается адресам,
     * требующих прав, то мы проверяем
     * вошел ли пользователь в систему.
     * Если не вошел, то перебрасываем
     * его на страницу входа.
     * 3.Если пользователь залогинен,
     * то разрешаем всё.
     *
     * По умолчанию ContextPath пустой,
     * т.к. мы запускаем проект под ROOT-ом.
     * Поэтому, если мы его здесь опустим,
     * то на работу приложения это не повлияет.
     */
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        var uri = request.getRequestURI();
        if (isAlwaysPermitted(uri)) {
            chain.doFilter(request, response);
            return;
        }
        var userLoggedIn = request.getSession().getAttribute("user") != null;
        if (!userLoggedIn) {
            var loginPageUrl = request.getContextPath() + "/users/login";
            response.sendRedirect(loginPageUrl);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * В данном методе мы проверяем
     * обращается ли пользователь к
     * общедоступным адресам.
     */
    private boolean isAlwaysPermitted(String uri) {
        return uri.startsWith("/users/register")
                || uri.startsWith("/users/login")
                || uri.startsWith("/js")
                || uri.startsWith("/css");
    }
}