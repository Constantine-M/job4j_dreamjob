package ru.job4j.dreamjob.model;

import java.util.Objects;

/**
 * Данная модель описывает файл,
 * который будет содержать в себе
 * самую разную информацию.
 *
 * <p>Данная модель является
 * доменным объектом.
 */
public class File {

    private int id;

    private String name;

    /**
     * Здесь собственно различие между
     * доменным объектом и объектом
     * {@link ru.job4j.dreamjob.dto.FileDto}.
     */
    private String path;

    public File(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        File file = (File) o;
        return id == file.id && Objects.equals(path, file.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path);
    }
}
