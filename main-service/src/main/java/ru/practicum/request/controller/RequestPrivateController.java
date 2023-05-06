package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestPrivateController {

    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable("userId") Integer userId) {
        log.info("Получен запрос на получение заявок на участие в чужих событиях для пользователя с id={}", userId);
        return ResponseEntity.ok().body(requestService.getUserRequests(userId));
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable("userId") Integer userId,
                                                                 @RequestParam("eventId") Integer eventId) {
        log.info("Получен запрос на создание новой заявки на участие в событии с id={} от пользователя с id={}",
                eventId, userId);
        return ResponseEntity.status(201).body(requestService.createRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable("userId") Integer userId,
                                                                 @PathVariable("requestId") Integer requestId) {
        log.info("Получен запрос на отмену своей заявки на участие с id={} от пользователя с id={}", requestId, userId);
        return ResponseEntity.ok().body(requestService.cancelRequest(userId, requestId));
    }
}
