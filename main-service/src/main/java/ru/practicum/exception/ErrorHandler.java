package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.category.controller.CategoryAdminController;
import ru.practicum.category.controller.CategoryPublicController;
import ru.practicum.compilation.controller.CompilationAdminController;
import ru.practicum.compilation.controller.CompilationPublicController;
import ru.practicum.event.controller.EventAdminController;
import ru.practicum.event.controller.EventPrivateController;
import ru.practicum.event.controller.EventPublicController;
import ru.practicum.request.controller.RequestPrivateController;
import ru.practicum.user.controller.UserAdminController;

@Slf4j
@RestControllerAdvice(assignableTypes = {CategoryAdminController.class, CategoryPublicController.class,
        CompilationAdminController.class, CompilationPublicController.class, EventAdminController.class,
        EventPrivateController.class, EventPublicController.class, RequestPrivateController.class,
        UserAdminController.class})
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(final Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleCategoryNotFoundException(final CategoryNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(HttpStatus.NOT_FOUND, "Required object not found.", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleUserNotFoundException(final UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(HttpStatus.NOT_FOUND, "Required object not found.", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleCompilationNotFoundException(final CompilationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(HttpStatus.NOT_FOUND, "Required object not found.", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleRequestNotFoundException(final RequestNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(HttpStatus.NOT_FOUND, "Required object not found.", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleCategoryAlreadyExistsException(final CategoryAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(HttpStatus.CONFLICT, e.getCause().getMessage(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(final EmailAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(HttpStatus.CONFLICT, e.getCause().getMessage(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleRequestAlreadyExistsException(final RequestAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(HttpStatus.CONFLICT, e.getCause().getMessage(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleConditionNotMetException(final ConditionNotMetException e) {
        return ResponseEntity.status(e.getStatus())
                .body(new ApiError(e.getStatus(), "For the requested operation the conditions are not met.",
                        e.getMessage()));
    }
}
