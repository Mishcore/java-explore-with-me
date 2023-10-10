package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestServicePrivate {

    List<ParticipationRequestDto> getAllRequestsByUser(Long userId);

    ParticipationRequestDto addRequest(Long userId, Integer eventId);

    ParticipationRequestDto cancelRequest(Long userId, Integer requestId);
}