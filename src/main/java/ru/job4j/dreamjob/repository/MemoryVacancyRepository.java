package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Данный класс описывает реализацию
 * репозитория.
 *
 * <p>Интерфейсы репозиториев принадлежат
 * к слою домена. Реализация же относятся
 * к слою приложения. Это означает,
 * что мы свободны при построении
 * архитектуры на уровне доменного
 * слоя без необходимости зависеть
 * от слоя сервиса.
 */
@Repository
public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();

    private int nextId = 1;

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "Description of dream job for intern", LocalDateTime.now()));
        save(new Vacancy(0, "Junior Java Developer", "Description of dream job for junior", LocalDateTime.now()));
        save(new Vacancy(0, "Junior+ Java Developer", "Description of dream job for junior+", LocalDateTime.now()));
        save(new Vacancy(0, "Middle Java Developer", "Description of dream job for middle", LocalDateTime.now()));
        save(new Vacancy(0, "Middle+ Java Developer", "Description of dream job for middle+", LocalDateTime.now()));
        save(new Vacancy(0, "Senior Java Developer", "Description of dream job for senior", LocalDateTime.now()));
    }

    public static MemoryVacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
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
     * <p>Здесь мы используем метод
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
                        oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription(), vacancy.getCreationDate()
                )
        ) != null;
    }

    /**
     * Данный метод находит вакансию по ID.
     *
     * <p>Здесь используется {@link Optional}.
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
