package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConditionNotMetException;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.exception.RequestAlreadyExistsException;
import ru.practicum.exception.RequestNotFoundException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.event.model.State.PUBLISHED;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getUserRequests(Integer userId) {
        checkUserExistence(userId);

        return requestRepository.findAllByRequester(userRepository.getReferenceById(userId)).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Integer userId, Integer eventId) {
        checkUserExistence(userId);
        checkEventExistence(eventId);

        Event event = eventRepository.getReferenceById(eventId);
        ParticipationRequest participationRequest = new ParticipationRequest(event,
                userRepository.getReferenceById(userId));

        if (event.getInitiator().getId().equals(userId)) {
            log.warn("Conditions are not met while creating new request! field: initiator, value={}", userId);
            throw new ConditionNotMetException(String.format("Field: initiator. Error: нельзя добавить запрос на " +
                    "участие в своём событии. Value: %d", userId));
        }

        if (!PUBLISHED.equals(event.getState())) {
            log.warn("Conditions are not met while creating new request! field: state, value={}", userId);
            throw new ConditionNotMetException(String.format("Field: state. Error: нельзя участвовать в " +
                    "неопубликованном событии. Value: %s", event.getState()));
        }

        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            log.warn("Conditions are not met while creating new request! field: participant_limit, value={}", userId);
            throw new ConditionNotMetException(String.format("Field: participant_limit. Error: достигнут лимит " +
                    "запросов на участие. Value: %d", event.getParticipantLimit()));
        }

        if (requestRepository.findAllByRequesterAndEvent(userRepository.getReferenceById(userId),
                eventRepository.getReferenceById(eventId)).size() > 0) {
            log.warn("User with id={} has already submitted request for event with id={}!", userId, eventId);
            throw new ConditionNotMetException(String.format("Field: userId. Error: пользователь уже добавил запрос " +
                    "на участие в событии с id=%d. Value: %d", eventId, userId));
        }

        if (!event.getRequestModeration()) {
            participationRequest.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        participationRequest.setCreated(LocalDateTime.now());
        eventRepository.saveAndFlush(event);

        try {
            return requestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
        } catch (DataIntegrityViolationException e) {
            log.warn("Request with eventId={} and requesterId={} already exists!", eventId, userId);
            throw new RequestAlreadyExistsException(e.getMessage(), e.getCause());
        }
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Integer userId, Integer requestId) {
        checkRequestExistence(requestId);

        ParticipationRequest request = requestRepository.getReferenceById(requestId);
        request.setStatus(Status.CANCELED);

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private void checkUserExistence(Integer userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} was not found!", userId);
            throw new UserNotFoundException(userId);
        }
    }

    private void checkEventExistence(Integer eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.warn("Event with id={} was not found!", eventId);
            throw new EventNotFoundException(eventId);
        }
    }

    private void checkRequestExistence(Integer requestId) {
        if (!requestRepository.existsById(requestId)) {
            log.warn("Request with id={} was not found!", requestId);
            throw new RequestNotFoundException(requestId);
        }
    }
}
