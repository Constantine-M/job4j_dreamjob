package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.City;

import java.util.Collection;

/**
 * Данный интерфейс описывает репозиторий.
 * В проекте он будет относиться к слою
 * persistence.
 */
public interface CityRepository {

    Collection<City> findAll();
}
