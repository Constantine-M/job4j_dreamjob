package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Optional;

/**
 * Изначально у нас контроллеры напрямую
 * используют репозитории. Согласно
 * архитектуре это неправильно. Поэтому
 * мы создаем слой сервисов.
 *
 * Вся бизнес-логика будет
 * прописана только в слое сервисов.
 */
public interface CandidateService {

    Candidate save(Candidate candidate, FileDto image);

    boolean deleteById(int id);

    boolean update(Candidate candidate, FileDto image);

    Optional<Candidate> findById(int id);

    Collection<Candidate> findAll();
}
