package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.service.EventServiceAdmin;
import ru.practicum.ewm.event.validator.PositiveInArray;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/events")
public class EventControllerAdmin {

    private final EventServiceAdmin eventServiceAdmin;

    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) @PositiveInArray Long[] users,
            @RequestParam(required = false) State[] states,
            @RequestParam(required = false) @PositiveInArray Integer[] categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Принят запрос на получение списка событий с фильтрацией по заданным параметрам");
        return eventServiceAdmin.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@PathVariable @Positive Integer eventId,
                                   @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Принят запрос на редактирование события ID: {}", eventId);
        return eventServiceAdmin.patchEventByAdmin(eventId, updateEventAdminRequest);
    }
}
