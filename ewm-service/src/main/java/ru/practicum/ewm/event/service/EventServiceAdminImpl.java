package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.enums.StateActionAdmin;
import ru.practicum.ewm.event.location.dao.LocationRepository;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.exception.InvalidOperationException;
import ru.practicum.ewm.rating.dao.VoteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.Constants.TIME_FORMATTER;
import static ru.practicum.ewm.utility.EntityFinder.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EventServiceAdminImpl implements EventServiceAdmin {

    private final StatsClient statsClient;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final VoteRepository voteRepository;

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEvents(Long[] users, State[] states, Integer[] categories,
                                        String rangeStart, String rangeEnd,
                                        Integer from, Integer size) {
        QEvent qEvent = QEvent.event;
        BooleanExpression filteringCondition =
                buildCondition(qEvent, users, states, categories, rangeStart, rangeEnd);
        List<Event> eventList =
                eventRepository.findAll(filteringCondition, PageRequest.of(from / size, size)).getContent();
        log.info("Получен список событий: {}", eventList);
        return eventList.stream()
                .map((event) -> EventMapper.toEventFullDto(event, statsClient, voteRepository))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto patchEventByAdmin(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = findEventOrThrowException(eventRepository, eventId);
        permitUpdating(event, updateEventAdminRequest);
        patchEvent(event, updateEventAdminRequest);
        eventRepository.saveAndFlush(event);
        log.info("Внесены изменения в событие ID: {} пользователя ID: {}", eventId, event.getInitiator().getId());
        return EventMapper.toEventFullDto(event, statsClient, voteRepository);
    }

    private BooleanExpression buildCondition(QEvent event, Long[] users, State[] states, Integer[] categories,
                                             String rangeStart, String rangeEnd) {
        BooleanExpression condition = event.isNotNull();
        if (users != null) {
            condition = condition.and(event.initiator.id.in(users));
        }
        if (states != null) {
            condition = condition.and(event.state.in(states));
        }
        if (categories != null) {
            condition = condition.and(event.category.id.in(categories));
        }
        if (rangeStart != null) {
            condition = condition.and(event.eventDate.after(LocalDateTime.parse(rangeStart, TIME_FORMATTER)));
        }
        if (rangeEnd != null) {
            condition = condition.and(event.eventDate.before(LocalDateTime.parse(rangeEnd, TIME_FORMATTER)));
        }
        return condition;
    }

    private void permitUpdating(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        if (event.getState() != null && event.getState().equals(State.PUBLISHED) &&
                event.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new InvalidOperationException(
                    "Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }
        if (updateEventAdminRequest.getStateAction() != null &&
                updateEventAdminRequest.getStateAction().equals(StateActionAdmin.PUBLISH_EVENT) &&
                !event.getState().equals(State.PENDING)) {
            throw new InvalidOperationException(
                    "Событие можно публиковать, только если оно в состоянии ожидания публикации");
        }
        if (updateEventAdminRequest.getStateAction() != null &&
                updateEventAdminRequest.getStateAction().equals(StateActionAdmin.REJECT_EVENT) &&
                event.getState().equals(State.PUBLISHED)) {
            throw new InvalidOperationException(
                    "Событие можно отклонить, только если оно еще не опубликовано");
        }
    }

    private Event patchEvent(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(findCategoryOrThrowException(categoryRepository, updateEventAdminRequest.getCategory()));
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEventAdminRequest.getEventDate(), TIME_FORMATTER));
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(findLocationOrSaveNew(locationRepository, updateEventAdminRequest.getLocation()));
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            StateActionAdmin stateAdmin = updateEventAdminRequest.getStateAction();
            if (stateAdmin.equals(StateActionAdmin.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (stateAdmin.equals(StateActionAdmin.REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }
        return event;
    }
}
