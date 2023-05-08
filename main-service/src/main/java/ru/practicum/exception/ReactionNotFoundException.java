package ru.practicum.exception;

import ru.practicum.reaction.model.ReactionType;

public class ReactionNotFoundException extends RuntimeException {

    public ReactionNotFoundException(Integer userId, Integer eventId, ReactionType type) {
        super(String.format("Reaction with userId=%d, eventId=%d and type=%s was not found", userId, eventId, type));
    }
}
