package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;

import java.util.Optional;

/**
 * Данный интерфейс описывает сервисы
 * для работы с DTO (а именно
 * {@link FileDto}.
 */
public interface FileService {

    File save(FileDto fileDto);

    Optional<FileDto> findById(int id);

    boolean deleteById(int id);
}
