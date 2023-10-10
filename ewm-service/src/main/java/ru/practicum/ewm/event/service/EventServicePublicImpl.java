package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.enums.Sort;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.exception.EntityNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.Constants.TIME_FORMATTER;
import static ru.practicum.ewm.utility.EntityFinder.findEventOrThrowException;
import static ru.practicum.ewm.utility.EventViewsManager.getEventViews;
import static ru.practicum.ewm.utility.EventViewsManager.saveEndpointHit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EventServicePublicImpl implements EventServicePublic {

    private final StatsClient statsClient;
    private final EventRepository eventRepository;

    @Override
    public List<EventShortDto> getEvents(String text, Integer[] categories, Boolean paid,
                                         String rangeStart, String rangeEnd, Boolean onlyAvailable, Sort sort,
                                         Integer from, Integer size, HttpServletRequest request) {
        QEvent qEvent = QEvent.event;
        BooleanExpression filteringCondition =
                buildCondition(qEvent, text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        List<Event> eventList =
                eventRepository.findAll(filteringCondition, PageRequest.of(from / size, size)).getContent();

        if (sort.equals(Sort.EVENT_DATE) && eventList.size() > 1) {
            eventList.sort(Comparator.comparing(Event::getEventDate));
        }
        List<EventShortDto> eventDtoList = eventList.stream()
                .map(event -> EventMapper.toEventShortDto(event, getEventViews(statsClient, event)))
                .collect(Collectors.toList());
        if (sort.equals(Sort.VIEWS) && eventList.size() > 1) {
            eventDtoList.sort(Comparator.comparingLong(EventShortDto::getViews));
        }
        log.info("Получен список событий: {}", eventList);
        saveEndpointHit(statsClient, request);
        log.info("Сервис статистики сохранил обращение к списку событий");
        return eventDtoList;
    }

    @Override
    public EventFullDto getEventById(Integer eventId, HttpServletRequest request) {
        Event event = findEventOrThrowException(eventRepository, eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityNotFoundException("Запрашиваемое событие не было опубликовано");
        }
        log.info("Получено событие ID: {}", eventId);
        saveEndpointHit(statsClient, request);
        log.info("Сервис статистики сохранил обращение к событию ID: {}", eventId);
        return EventMapper.toEventFullDto(event, getEventViews(statsClient, event));
    }

    private BooleanExpression buildCondition(QEvent event, String text, Integer[] categories, Boolean paid,
                                             String rangeStart, String rangeEnd, Boolean onlyAvailable) {
        BooleanExpression condition = event.state.eq(State.PUBLISHED);
        if (text != null) {
            condition = condition
                    .and(event.annotation.containsIgnoreCase(text)
                            .or(event.description.containsIgnoreCase(text)));
        }
        if (categories != null) {
            condition = condition.and(event.category.id.in(categories));
        }
        if (paid != null) {
            if (paid) {
                condition = condition.and(event.paid.isTrue());
            }
        }
        if (rangeStart != null) {
            condition = condition.and(event.eventDate.after(LocalDateTime.parse(rangeStart, TIME_FORMATTER)));
        }
        if (rangeEnd != null) {
            condition = condition.and(event.eventDate.before(LocalDateTime.parse(rangeEnd, TIME_FORMATTER)));
        }
        if (rangeStart == null && rangeEnd == null) {
            condition = condition.and(event.eventDate.after(LocalDateTime.now()));
        }
        if (onlyAvailable) {
            condition = condition.and(event.confirmedRequests.lt(event.participantLimit));
        }
        return condition;
    }
}