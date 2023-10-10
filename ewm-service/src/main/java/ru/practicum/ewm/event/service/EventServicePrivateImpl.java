package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.enums.StateActionUser;
import ru.practicum.ewm.event.location.dao.LocationRepository;
import ru.practicum.ewm.event.location.model.Location;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.InvalidOperationException;
import ru.practicum.ewm.exception.UnauthorizedAccessException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.enums.Status;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.Constants.TIME_FORMATTER;
import static ru.practicum.ewm.utility.EntityFinder.*;
import static ru.practicum.ewm.utility.EventViewsManager.getEventViews;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EventServicePrivateImpl implements EventServicePrivate {

    private final StatsClient statsClient;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        findUserOrThrowException(userRepository, userId);
        List<Event> eventList = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        log.info("Получен список событий пользователя ID: {}", userId);
        return eventList.stream()
                .map(event -> EventMapper.toEventShortDto(event, getEventViews(statsClient, event)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        User initiator = findUserOrThrowException(userRepository, userId);
        Category category = findCategoryOrThrowException(categoryRepository, newEventDto.getCategory());
        Location location = findLocationOrSaveNew(locationRepository, newEventDto.getLocation());
        Event event = eventRepository.save(EventMapper.toEvent(newEventDto, initiator, category, location));
        log.info("Добавлено новое событие ID: {}", event.getId());
        return EventMapper.toEventFullDto(event, 0L);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getUserEventById(Long userId, Integer eventId) {
        findUserOrThrowException(userRepository, userId);
        Event event = findEventOrThrowException(eventRepository, eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Пользователь не является инициатором события");
        }
        log.info("Получено событие ID: {} пользователя ID: {}", eventId, userId);
        return EventMapper.toEventFullDto(event, getEventViews(statsClient, event));
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest) {
        User initiator = findUserOrThrowException(userRepository, userId);
        Event event = findEventOrThrowException(eventRepository, eventId);
        permitEventUpdating(initiator, event, updateEventUserRequest);
        patchEvent(event, updateEventUserRequest);
        eventRepository.saveAndFlush(event);
        log.info("Внесены изменения в событие ID: {} пользователя ID: {}", eventId, userId);
        return EventMapper.toEventFullDto(event, getEventViews(statsClient, event));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getUserParticipationRequests(Long userId, Integer eventId) {
        User initiator = findUserOrThrowException(userRepository, userId);
        Event event = findEventOrThrowException(eventRepository, eventId);
        if (!event.getInitiator().getId().equals(initiator.getId())) {
            throw new InvalidOperationException("Получить список заявок на событие может только инициатор события");
        }
        List<ParticipationRequest> requestList = requestRepository.findAllByEventId(eventId);
        log.info("Получен список заявок на участие в событии ID: {} пользователя ID: {}", eventId, userId);
        return requestList.stream()
                .map(ParticipationRequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateUserParticipationRequests(Long userId, Integer eventId,
                                                                          EventRequestStatusUpdateRequest updateRequest) {
        User initiator = findUserOrThrowException(userRepository, userId);
        Event event = findEventOrThrowException(eventRepository, eventId);

        permitRequestUpdating(initiator, event, updateRequest);
        checkParticipantLimit(event, updateRequest);

        List<ParticipationRequest> requestList = updateRequests(event, updateRequest);
        requestRepository.saveAllAndFlush(requestList);
        eventRepository.saveAndFlush(event);
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        fillRequestLists(requestList, confirmedRequests, rejectedRequests);

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private Event patchEvent(Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(findCategoryOrThrowException(categoryRepository, updateEventUserRequest.getCategory()));
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEventUserRequest.getEventDate(), TIME_FORMATTER));
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(findLocationOrSaveNew(locationRepository, updateEventUserRequest.getLocation()));
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() != null &&
                updateEventUserRequest.getStateAction().equals(StateActionUser.CANCEL_REVIEW)) {
            event.setState(State.CANCELED);
        } else if (updateEventUserRequest.getStateAction() != null &&
                updateEventUserRequest.getStateAction().equals(StateActionUser.SEND_TO_REVIEW)) {
            event.setState(State.PENDING);
        }
        return event;
    }

    private void permitEventUpdating(User initiator, Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (!initiator.getId().equals(event.getInitiator().getId())) {
            throw new InvalidOperationException("Изменить данные о событии может только инициатор события");
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new InvalidOperationException(
                    "Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2)) &&
                !updateEventUserRequest.getStateAction().equals(StateActionUser.CANCEL_REVIEW)) {
            throw new InvalidOperationException(
                    "Дата и время события не может быть раньше, чем через два часа от текущего момента");
        }
    }

    private void permitRequestUpdating(User initiator, Event event, EventRequestStatusUpdateRequest updateRequest) {
        if (!event.getInitiator().getId().equals(initiator.getId())) {
            throw new InvalidOperationException("Изменить статус заявок на событие может только инициатор события");
        }
        if (!event.getRequestModeration()) {
            throw new InvalidOperationException("Для события отключена премодерация заявок");
        }
        if (event.getParticipantLimit() == 0) {
            throw new InvalidOperationException("Для события с лимитом заявок 0 не требуется премодерация заявок");
        }
        List<ParticipationRequest> requestList = requestRepository.findAllById(updateRequest.getRequestIds());
        for (ParticipationRequest request : requestList) {
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new InvalidOperationException("Статус можно изменить только у заявок в состоянии ожидания");
            }
        }
    }

    private void checkParticipantLimit(Event event, EventRequestStatusUpdateRequest updateRequest) {
        int participantLimit = event.getParticipantLimit();
        int confirmedParticipants = event.getConfirmedRequests();
        int availableForConfirmation = participantLimit - confirmedParticipants;
        if (updateRequest.getStatus().equals(Status.CONFIRMED)
                && updateRequest.getRequestIds().size() > availableForConfirmation) {
            throw new InvalidOperationException("Количество подтверждённых заявок превышает лимит участников события");
        }
    }

    private List<ParticipationRequest> updateRequests(Event event, EventRequestStatusUpdateRequest updateRequest) {
        List<ParticipationRequest> requestList = requestRepository.findAllByEventId(event.getId());
        for (ParticipationRequest request : requestList) {
            if (updateRequest.getRequestIds().contains(request.getId()) && request.getStatus().equals(Status.PENDING)) {
                if (updateRequest.getStatus().equals(Status.CONFIRMED)) {
                    request.setStatus(Status.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else {
                    request.setStatus(Status.REJECTED);
                }
            }
        }
        return requestList;
    }

    private void fillRequestLists(List<ParticipationRequest> requestList,
                                  List<ParticipationRequestDto> confirmedRequests,
                                  List<ParticipationRequestDto> rejectedRequests) {
        requestList.stream()
                .map(ParticipationRequestMapper::toRequestDto)
                .forEach(requestDto -> {
                    if (requestDto.getStatus().equals(Status.CONFIRMED)) {
                        confirmedRequests.add(requestDto);
                    } else if (requestDto.getStatus().equals(Status.REJECTED)) {
                        rejectedRequests.add(requestDto);
                    }
                });
    }
}
