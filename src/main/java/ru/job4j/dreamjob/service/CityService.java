package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.model.City;

import java.util.Collection;

/**
 * Данный интерфейс описывает сервисы.
 *
 * Вся бизнес-логика будет
 * прописана только в слое сервисов.
 */

public interface CityService {

    Collection<City> findAll();
}
