package ru.practicum.exception;

public class CategoryAlreadyExistsException extends RuntimeException {

    public CategoryAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
