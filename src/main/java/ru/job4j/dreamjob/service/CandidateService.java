package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Optional;

/**
 * Изначально у нас контроллеры напрямую
 * используют репозитории. Согласно
 * архитектуре это неправильно. Поэтому
 * мы создаем слой сервисов.
 *
 * <p>Вся бизнес-логика будет
 * прописана только в слое сервисов.
 */
public interface CandidateService {

    Candidate save(Candidate candidate);

    boolean deleteById(int id);

    boolean update(Candidate candidate);

    Optional<Candidate> findById(int id);

    Collection<Candidate> findAll();
}
