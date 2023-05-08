package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getUserEvents(@PathVariable("userId") Integer userId,
                                                             @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение событий, добавленных пользователем с id={}: from={}, size={}", userId,
                from, size);
        return ResponseEntity.ok().body(eventService.getUserEvents(userId, from, size));
    }

    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@Valid @RequestBody NewEventDto newEventDto,
                                                    @PathVariable("userId") Integer userId) {
        log.info("Получен запрос на создание нового события от пользователя с id={}: newEventDto={}", userId, newEventDto);
        return ResponseEntity.status(201).body(eventService.createEvent(newEventDto, userId));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getUserEventById(@PathVariable("userId") Integer userId,
                                                         @PathVariable("eventId") Integer eventId) {
        log.info("Получен запрос на получение полной информации о событии с id={}, добавленном пользователем с id={}",
                eventId, userId);
        return ResponseEntity.ok().body(eventService.getUserEventById(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@RequestBody UpdateEventUserRequest updateEventUserRequest,
                                                    @PathVariable("userId") Integer userId,
                                                    @PathVariable("eventId") Integer eventId) {
        log.info("Получен запрос на обновление события с id={}, добавленного пользователем с id={}: updateEventUserRequest={}",
                eventId, userId, updateEventUserRequest);
        return ResponseEntity.ok().body(eventService.updateEvent(updateEventUserRequest, userId, eventId));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable("userId") Integer userId,
                                                                     @PathVariable("eventId") Integer eventId) {
        log.info("Получен запрос на получение заявок на участие в событии с id={}, добавленного пользователем с id={}",
                eventId, userId);
        return ResponseEntity.ok().body(eventService.getRequests(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(@RequestBody EventRequestStatusUpdateRequest statusUpdateRequest,
                                                                              @PathVariable("userId") Integer userId,
                                                                              @PathVariable("eventId") Integer eventId) {
        log.info("Получен запрос на изменение статуса заявок на участие в событии с id={}, добавленном пользователем " +
                "с id={}: statusUpdateRequest={}", eventId, userId, statusUpdateRequest);
        return ResponseEntity.ok().body(eventService.updateRequestStatus(statusUpdateRequest, userId, eventId));
    }
}
