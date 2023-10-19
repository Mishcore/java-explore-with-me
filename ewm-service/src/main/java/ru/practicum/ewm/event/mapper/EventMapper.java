package ru.practicum.ewm.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.location.model.Location;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.rating.dao.VoteRepository;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

import static ru.practicum.ewm.Constants.TIME_FORMATTER;
import static ru.practicum.ewm.utility.EventManager.getEventRating;
import static ru.practicum.ewm.utility.EventManager.getEventViews;

@UtilityClass
public class EventMapper {

    public static Event toEvent(NewEventDto eventDto, User initiator, Category category, Location location) {
        return new Event(
                null,
                eventDto.getTitle(),
                eventDto.getAnnotation(),
                category,
                0,
                LocalDateTime.now(),
                eventDto.getDescription(),
                LocalDateTime.parse(eventDto.getEventDate(), TIME_FORMATTER),
                initiator,
                location,
                eventDto.getPaid() != null && eventDto.getPaid(),
                eventDto.getParticipantLimit() == null ? 0 : eventDto.getParticipantLimit(),
                null,
                eventDto.getRequestModeration() == null || eventDto.getRequestModeration(),
                State.PENDING
        );
    }

    public static EventShortDto toEventShortDto(Event event, StatsClient statsClient) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getCategory(),
                event.getConfirmedRequests(),
                event.getEventDate().format(TIME_FORMATTER),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                getEventViews(statsClient, event),
                null
        );
    }

    public static EventFullDto toEventFullDto(Event event, StatsClient statsClient, VoteRepository voteRepository) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getCategory(),
                event.getConfirmedRequests(),
                event.getCreatedOn().format(TIME_FORMATTER),
                event.getDescription(),
                event.getEventDate().format(TIME_FORMATTER),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn() == null ? null : event.getPublishedOn().format(TIME_FORMATTER),
                event.getRequestModeration(),
                event.getState(),
                getEventViews(statsClient, event),
                getEventRating(voteRepository, event)
        );
    }

    public static EventFullDto toNewEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getCategory(),
                0,
                null,
                event.getDescription(),
                event.getEventDate().format(TIME_FORMATTER),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                null,
                event.getRequestModeration(),
                State.PENDING,
                0L,
                null
        );
    }
}
