package ru.practicum.exception;

public class CompilationNotFoundException extends RuntimeException {

    public CompilationNotFoundException(Integer compId) {
        super(String.format("Compilation with id=%d was not found", compId));
    }
}
