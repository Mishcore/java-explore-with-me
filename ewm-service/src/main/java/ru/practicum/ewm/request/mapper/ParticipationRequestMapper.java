package ru.practicum.ewm.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.ParticipationRequest;

import static ru.practicum.ewm.Constants.TIME_FORMATTER;

@UtilityClass
public class ParticipationRequestMapper {
    public static ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated().format(TIME_FORMATTER),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }
}
