package ru.practicum.exception;

public class RequestAlreadyExistsException extends RuntimeException {

    public RequestAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
