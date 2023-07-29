package ru.job4j.dreamjob.repository;


import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Jasper", "Vampire. An expert with any weapon.", LocalDateTime.now()));
        save(new Candidate(0, "Alice", "Vampire also. Sees the future.", LocalDateTime.now()));
        save(new Candidate(0, "Jakob", "Werewolf. Can fix motorcycles.", LocalDateTime.now()));
        save(new Candidate(0, "Bella", "Became a vampire. Gave birth to a vampire.", LocalDateTime.now()));
        save(new Candidate(0, "Edward", "Tired of being a vampire. Until he met Bella.", LocalDateTime.now()));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.incrementAndGet());
        candidates.putIfAbsent(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(
                candidate.getId(),
                (id, oldCandidate) -> new Candidate(
                oldCandidate.getId(), candidate.getName(), candidate.getDescription(), candidate.getCreationDate())
        ) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
