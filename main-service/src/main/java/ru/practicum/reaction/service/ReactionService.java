package ru.practicum.reaction.service;

import ru.practicum.reaction.model.ReactionType;

public interface ReactionService {

    void addReaction(Integer userId, Integer eventId, ReactionType reactionType);

    void removeReaction(Integer userId, Integer eventId, ReactionType reactionType);
}
