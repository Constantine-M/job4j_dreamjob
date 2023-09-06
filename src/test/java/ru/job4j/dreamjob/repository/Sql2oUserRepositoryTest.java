package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        sql2oUserRepository.findAll()
                .forEach(user -> sql2oUserRepository.deleteById(user.getId()));
    }

    @Test
    void whenSaveUserThenGetSame() {
        var user = new User(0, "test", "Consta", "123");
        var savedUser = sql2oUserRepository.save(user);
        var expectedUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
        assertThat(expectedUser).usingRecursiveComparison().isEqualTo(savedUser);
    }

    @Test
    void whenSaveUserWithTheSameEmailThenGetNothing() {
        var user = new User(0, "test", "Consta", "123");
        sql2oUserRepository.save(user);
        assertThat(sql2oUserRepository.save(user)).isEqualTo(Optional.empty());
    }

    @Test
    void whenSaveSeveralUsersThenGetAll() {
        var user1 = sql2oUserRepository.save(new User(0, "test@mail.ru", "Consta", "qwerty")).get();
        var user2 = sql2oUserRepository.save(new User(0, "tes@mail.ru", "Const", "qwert")).get();
        var user3 = sql2oUserRepository.save(new User(0, "te@mail.ru", "Cons", "qwer")).get();
        var userList = sql2oUserRepository.findAll();
        assertThat(userList).isEqualTo(List.of(user1, user2, user3));
    }

    @Test
    void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findAll()).isEqualTo(Collections.emptyList());
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        var user = sql2oUserRepository.save(new User(0, "test@mail.ru", "Consta", "qwerty")).get();
        var isDeletedUser = sql2oUserRepository.deleteById(user.getId());
        var savedUser = sql2oUserRepository.findByEmailAndPassword("test@mail.ru", "qwerty");
        assertThat(isDeletedUser).isTrue();
        assertThat(savedUser).isEqualTo(Optional.empty());
    }
}