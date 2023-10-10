package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.InvalidOperationException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.enums.Status;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utility.EntityFinder.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RequestServicePrivateImpl implements RequestServicePrivate {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getAllRequestsByUser(Long userId) {
        findUserOrThrowException(userRepository, userId);
        List<ParticipationRequest> requestList = requestRepository.findAllByRequesterId(userId);
        log.info("Получен список заявок на участие пользователя ID: {}", userId);
        return requestList.stream()
                .map(ParticipationRequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Integer eventId) {
        User requester = findUserOrThrowException(userRepository, userId);
        Event event = findEventOrThrowException(eventRepository, eventId);
        validateRequest(requester, event);

        ParticipationRequest request;
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            request = requestRepository.save(
                    new ParticipationRequest(null, LocalDateTime.now(), event, requester, Status.CONFIRMED));
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.saveAndFlush(event);
        } else {
            request = requestRepository.save(
                    new ParticipationRequest(null, LocalDateTime.now(), event, requester, Status.PENDING));
        }
        return ParticipationRequestMapper.toRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Integer requestId) {
        findUserOrThrowException(userRepository, userId);
        ParticipationRequest request = findRequestOtThrowException(requestRepository, requestId);

        if (!request.getRequester().getId().equals(userId)) {
            throw new InvalidOperationException("Отменить заявку может только пользователь, подавший заявку");
        }

        if (request.getStatus().equals(Status.CONFIRMED)) {
            Event event = findEventOrThrowException(eventRepository, request.getEvent().getId());
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.saveAndFlush(event);
        } else if (request.getStatus().equals(Status.CANCELED)) {
            throw new InvalidOperationException("Нельзя повторно отменить заявку");
        }

        request.setStatus(Status.CANCELED);
        request = requestRepository.saveAndFlush(request);
        log.info("Заявка на участие отменена");
        return ParticipationRequestMapper.toRequestDto(request);
    }

    private void validateRequest(User requester, Event event) {
        if (requestRepository.findByRequesterIdAndEventId(requester.getId(), event.getId()).isPresent()) {
            throw new InvalidOperationException("Нельзя добавить повторный запрос");
        }
        if (event.getInitiator().getId().equals(requester.getId())) {
            throw new InvalidOperationException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new InvalidOperationException("Нельзя участвовать в неопубликованном событии");
        }
        if (!event.getParticipantLimit().equals(0) &&
                event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new InvalidOperationException("У события достигнут лимит запросов на участие");
        }
    }
}
