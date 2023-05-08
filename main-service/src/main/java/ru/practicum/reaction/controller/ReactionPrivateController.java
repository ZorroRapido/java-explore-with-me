package ru.practicum.reaction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.reaction.model.ReactionType;
import ru.practicum.reaction.service.ReactionService;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events/{eventId}")
@RequiredArgsConstructor
public class ReactionPrivateController {

    private final ReactionService reactionService;

    @PutMapping("/like")
    public ResponseEntity<Void> addLike(@PathVariable("userId") Integer userId,
                                        @PathVariable("eventId") Integer eventId) {
        log.info("Получен запрос на добавление лайка от пользователя с id={} на событие с id={}", userId, eventId);
        reactionService.addReaction(userId, eventId, ReactionType.LIKE);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/dislike")
    public ResponseEntity<Void> addDislike(@PathVariable("userId") Integer userId,
                                           @PathVariable("eventId") Integer eventId) {
        log.info("Получен запрос на добавление дизлайка от пользователя с id={} на событие с id={}", userId, eventId);
        reactionService.addReaction(userId, eventId, ReactionType.DISLIKE);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/like")
    public ResponseEntity<Void> removeLike(@PathVariable("userId") Integer userId,
                                           @PathVariable("eventId") Integer eventId) {
        log.info("Получен запрос на удаление лайка от пользователя с id={} на событие с id={}", userId, eventId);
        reactionService.removeReaction(userId, eventId, ReactionType.LIKE);
        return ResponseEntity.status(204).build();
    }

    @DeleteMapping("/dislike")
    public ResponseEntity<Void> removeDislike(@PathVariable("userId") Integer userId,
                                              @PathVariable("eventId") Integer eventId) {
        log.info("Получен запрос на удаление дизлайка от пользователя с id={} на событие с id={}", userId, eventId);
        reactionService.removeReaction(userId, eventId, ReactionType.DISLIKE);
        return ResponseEntity.status(204).build();
    }
}
