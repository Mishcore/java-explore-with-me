package ru.practicum.ewm.exception;

public class InvalidOperationException extends IllegalArgumentException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
