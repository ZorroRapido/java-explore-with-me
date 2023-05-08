package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHit;
import ru.practicum.StatsClient;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.EventMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.Sort;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ConditionNotMetException;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.event.dto.Sort.EVENT_DATE;
import static ru.practicum.event.dto.Sort.VIEWS;
import static ru.practicum.event.model.State.CANCELED;
import static ru.practicum.event.model.State.PENDING;
import static ru.practicum.event.model.State.PUBLISHED;
import static ru.practicum.event.model.StateAction.PUBLISH_EVENT;
import static ru.practicum.event.model.StateAction.REJECT_EVENT;
import static ru.practicum.event.model.StateAction.SEND_TO_REVIEW;
import static ru.practicum.request.model.Status.CONFIRMED;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final RequestMapper requestMapper;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAllEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                            String rangeEnd, Boolean onlyAvailable, Sort sort, Integer from, Integer size,
                                            HttpServletRequest request) {
        statsClient.saveEndpointHit(new EndpointHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr()));

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (text != null) {
            predicates.add(cb.or(cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%")));
        }

        if (categories != null) {
            predicates.add(root.get("category").in(categories));
        }

        if (paid != null) {
            predicates.add(cb.equal(root.get("paid"), paid));
        }

        if (rangeStart != null && rangeEnd != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), LocalDateTime.parse(rangeStart, FORMATTER)));
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), LocalDateTime.parse(rangeEnd, FORMATTER)));
        } else {
            predicates.add(cb.greaterThan(root.get("eventDate"), LocalDateTime.now()));
        }

        if (onlyAvailable) {
            predicates.add(cb.lessThan(root.get("confirmedRequests"), root.get("participationLimit")));
        }

        predicates.add(cb.equal(root.get("state"), PUBLISHED));

        if (EVENT_DATE.equals(sort)) {
            query.select(root).where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(root.get("eventDate")));
        } else if (VIEWS.equals(sort)) {
            query.select(root).where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(root.get("views")));
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);

        TypedQuery<Event> typedQuery = entityManager.createQuery(query);
        Page<Event> events = new PageImpl<>(typedQuery.getResultList(), pageRequest, size);

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventById(Integer eventId, HttpServletRequest request) {
        statsClient.saveEndpointHit(new EndpointHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr()));

        checkEventExistence(eventId);

        return eventMapper.toEventFullDto(eventRepository.getReferenceById(eventId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getUserEvents(Integer userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} was not found!", userId);
            throw new UserNotFoundException(userId);
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return eventRepository.findAllByInitiatorId(userId, pageRequest).stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto createEvent(NewEventDto newEventDto, Integer userId) {
        checkUserExistence(userId);

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Conditions are not met while creating new event! field: eventDate, value={}", newEventDto.getEventDate());
            throw new ConditionNotMetException(String.format("Field: eventDate. Error: должно содержать дату, которая" +
                    " еще не наступила. Value: %s", newEventDto.getEventDate()));
        }

        locationRepository.saveAndFlush(newEventDto.getLocation());

        Event event = eventMapper.toEvent(newEventDto);
        event.setConfirmedRequests(0);
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(userRepository.getReferenceById(userId));
        event.setPaid(newEventDto.getPaid() == null ? null : newEventDto.getPaid());
        event.setViews(0);
        event.setState(State.PENDING);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getUserEventById(Integer userId, Integer eventId) {
        checkUserExistence(userId);
        checkEventExistence(eventId);

        return eventMapper.toEventFullDto(eventRepository.getReferenceById(eventId));
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(UpdateEventUserRequest updateEventUserRequest, Integer userId, Integer eventId) {
        checkUserExistence(userId);
        checkEventExistence(eventId);

        Event event = eventRepository.getReferenceById(eventId);

        if (!CANCELED.equals(event.getState()) && !PENDING.equals(event.getState())) {
            log.warn("Conditions are not met while updating event with id={}! field: eventDate, value={}!", eventId,
                    event.getState());
            throw new ConditionNotMetException("Only pending or canceled events can be changed");
        }

        if (updateEventUserRequest.getEventDate() != null) {
            if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                log.warn("Conditions are not met while updating event with id={}! field: eventDate, value={}", eventId,
                        updateEventUserRequest.getEventDate());
                throw new ConditionNotMetException(String.format("Field: eventDate. Error: должно содержать дату, " +
                        "которая еще не наступила. Value: %s", updateEventUserRequest.getEventDate()));
            } else {
                event.setEventDate(updateEventUserRequest.getEventDate());
            }
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(event.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryRepository.getReferenceById(updateEventUserRequest.getCategory()));
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(updateEventUserRequest.getLocation());
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (PUBLISH_EVENT.equals(updateEventUserRequest.getStateAction())) {
            event.setState(State.PUBLISHED);
        } else if (REJECT_EVENT.equals(updateEventUserRequest.getStateAction())) {
            event.setState(State.REJECTED);
        } else if (SEND_TO_REVIEW.equals(updateEventUserRequest.getStateAction())) {
            event.setState(State.PENDING);
        } else if (StateAction.CANCEL_REVIEW.equals(updateEventUserRequest.getStateAction())) {
            event.setState(State.CANCELED);
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequests(Integer userId, Integer eventId) {
        checkUserExistence(userId);
        checkEventExistence(eventId);

        return requestRepository.findAllByEvent(eventRepository.getReferenceById(eventId)).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequest statusUpdateRequest,
                                                              Integer userId, Integer eventId) {
        checkUserExistence(userId);
        checkEventExistence(eventId);

        Event event = eventRepository.getReferenceById(eventId);
        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            return new EventRequestStatusUpdateResult();
        }

        if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())
                && CONFIRMED.equals(statusUpdateRequest.getStatus())
                && statusUpdateRequest.getRequestIds().size() > 0) {
            log.warn("The participant limit has been reached for event with id={}!", eventId);
            throw new ConditionNotMetException("The participant limit has been reached.");
        }

        if (Status.REJECTED.equals(statusUpdateRequest.getStatus())) {
            statusUpdateRequest.getRequestIds().forEach(id -> {
                ParticipationRequest request = requestRepository.getReferenceById(id);

                if (Status.CONFIRMED.equals(request.getStatus())) {
                    log.warn("Already confirmed request can not be rejected!");
                    throw new ConditionNotMetException("Already confirmed request can not be rejected");
                }

                request.setStatus(Status.REJECTED);
                rejectedRequests.add(request);
                requestRepository.save(request);
            });
        } else if (Status.CONFIRMED.equals(statusUpdateRequest.getStatus())) {
            statusUpdateRequest.getRequestIds().forEach(id -> {
                ParticipationRequest request = requestRepository.getReferenceById(id);

                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    if (!Status.PENDING.equals(request.getStatus())) {
                        log.warn("Only requests with PENDING status can be confirmed!");
                        throw new ConditionNotMetException("Request must have status PENDING", HttpStatus.BAD_REQUEST);
                    }

                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    request.setStatus(Status.CONFIRMED);
                    confirmedRequests.add(request);
                    requestRepository.save(request);
                } else {
                    request.setStatus(Status.REJECTED);
                    rejectedRequests.add(request);
                    requestRepository.save(request);
                }
            });
        }

        return new EventRequestStatusUpdateResult(
                confirmedRequests.stream()
                        .map(requestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()),
                rejectedRequests.stream()
                        .map(requestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList())
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> searchForEvent(List<Integer> users, List<State> states, List<Integer> categories,
                                             String rangeStart, String rangeEnd, Integer from, Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (users != null) {
            predicates.add(root.get("initiator").in(users));
        }

        if (states != null) {
            predicates.add(root.get("state").in(states));
        }

        if (categories != null) {
            predicates.add(root.get("category").in(categories));
        }

        if (rangeStart != null && rangeEnd != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), LocalDateTime.parse(rangeStart, FORMATTER)));
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), LocalDateTime.parse(rangeEnd, FORMATTER)));
        } else {
            predicates.add(cb.greaterThan(root.get("eventDate"), LocalDateTime.now()));
        }

        query.select(root).where(predicates.toArray(new Predicate[]{}));

        PageRequest pageRequest = PageRequest.of(from / size, size);

        TypedQuery<Event> typedQuery = entityManager.createQuery(query);
        Page<Event> events = new PageImpl<>(typedQuery.getResultList(), pageRequest, size);

        return events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(UpdateEventAdminRequest updateEventAdminRequest, Integer eventId) {
        checkEventExistence(eventId);
        Event event = eventRepository.getReferenceById(eventId);

        if (updateEventAdminRequest.getEventDate() != null) {
            if (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                log.warn("Conditions are not met while creating new event! field: eventDate, value={}",
                        updateEventAdminRequest.getEventDate());
                throw new ConditionNotMetException(String.format("Field: eventDate. Error: должно содержать дату, " +
                        "которая еще не наступила. Value: %s", updateEventAdminRequest.getEventDate()));
            } else {
                event.setEventDate(updateEventAdminRequest.getEventDate());
            }
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryRepository.getReferenceById(updateEventAdminRequest.getCategory()));
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(updateEventAdminRequest.getLocation());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (PUBLISH_EVENT.equals(updateEventAdminRequest.getStateAction())) {
            if (State.PENDING.equals(event.getState())) {
                event.setState(State.PUBLISHED);
            } else {
                log.warn("Only events with PENDING state can be published! Value: {}", event.getState());
                throw new ConditionNotMetException("Only events with PENDING state can be published");
            }
        } else if (REJECT_EVENT.equals((updateEventAdminRequest.getStateAction()))) {
            if (State.PENDING.equals(event.getState())) {
                event.setState(State.CANCELED);
            } else {
                log.warn("Only events with PENDING state can be rejected! Value: {}", event.getState());
                throw new ConditionNotMetException("Only events with PENDING state can be rejected");
            }
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
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
}
