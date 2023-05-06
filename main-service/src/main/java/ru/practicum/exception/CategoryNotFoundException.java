package ru.practicum.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Integer catId) {
        super(String.format("Category with id=%d was not found", catId));
    }
}
