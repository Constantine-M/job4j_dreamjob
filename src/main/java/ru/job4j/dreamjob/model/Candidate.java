package ru.job4j.dreamjob.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Данный класс описывает
 * модель кандидата.
 */
public class Candidate {

    private int id;

    private String name;

    private String description;

    private LocalDateTime creationDate = LocalDateTime.now();

    private int cityId;

    private int fileId;

    /**
     * Важно! Не забудь создать пустой конструктор
     * (при маппинге сначала создается пустой объект,
     * а потом вызываются set методы) и
     * проинициализировать поля, которые не
     * участвуют при маппинге.
     *
     * <p>Забыл про эту мелочь и сутки искал проблему!
     * Она заключалась в том, когда отправлял запрос
     * на сервер, то сервер не понимал запрос.
     * Он ругался на ID, писал, что он = null, а
     * нужен int. Т.к. мы не прописали пустой
     * конструктор, то сервер ждал, что
     * я ему в запросе отправлю параметры -
     * id, name, description.
     *
     * <p>Т.к. я не проинициализировал
     * {@link LocalDateTime}, то при создании
     * кандидата место в таблице, которое выделено
     * под дату и время, пустовало бы.
     * Надеюсь, что это верные рассуждения, т.к.
     * я их проверил на практике, когда сравнивал
     * код для вакансии и код для кандидата.
     */
    public Candidate() {

    }

    public Candidate(int id, String name, String description, LocalDateTime creationDate, int cityId, int fileId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.cityId = cityId;
        this.fileId = fileId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }



    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Candidate candidate = (Candidate) o;
        return getId() == candidate.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
