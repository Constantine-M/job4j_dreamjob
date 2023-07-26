package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Optional;

/**
 * Данный интерфейс описывает репозиторий.
 * В проекте он будет относиться
 * к слою persistence.
 *
 * <p>Интерфейсы репозиториев принадлежат
 * к слою домена (бизнес-логика).
 * Реализация же относятся
 * к слою приложения. Это означает,
 * что мы свободны при построении
 * архитектуры на уровне доменного
 * слоя без необходимости зависеть
 * от слоя сервиса.
 */
public interface CandidateRepository {

    Candidate save(Candidate candidate);

    boolean deleteById(int id);

    boolean update(Candidate candidate);

    Optional<Candidate> findById(int id);

    Collection<Candidate> findAll();
}
