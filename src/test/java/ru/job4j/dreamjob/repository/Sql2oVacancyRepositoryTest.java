package ru.job4j.dreamjob.repository;

import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.*;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.*;

class Sql2oVacancyRepositoryTest {

    /**
     * Делаем поле статическим, т.к. будем использовать
     * один экземпляр на весь тестовый класс.
     */
    private static Sql2oVacancyRepository sql2oVacancyRepository;

    /**
     * Делаем поле статическим, т.к. при сохранении
     * вакансии нам нужно указывать файл. Делаем один
     * файл на все вакансии.
     */
    private static Sql2oFileRepository sql2oFileRepository;

    private static File file;

    /**
     * 1.Подгружаем настройки для тестовой БД,
     * используя метод {@link ClassLoader#getResourceAsStream}.
     *
     * 2.Создадим клиент БД Sql2o. Он в свою очередь зависит
     * от пула соединений. До этого за нас все настраивал Spring.
     * Теперь нам нужно это сделать "руками". Т.е. вызвать
     * метод {@link DatasourceConfiguration#connectionPool}
     * для создания пула соединений и вызвать
     * {@link DatasourceConfiguration#databaseClient}
     * для создания Sql2o.
     *
     * 3.Клиент БД настроили. Создаем на его основе репозитории.
     *
     * 4.Создаем файл на который будут ссылаться вакансии.
     */
    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oVacancyRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oVacancyRepository = new Sql2oVacancyRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        /* Нужно сохранить хотя бы один файл, т.к. Vacancy от него зависит*/
        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    /**
     * После всех тестов удаляем файл, на который
     * ссылались все вакансии.
     */
    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    /**
     * Вакансии мы удаляем после каждого теста
     * для изолированности тестирования.
     *
     * Напоминаем про принцип FIRST:
     * F(Fast) - Быстрота
     * I(Independent) - Независимость
     * R(Repeatable) - Повторяемость
     * S(Self-Validating) - Очевидность
     * T(Timely) - Своевременность
     */
    @AfterEach
    public void clearVacancies() {
        var vacancies = sql2oVacancyRepository.findAll();
        vacancies.forEach(vac -> sql2oVacancyRepository.deleteById(vac.getId()));
    }

    /**
     * В данном тесте мы используем метод
     * {@link AbstractObjectAssert#usingRecursiveComparison()}.
     * Он указывает Junit, что нужно пройтись
     * по всем свойствам объекта и сравнить их.
     * Если не вызвать этот метод, то Junit
     * сравнит оба объекта через equals().
     *
     * Нам это не подходит, потому что в нашем
     * случае equals() сравнивает только
     * id в классе {@link Vacancy}.
     */
    @Test
    public void whenSaveThenGetSame() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var vacancy = sql2oVacancyRepository.save(new Vacancy(0, "title", "description", creationDate, true, 1, file.getId()));
        var savedVacancy = sql2oVacancyRepository.findById(vacancy.getId()).get();
        assertThat(savedVacancy).usingRecursiveComparison().isEqualTo(vacancy);
    }

    /**
     * Во всех тестах мы округляем время
     * до МИНУТ, так как БД H2 и JVM с
     * разной точностью хранят дату и время.
     *
     * Разница небольшая - несколько миллисекунд.
     * Из-за этого тесты могут валиться иногда.
     */
    @Test
    public void whenSaveSeveralThenGetAll() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var vacancy1 = sql2oVacancyRepository.save(new Vacancy(0, "title1", "description1", creationDate, true, 1, file.getId()));
        var vacancy2 = sql2oVacancyRepository.save(new Vacancy(0, "title2", "description2", creationDate, false, 1, file.getId()));
        var vacancy3 = sql2oVacancyRepository.save(new Vacancy(0, "title3", "description3", creationDate, true, 1, file.getId()));
        var result = sql2oVacancyRepository.findAll();
        assertThat(result).isEqualTo(List.of(vacancy1, vacancy2, vacancy3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oVacancyRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oVacancyRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var vacancy = sql2oVacancyRepository.save(new Vacancy(0, "title", "description", creationDate, true, 1, file.getId()));
        var isDeleted = sql2oVacancyRepository.deleteById(vacancy.getId());
        var savedVacancy = sql2oVacancyRepository.findById(vacancy.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedVacancy).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oVacancyRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var vacancy = sql2oVacancyRepository.save(new Vacancy(0, "title", "description", creationDate, true, 1, file.getId()));
        var updatedVacancy = new Vacancy(
                vacancy.getId(), "new title", "new description", creationDate.plusDays(1),
                !vacancy.getVisible(), 1, file.getId()
        );
        var isUpdated = sql2oVacancyRepository.update(updatedVacancy);
        var savedVacancy = sql2oVacancyRepository.findById(updatedVacancy.getId()).get();
        assertThat(isUpdated).isTrue();
        assertThat(savedVacancy).usingRecursiveComparison().isEqualTo(updatedVacancy);
    }

    @Test
    public void whenUpdateUnExistingVacancyThenGetFalse() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var vacancy = new Vacancy(0, "title", "description", creationDate, true, 1, file.getId());
        var isUpdated = sql2oVacancyRepository.update(vacancy);
        assertThat(isUpdated).isFalse();
    }
}