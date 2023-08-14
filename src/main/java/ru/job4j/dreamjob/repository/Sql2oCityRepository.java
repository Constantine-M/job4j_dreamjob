package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.City;

import java.util.Collection;

@Repository
public class Sql2oCityRepository implements CityRepository {

    private final Sql2o sql2o;

    public Sql2oCityRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    /**
     * Данный метод находит все города.
     *
     * Здесь мы уже работаем с БД:
     * 1.Открываем соединение (возвращаем
     * соединение из пула).
     * 2.Создаем запрос к БД.
     * 3.Выполняем запрос и возвращаем список
     * городов (List). Т.к. мы указали в кач-ве
     * аргумента класс {@link City},
     * то вернется список объектов
     * городов.
     *
     * - Sql2o оборачивает всё в необрабатываемые
     * исключения, что тоже удобно.
     * - Соединение к БД не закрывается
     * как мы себе это представляем по
     * опыту работы с java.sql.Connection.
     *
     * - Во-первых, это не java.sql.Connection. Э
     * то Connection Sql2o.
     * - Во-вторых, этот объект это что-то вроде
     * сессии работы с БД. В рамках него мы
     * можем делать несколько операций и если
     * произойдет исключение, то произойдет
     * откат изменений, т.е. под капотом
     * Sql2o работает с транзакциями, что также
     * удобно т.к. не нужно делать самим
     * commit(), rollback().
     *
     * @return список городов.
     */
    @Override
    public Collection<City> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM cities");
            return query.executeAndFetch(City.class);
        }
    }
}
