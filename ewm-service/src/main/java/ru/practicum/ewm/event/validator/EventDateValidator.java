package ru.practicum.ewm.event.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventDateValidator implements ConstraintValidator<NotTooSoon, String> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Long minHoursBefore;

    @Override
    public void initialize(NotTooSoon constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        minHoursBefore = Long.parseLong(constraintAnnotation.minHoursBefore());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        LocalDateTime eventDate = LocalDateTime.parse(value, formatter);
        return eventDate.isAfter(LocalDateTime.now().plusHours(minHoursBefore));
    }
}