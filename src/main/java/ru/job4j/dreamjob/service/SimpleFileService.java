package ru.job4j.dreamjob.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
public class SimpleFileService implements FileService {

    private final FileRepository fileRepository;

    private final String storageDirectory;

    /**
     * В данном конструкторе следует
     * обратить внимание на аннотацию
     * {@link Value}, с помощью которой
     * можно подставить на место storageDirectory
     * значение из файла application.properties
     * с ключом file.directory.
     */
    public SimpleFileService(FileRepository sql2oFileRepository,
                             @Value("${file.directory}") String storageDirectory) {
        this.fileRepository = sql2oFileRepository;
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
    }

    private void createStorageDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Данный метод позволяет сохранить файл
     * {@link FileDto} и путь к нему.
     *
     * @param fileDto файл-объект для передачи данных.
     * @return
     */
    @Override
    public File save(FileDto fileDto) {
        var path = getNewFilePath(fileDto.getName());
        writeFileBytes(path, fileDto.getContent());
        return fileRepository.save(new File(fileDto.getName(), path));
    }

    /**
     * Данный метод создает уникальный путь для
     * нового файла.
     *
     * UUID - это просто рандомная строка
     * определенного формата.
     *
     * @param sourceName имя файла DTO.
     * @return новый сгенерированный путь.
     */
    private String getNewFilePath(String sourceName) {
        return storageDirectory + java.io.File.separator + UUID.randomUUID() + sourceName;
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Данный метод позволяет найти
     * объект {@link FileDto} по его ID.
     *
     * Здесь мы используем метод
     * {@link  Optional#of} Разница от
     * {@link  Optional#ofNullable} в том, что
     * Optional.of бросит исключение
     * NullPointerException, если ему передать
     * значение null в качестве параметра.
     *
     * Optional.ofNullable вернёт Optional,
     * не содержащий значение, если ему передать null.
     */
    @Override
    public Optional<FileDto> findById(int id) {
        var fileOptional = fileRepository.findById(id);
        if (fileOptional.isEmpty()) {
            return Optional.empty();
        }
        var content = readFileAsBytes(fileOptional.get().getPath());
        return Optional.of(new FileDto(fileOptional.get().getName(), content));
    }

    /**
     * Данный метод читает файл побайтово.
     *
     * @param path путь к файлу.
     * @return массив байт.
     */
    private byte[] readFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Данный метод удаляет файл по ID.
     *
     * Сначала удаляем данные, а
     * потом объект DTO из карты по его ID.
     */
    @Override
    public boolean deleteById(int id) {
        var fileOptional = fileRepository.findById(id);
        if (fileOptional.isEmpty()) {
            return false;
        }
        deleteFile(fileOptional.get().getPath());
        return fileRepository.deleteById(id);
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
