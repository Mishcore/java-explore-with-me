package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.enums.StateActionUser;
import ru.practicum.ewm.event.location.model.Location;
import ru.practicum.ewm.event.validator.NotTooSoon;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static ru.practicum.ewm.Constants.DATE_STRING_VALIDATION_REGEX;
import static ru.practicum.ewm.Constants.STRING_VALIDATION_REGEX;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {

    @Pattern(regexp = STRING_VALIDATION_REGEX)
    @Size(min = 3, max = 120)
    private String title;

    @Pattern(regexp = STRING_VALIDATION_REGEX)
    @Size(min = 20, max = 2000)
    private String annotation;

    private Integer category;

    @Pattern(regexp = STRING_VALIDATION_REGEX)
    @Size(min = 20, max = 7000)
    private String description;

    @NotTooSoon(minHoursBefore = "2")
    @Pattern(regexp = DATE_STRING_VALIDATION_REGEX)
    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private StateActionUser stateAction;
}