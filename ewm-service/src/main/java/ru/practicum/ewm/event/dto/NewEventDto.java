package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.location.model.Location;
import ru.practicum.ewm.event.validator.NotTooSoon;

import javax.validation.constraints.*;

import static ru.practicum.ewm.Constants.DATE_STRING_VALIDATION_REGEX;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    private Integer category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @NotTooSoon(minHoursBefore = "2")
    @Pattern(regexp = DATE_STRING_VALIDATION_REGEX)
    private String eventDate;

    @NotNull
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;
}