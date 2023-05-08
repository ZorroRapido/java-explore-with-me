package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Reaction;
import ru.practicum.event.model.ReactionType;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Integer> {

    boolean existsByUserIdAndEventId(Integer userId, Integer eventId);

    Reaction findByUserIdAndEventIdAndReactionType(Integer userId, Integer eventId, ReactionType reactionType);

    Reaction findByUserIdAndEventId(Integer userId, Integer eventId);
}
