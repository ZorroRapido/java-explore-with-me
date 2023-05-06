package ru.practicum.exception;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(Integer eventId) {
        super(String.format("Event with id=%d was not found", eventId));
    }
}
