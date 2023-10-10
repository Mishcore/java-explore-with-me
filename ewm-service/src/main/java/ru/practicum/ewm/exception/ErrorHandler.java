package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        log.error(e.getMessage());
        return new ApiError(
                e.getMessage(),
                "Невалидные данные",
                HttpStatus.BAD_REQUEST,
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException e) {
        log.error(e.getMessage());
        return new ApiError(
                e.getMessage(),
                "Сущность не найдена или недоступна",
                HttpStatus.NOT_FOUND,
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEmailAlreadyExistsException(final EmailAlreadyExistsException e) {
        log.error(e.getMessage());
        return new ApiError(
                e.getMessage(),
                "Email уже существует",
                HttpStatus.CONFLICT,
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return new ApiError(
                e.getMessage(),
                "Ошибка в запросе",
                HttpStatus.BAD_REQUEST,
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUnauthorizedAccessException(final UnauthorizedAccessException e) {
        log.error(e.getMessage());
        return new ApiError(
                e.getMessage(),
                "Неавторизованный доступ",
                HttpStatus.NOT_FOUND,
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleInvalidOperationException(final InvalidOperationException e) {
        log.error(e.getMessage());
        return new ApiError(
                e.getMessage(),
                "Недопустимая операция",
                HttpStatus.CONFLICT,
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.error(e.getMessage());
        return new ApiError(
                e.getMessage(),
                "Нарушение целостности данных",
                HttpStatus.CONFLICT,
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList())
        );
    }
}
