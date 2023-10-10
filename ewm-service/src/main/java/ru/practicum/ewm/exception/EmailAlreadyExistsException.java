package ru.practicum.ewm.exception;

public class EmailAlreadyExistsException extends IllegalArgumentException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
