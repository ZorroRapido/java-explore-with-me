package ru.practicum.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
