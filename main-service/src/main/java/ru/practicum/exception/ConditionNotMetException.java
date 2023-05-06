package ru.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ConditionNotMetException extends RuntimeException {

    private final HttpStatus status;

    public ConditionNotMetException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

    public ConditionNotMetException(String msg) {
        super(msg);
        this.status = HttpStatus.CONFLICT;
    }
}
