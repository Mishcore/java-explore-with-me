package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventServicePrivate;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/users/{userId}/events")
public class EventControllerPrivate {

    private final EventServicePrivate eventServicePrivate;

    @GetMapping
    public List<EventShortDto> getEventsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Принят запрос на получение списка событий пользователя ID: {}", userId);
        return eventServicePrivate.getEventsByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable @Positive Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Принят запрос на добавление нового события от пользователя ID: {}", userId);
        return eventServicePrivate.addEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(@PathVariable @Positive Long userId,
                                         @PathVariable @Positive Integer eventId) {
        log.info("Принят запрос на получение события ID: {}, добавленного пользователем ID: {}", eventId, userId);
        return eventServicePrivate.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchUserEvent(@PathVariable @Positive Long userId,
                                       @PathVariable @Positive Integer eventId,
                                       @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Принят запрос на редактирование события ID: {}, добавленного пользователем ID: {}", eventId, userId);
        return eventServicePrivate.updateUserEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUserParticipationRequests(@PathVariable @Positive Long userId,
                                                                      @PathVariable @Positive Integer eventId) {
        log.info("Принят запрос на получение информации о заявках на участие в событии ID: {} пользователя ID: {}",
                eventId, userId);
        return eventServicePrivate.getUserParticipationRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateUserParticipationRequests(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Integer eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Принят запрос на изменение статуса заявок на участие в событии ID: {} пользователя ID: {}",
                eventId, userId);
        return eventServicePrivate.updateUserParticipationRequests(userId, eventId, updateRequest);
    }

    @PostMapping("/{eventId}/like")
    public EventFullDto voteEvent(@PathVariable @Positive Long userId,
                                  @PathVariable @Positive Integer eventId,
                                  @RequestParam Boolean liked) {
        log.info("Принят запрос на добавление оценки события ID: {} от пользователя ID: {}", eventId, userId);
        return eventServicePrivate.addVote(userId, eventId, liked);
    }

    @DeleteMapping("/{eventId}/like")
    public EventFullDto removeVote(@PathVariable @Positive Long userId,
                                  @PathVariable @Positive Integer eventId) {
        log.info("Принят запрос на удаление оценки события ID: {} от пользователя ID: {}", eventId, userId);
        return eventServicePrivate.removeVote(userId, eventId);
    }
}