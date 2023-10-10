package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalStartEndDatesException(final IllegalStartEndDatesException e) {
        log.error(e.getMessage());
        return new ApiError(
                e.getMessage(),
                "Невалидные данные",
                HttpStatus.BAD_REQUEST,
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList())
        );
    }
}
