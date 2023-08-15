package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;
import java.util.Collection;
import java.util.Optional;

/**
 * В сервисе мы просто вызываем методы
 * репозитория, потому что вся логика
 * приложения сводится к логике работы
 * с данными.
 *
 * Заменим обращение к репозиториям
 * обращениями к сервису в контроллере.
 */
@Service
public class SimpleCandidateService implements CandidateService {

    private final CandidateRepository candidateRepository;

    private final FileService fileService;

    public SimpleCandidateService(CandidateRepository candidateRepository, FileService fileService) {
        this.candidateRepository = candidateRepository;
        this.fileService = fileService;
    }

    @Override
    public Candidate save(Candidate candidate, FileDto image) {
        saveNewFile(candidate, image);
        return candidateRepository.save(candidate);
    }

    private void saveNewFile(Candidate candidate, FileDto image) {
        var file = fileService.save(image);
        candidate.setFileId(file.getId());
    }

    /**
     * Данный метод удаляет резюме кандидата
     * по его ID.
     *
     * Если резюме с таким ID
     * имеется, то сначала удаляем
     * резюме из репозитория, а затем
     * файл, прикрепленный к этому резюме.
     */
    @Override
    public boolean deleteById(int id) {
        var fileOptional = candidateRepository.findById(id);
        if (fileOptional.isEmpty()) {
            return false;
        }
        var isDeleted = candidateRepository.deleteById(id);
        fileService.deleteById(fileOptional.get().getFileId());
        return isDeleted;
    }

    /**
     * Данный метод обновляет данные
     * внутри кандидата.
     *
     * При обновлении кандидата
     * сохраняется связанный с ним файл.
     *
     * Старый файл можем удалить в
     * последнюю очередь, т.к. у каждого
     * файла свой ID, а в резюме кандидата
     * мы лишь прикрепляем ссылку на
     * этот файл.
     *
     * @param candidate обновляемое резюме кандидата.
     * @param image файл, который будет привязан
     *              к кандидату.
     * @return true если обновление
     * прошло успешно.
     */
    @Override
    public boolean update(Candidate candidate, FileDto image) {
        var isNewFileEmpty = image.getContent().length == 0;
        if (isNewFileEmpty) {
            return candidateRepository.update(candidate);
        }
        /* если передан новый не пустой файл, то старый удаляем, а новый сохраняем */
        var oldFileId = candidate.getFileId();
        saveNewFile(candidate, image);
        var isUpdated = candidateRepository.update(candidate);
        fileService.deleteById(oldFileId);
        return isUpdated;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}
