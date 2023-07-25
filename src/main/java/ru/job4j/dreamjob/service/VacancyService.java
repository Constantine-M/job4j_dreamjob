package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.Optional;

/**
 * Изначально у нас контроллеры напрямую
 * используют репозитории. Согласно
 * архитектуре это неправильно. Поэтому
 * мы создаем слой сервисов.
 */
public interface VacancyService {

    Vacancy save(Vacancy vacancy);

    boolean deleteById(int id);

    boolean update(Vacancy vacancy);

    Optional<Vacancy> findById(int id);

    Collection<Vacancy> findAll();
}
