package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Данный класс описывает реализацию
 * репозитория.
 *
 * Интерфейсы репозиториев принадлежат
 * к слою домена. Реализация же относятся
 * к слою приложения. Это означает,
 * что мы свободны при построении
 * архитектуры на уровне доменного
 * слоя без необходимости зависеть
 * от слоя сервиса.
 */
@ThreadSafe
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    public MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "Description of dream job for intern", LocalDateTime.now(), true, 1, 0));
        save(new Vacancy(0, "Junior Java Developer", "Description of dream job for junior", LocalDateTime.now(), true, 3, 1));
        save(new Vacancy(0, "Junior+ Java Developer", "Description of dream job for junior+", LocalDateTime.now(), true, 3, 2));
        save(new Vacancy(0, "Middle Java Developer", "Description of dream job for middle", LocalDateTime.now(), true, 2, 3));
        save(new Vacancy(0, "Middle+ Java Developer", "Description of dream job for middle+", LocalDateTime.now(), true, 2, 4));
        save(new Vacancy(0, "Senior Java Developer", "Description of dream job for senior", LocalDateTime.now(), true, 1, 5));
    }


    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.incrementAndGet());
        vacancies.putIfAbsent(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    /**
     * Данный метод обновляет запись
     * в вакансии.
     *
     * Здесь мы используем метод
     * {@link Map#computeIfPresent}.
     * Таким образом, если мы находим
     * вакансию по ID, то перезаписываем
     * данные (перезаписываем в нашем случае
     * заголовок вакансии).
     *
     * @param vacancy вакансия.
     * @return true, если обновление записи
     * прошло успешно.
     */
    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(
                vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(
                        oldVacancy.getId(),
                        vacancy.getTitle(),
                        vacancy.getDescription(),
                        vacancy.getCreationDate(),
                        vacancy.getVisible(),
                        vacancy.getCityId(),
                        vacancy.getFileId()
                )
        ) != null;
    }

    /**
     * Данный метод находит вакансию по ID.
     *
     * Здесь используется {@link Optional}.
     * Метод {@link Optional#ofNullable}
     * возвращает {@link Vacancy} если
     * оно будет != null, иначе вернется
     * пустой Optional.
     *
     * @param id номер вакансии.
     * @return вакансия {@link Vacancy}.
     */
    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}
