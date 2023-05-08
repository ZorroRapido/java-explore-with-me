package ru.practicum.reaction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.service.ConsistencyService;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.reaction.repository.ReactionRepository;
import ru.practicum.exception.ConditionNotMetException;
import ru.practicum.exception.ReactionNotFoundException;
import ru.practicum.reaction.model.Reaction;
import ru.practicum.reaction.model.ReactionType;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final ReactionRepository reactionRepository;
    private final ConsistencyService consistencyService;

    @Transactional
    @Override
    public void addReaction(Integer userId, Integer eventId, ReactionType reactionType) {
        consistencyService.checkUserExistence(userId);
        consistencyService.checkEventExistence(eventId);

        if (reactionRepository.existsByUserIdAndEventId(userId, eventId)) {
            Reaction existingReaction = reactionRepository.findByUserIdAndEventId(userId, eventId);

            if (existingReaction.getReactionType().equals(reactionType)) {
                log.warn("User with id={} has already added {} for event with id={}!", userId, existingReaction.getReactionType(),
                        eventId);
                throw new ConditionNotMetException(String.format("User has already added %s for this event!",
                        existingReaction.getReactionType()));
            } else {
                log.warn("User with id={} has already added another reaction ({}) for event with id={}!", userId,
                        existingReaction.getReactionType(), eventId);
                throw new ConditionNotMetException(String.format("User has already added another reaction (%s) for " +
                        "this event!", existingReaction.getReactionType()));
            }
        }

        UserDto userDto = userRepository.getReferenceById(userId);
        Event event = eventRepository.getReferenceById(eventId);
        ParticipationRequest request = requestRepository.findOneByRequesterAndEvent(userDto, event);

        if (request != null && Status.CONFIRMED.equals(request.getStatus())
                && event.getEventDate().isBefore(LocalDateTime.now())) {
            Reaction reaction = new Reaction(userId, eventId, reactionType);
            reactionRepository.save(reaction);

            if (ReactionType.LIKE.equals(reactionType)) {
                event.setRating(event.getRating() + 1);
                userDto.setRating(userDto.getRating() + 1);
            } else if (ReactionType.DISLIKE.equals(reactionType)) {
                event.setRating(event.getRating() - 1);
                userDto.setRating(userDto.getRating() - 1);
            }

            eventRepository.save(event);
            userRepository.save(userDto);
        } else {
            log.warn("Users can not add reactions about events that they haven't visited! (user_id = {}, event_id={})",
                    userId, eventId);
            throw new ConditionNotMetException("Only users who have visited the event can add reactions about it!");
        }
    }

    @Transactional
    @Override
    public void removeReaction(Integer userId, Integer eventId, ReactionType reactionType) {
        consistencyService.checkUserExistence(userId);
        consistencyService.checkEventExistence(eventId);

        UserDto userDto = userRepository.getReferenceById(userId);
        Event event = eventRepository.getReferenceById(eventId);
        Reaction existingReaction = reactionRepository.findByUserIdAndEventIdAndReactionType(userId, eventId,
                reactionType);

        if (existingReaction != null) {
            reactionRepository.deleteById(existingReaction.getId());

            if (ReactionType.LIKE.equals(reactionType)) {
                userDto.setRating(userDto.getRating() - 1);
                event.setRating(event.getRating() - 1);
            } else if (ReactionType.DISLIKE.equals(reactionType)) {
                userDto.setRating(userDto.getRating() + 1);
                event.setRating(event.getRating() + 1);
            }

            eventRepository.save(event);
            userRepository.save(userDto);
        } else {
            log.warn("Reaction with type '{}' was not found for user with id={} and event with id={}!", reactionType,
                    userId, eventId);
            throw new ReactionNotFoundException(userId, eventId, reactionType);
        }
    }
}
