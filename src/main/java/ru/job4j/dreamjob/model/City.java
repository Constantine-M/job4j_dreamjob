package ru.job4j.dreamjob.model;

/**
 * Данная модель описывает город.
 * Мы будем указывать его в вакансии.
 */
public class City {

    private final int id;

    private final String name;

    public City(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
