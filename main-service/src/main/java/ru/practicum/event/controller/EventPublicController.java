package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.Sort;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEvents(@RequestParam(value = "text", required = false) String text,
                                                            @RequestParam(value = "categories", required = false) List<Integer> categories,
                                                            @RequestParam(value = "paid", required = false) Boolean paid,
                                                            @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                                            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                                            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                            @RequestParam(value = "sort", required = false) Sort sort,
                                                            @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                            @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                            HttpServletRequest request) {
        log.info("Получен запрос на получение событий, удовлетворяющих фильтрам: text={}, categories={}, paid={}, " +
                        "rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}", text, categories,
                paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return ResponseEntity.ok().body(eventService.getAllEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable("id") Integer id, HttpServletRequest request) {
        log.info("Получен запрос на получение события с id={}", id);
        return ResponseEntity.ok().body(eventService.getEventById(id, request));
    }
}
