package ru.practicum.exception;

public class RequestNotFoundException extends RuntimeException {

    public RequestNotFoundException(Integer requestId) {
        super(String.format("Request with id=%d was not found", requestId));
    }
}
