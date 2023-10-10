package ru.practicum.ewm.exception;

public class UnauthorizedAccessException extends IllegalArgumentException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
