package ru.practicum.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.CategoryNotFoundException;
import ru.practicum.exception.CompilationNotFoundException;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.exception.RequestNotFoundException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsistencyService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final CompilationRepository compilationRepository;

    public void checkCategoryExistence(Integer catId) {
        if (!categoryRepository.existsById(catId)) {
            log.warn("Category with id={} was not found!", catId);
            throw new CategoryNotFoundException(catId);
        }
    }

    public void checkEventExistence(Integer eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.warn("Event with id={} was not found!", eventId);
            throw new EventNotFoundException(eventId);
        }
    }

    public void checkCompilationExistence(Integer compId) {
        if (!compilationRepository.existsById(compId)) {
            log.warn("Compilation with id={} was not found", compId);
            throw new CompilationNotFoundException(compId);
        }
    }

    public void checkUserExistence(Integer userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} was not found!", userId);
            throw new UserNotFoundException(userId);
        }
    }

    public void checkRequestExistence(Integer requestId) {
        if (!requestRepository.existsById(requestId)) {
            log.warn("Request with id={} was not found!", requestId);
            throw new RequestNotFoundException(requestId);
        }
    }
}
