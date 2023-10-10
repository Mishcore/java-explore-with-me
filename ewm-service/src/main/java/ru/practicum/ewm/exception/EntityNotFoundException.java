package ru.practicum.ewm.exception;

public class EntityNotFoundException extends IllegalArgumentException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
