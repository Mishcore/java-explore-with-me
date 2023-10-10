package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestServicePrivate;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/users/{userId}/requests")
public class RequestControllerPrivate {

    private final RequestServicePrivate requestServicePrivate;

    @GetMapping
    public List<ParticipationRequestDto> getAllRequestsByUser(@PathVariable @Positive Long userId) {
        log.info("Принят запрос на получение списка заявок на участие в событиях пользователя ID: {}", userId);
        return requestServicePrivate.getAllRequestsByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable @Positive Long userId,
                                              @RequestParam @Positive Integer eventId) {
        log.info("Принят запрос на создание заявки на участие в событии ID: {} от пользователя ID: {}", eventId, userId);
        return requestServicePrivate.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Integer requestId) {
        log.info("Принят запрос на отмену заявки ID: {} пользователя ID: {}", requestId, userId);
        return requestServicePrivate.cancelRequest(userId, requestId);
    }

}