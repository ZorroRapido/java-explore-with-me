package ru.practicum.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Integer userId) {
        super(String.format("User with id=%d was not found", userId));
    }
}
