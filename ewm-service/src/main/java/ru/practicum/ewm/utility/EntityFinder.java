package ru.practicum.ewm.utility;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.compilation.dao.CompilationRepository;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.location.dao.LocationRepository;
import ru.practicum.ewm.event.location.model.Location;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.model.User;

@UtilityClass
public class EntityFinder {

    public static User findUserOrThrowException(UserRepository userRepository, Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    public static Category findCategoryOrThrowException(CategoryRepository categoryRepository, Integer catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));
    }

    public static Event findEventOrThrowException(EventRepository eventRepository, Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено"));
    }

    public static Location findLocationOrSaveNew(LocationRepository locationRepository, Location location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElse(locationRepository.save(location));
    }

    public static ParticipationRequest findRequestOtThrowException(RequestRepository requestRepository, Integer requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка на участие не найдена"));
    }

    public static Compilation findCompilationOrThrowException(CompilationRepository compilationRepository, Integer compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка событий не найдена"));
    }
}