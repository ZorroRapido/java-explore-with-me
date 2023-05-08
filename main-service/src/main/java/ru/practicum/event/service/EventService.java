package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.Sort;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.ReactionType;
import ru.practicum.event.model.State;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> getAllEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                     String rangeEnd, Boolean onlyAvailable, Sort sort, Integer from, Integer size,
                                     HttpServletRequest request);

    EventFullDto getEventById(Integer eventId, HttpServletRequest request);

    List<EventShortDto> getUserEvents(Integer userId, Integer from, Integer size);

    EventFullDto createEvent(NewEventDto newEventDto, Integer userId);

    EventFullDto getUserEventById(Integer userId, Integer eventId);

    EventFullDto updateEvent(UpdateEventUserRequest updateEventUserRequest, Integer userId, Integer eventId);

    List<ParticipationRequestDto> getRequests(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequest statusUpdateRequest,
                                                       Integer userId, Integer eventId);

    List<EventFullDto> searchForEvent(List<Integer> users, List<State> states, List<Integer> categories,
                                      String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(UpdateEventAdminRequest updateEventAdminRequest, Integer eventId);

    void addReaction(Integer userId, Integer eventId, ReactionType reactionType);

    void removeReaction(Integer userId, Integer eventId, ReactionType reactionType);
}
