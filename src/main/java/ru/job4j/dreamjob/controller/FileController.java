package ru.job4j.dreamjob.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.service.FileService;

@Controller
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Данный метод обрабатывает запрос
     * на поиск файла по ID.
     *
     * <p>{@link ResponseEntity} представляет
     * собой оболочку для Java классов,
     * благодаря которой мы в полной мере
     * сможем реализовать RESTfull архитектуру.
     *
     * <p>То есть конструктор {@link ResponseEntity}
     * позволяет перегружать этот объект,
     * добавляя в него не только наш
     * возвращаемый тип, но и статус,
     * чтобы фронтенд мог понимать, что
     * именно пошло не так.
     *
     * <p>Если файл не будет найден, то
     * от сервера придет ответ с кодом
     * ошибки 404 (not found), иначе ответ
     * с кодом 200 (OK) очень грубо говоря.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        var contentOptional = fileService.findById(id);
        if (contentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contentOptional.get().getContent());
    }
}
