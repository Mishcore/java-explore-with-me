package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventServicePrivate {

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEventById(Long userId, Integer eventId);

    EventFullDto updateUserEvent(Long userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getUserParticipationRequests(Long userId, Integer eventId);

    EventRequestStatusUpdateResult updateUserParticipationRequests(Long userId, Integer eventId,
                                                                   EventRequestStatusUpdateRequest updateRequest);

    EventFullDto addVote(Long userId, Integer eventId, Boolean liked);

    EventFullDto removeVote(Long userId, Integer eventId);
}
