package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.Optional;

/**
 * Данный интерфейс описывает репозиторий.
 * В проекте он будет относиться
 * к слою persistence.
 *
 * Репозиторий — это коллекция.
 * Коллекция, которая содержит сущности
 * и может фильтровать и возвращать
 * результат обратно в зависимости от
 * требований вашего приложения.
 * Где и как он хранит эти объекты
 * является ДЕТАЛЬЮ РЕАЛИЗАЦИИ.
 *
 * Интерфейсы репозиториев принадлежат
 * к слою домена (бизнес-логика).
 * Реализация же относятся
 * к слою приложения. Это означает,
 * что мы свободны при построении
 * архитектуры на уровне доменного
 * слоя без необходимости зависеть
 * от слоя сервиса.
 */
public interface VacancyRepository {

    Vacancy save(Vacancy vacancy);

    boolean deleteById(int id);

    boolean update(Vacancy vacancy);

    Optional<Vacancy> findById(int id);

    Collection<Vacancy> findAll();
}
