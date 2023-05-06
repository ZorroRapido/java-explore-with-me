package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> searchForEvents(@RequestParam(value = "users", required = false) List<Integer> users,
                                                              @RequestParam(value = "states", required = false) List<State> states,
                                                              @RequestParam(value = "categories", required = false) List<Integer> categories,
                                                              @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                                              @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на поиск событий, удовлетворяющих фильтрам: users={}, states={}, categories={}, " +
                "rangeStart={}, rangeEnd={}, from={}, size={}", users, states, categories, rangeStart, rangeEnd, from, size);
        return ResponseEntity.ok().body(eventService.searchForEvent(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                                                    @PathVariable("eventId") Integer eventId) {
        log.info("Получен запрос на обновление события с id={}: updateEventAdminRequest={}", eventId, updateEventAdminRequest);
        return ResponseEntity.ok().body(eventService.updateEvent(updateEventAdminRequest, eventId));
    }
}
