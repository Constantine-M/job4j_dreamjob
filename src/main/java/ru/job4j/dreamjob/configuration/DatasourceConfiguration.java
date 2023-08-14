package ru.job4j.dreamjob.configuration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;
import org.sql2o.quirks.NoQuirks;
import org.sql2o.quirks.Quirks;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Данный класс описывает конфигурацию БД.
 * Для соблюдения SRP все настройки должны
 * быть в классах-настройках.
 */
@Configuration
public class DatasourceConfiguration {

    /**
     * С помощью библиотеки apache.commons.dbcp2
     * мы можем воспользоваться методами
     * класса {@link BasicDataSource} и
     * настроить базовые параметры для
     * подключения к БД. Т.о. мы настроили
     * наш пул.
     */
    @Bean
    public DataSource connectionPool(@Value("${datasource.url}") String url,
                                     @Value("${datasource.username}") String username,
                                     @Value("${datasource.password}") String password) {
        return new BasicDataSource() {
            {
                setUrl(url);
                setUsername(username);
                setPassword(password);
            }
        };
    }

    /**
     * Sql2o - небольшая и быстрая библиотека
     * для доступа к реляционным базам данных.
     * По сути это клиент для работы с БД.
     */
    @Bean
    public Sql2o databaseClient(DataSource dataSource) {
        return new Sql2o(dataSource, createConverters());
    }

    /**
     * Данный метод создает конвертер, который
     * делает преобразование из Timestamp в
     * LocalDateTime и наоборот.
     *
     * Этот конвертер будет использоваться Sql2o.
     * 1.Конвертер - это карта(Map<>) из класса-реализации
     * {@link NoQuirks}, состоящая из
     * класса, в который конвертируем, и собсно
     * самого конвертера, где описана логика
     * конвертации. У нас это класс {@link LocalDateTime}
     * и конвертер, который имеет 2 метода -
     * convert() и toDatabaseParam().
     */
    private Quirks createConverters() {
        return new NoQuirks() {
            {
                converters.put(LocalDateTime.class, new Converter<LocalDateTime>() {

                    @Override
                    public LocalDateTime convert(Object value) throws ConverterException {
                        if (value == null) {
                            return null;
                        }
                        if (!(value instanceof Timestamp)) {
                            throw new ConverterException("Invalid value to convert");
                        }
                        return ((Timestamp) value).toLocalDateTime();
                    }

                    @Override
                    public Object toDatabaseParam(LocalDateTime value) {
                        return value == null ? null : Timestamp.valueOf(value);
                    }

                });
            }
        };
    }
}
