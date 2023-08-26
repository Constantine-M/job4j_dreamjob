package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.*;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;

import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.LocalDateTime.*;
import static java.util.Collections.*;
import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.*;


class Sql2oCandidateRepositoryTest {

    private static Sql2oCandidateRepository sql2oCandidateRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static File file;


    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oCandidateRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oCandidateRepository = new Sql2oCandidateRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        /* Нужно сохранить хотя бы один файл, т.к. Candidate от него тоже зависит*/
        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void clearCandidates() {
        sql2oCandidateRepository.findAll()
                .forEach(candidate -> sql2oCandidateRepository.deleteById(candidate.getId()));
    }

    @Test
    public void whenSaveCandidateThenGetSame() {
        var createdDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(
                new Candidate(0, "name", "description", createdDate, 1, file.getId())
        );
        var expectedCandidate = sql2oCandidateRepository.findById(candidate.getId()).get();
        assertThat(expectedCandidate).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var createdDate = now().truncatedTo(ChronoUnit.MINUTES);
        var firstCandidate = sql2oCandidateRepository.save(new Candidate(0, "Consta", "IT student", createdDate, 1, file.getId()));
        var secondCandidate = sql2oCandidateRepository.save(new Candidate(0, "Eddie", "engineer", createdDate, 1, file.getId()));
        var thirdCandidate = sql2oCandidateRepository.save(new Candidate(0, "Zhenya", "teacher", createdDate, 1, file.getId()));
        var expectedCandidates = sql2oCandidateRepository.findAll();
        assertThat(expectedCandidates).isEqualTo(List.of(firstCandidate, secondCandidate, thirdCandidate));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oCandidateRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oCandidateRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var createdDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(new Candidate(0, "Consta", "description", createdDate, 1, file.getId()));
        var isDeletedCandidate = sql2oCandidateRepository.deleteById(candidate.getId());
        var savedCandidate = sql2oCandidateRepository.findById(candidate.getId());
        assertThat(isDeletedCandidate).isTrue();
        assertThat(savedCandidate).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oCandidateRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        var createdDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(new Candidate(0, "Consta", "description", createdDate, 1, file.getId()));
        var updatedCandidate = new Candidate(
                candidate.getId(), "Consta Mezenin", "student", createdDate.plusDays(365), 1, file.getId()
        );
        var isUpdatedCandidate = sql2oCandidateRepository.update(updatedCandidate);
        var savedCandidate = sql2oCandidateRepository.findById(updatedCandidate.getId()).get();
        assertThat(isUpdatedCandidate).isTrue();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(updatedCandidate);
    }

    @Test
    public void whenUpdateUnExistingVacancyThenGetFalse() {
        var createdDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = new Candidate(0, "Mezenin", "description", createdDate, 1, file.getId());
        assertThat(sql2oCandidateRepository.update(candidate)).isFalse();
    }
}