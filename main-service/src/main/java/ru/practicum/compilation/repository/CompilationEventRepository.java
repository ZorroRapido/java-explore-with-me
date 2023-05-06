package ru.practicum.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.CompilationEvent;

import java.util.List;

@Repository
public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Integer> {

    List<CompilationEvent> findByCompilationId(Integer compilationId);

    void deleteByCompilationIdAndEventId(Integer compilationId, Integer eventId);
}
