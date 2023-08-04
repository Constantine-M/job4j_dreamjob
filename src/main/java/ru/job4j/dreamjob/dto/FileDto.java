package ru.job4j.dreamjob.dto;

/**
 * Данный класс описывает файл-объект
 * для передачи данных.
 *
 * <p>DTO (Data Transfer Object)
 * Это такой объект, который используется
 * для передачи данных между различными уровнями
 * (слоями) приложения, например, между
 * слоями контроллеров и сервисов. Классы в
 * пакете model отражают доменные объекты,
 * их структура отражает структуру объектов
 * из предметной области. Однако, что
 * нужно представлению не всегда
 * соответствует доменной модели.
 * С другой стороны, DTO позволяют
 * комбинировать данные из разных доменных
 * моделей.
 */
public class FileDto {

    private String name;

    /**
     * Тут кроется различие.
     * Доменная модель хранит путь, а не содержимое.
     */
    private byte[] content;

    public FileDto(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
