package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.compilation.dao.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utility.EntityFinder.findCompilationOrThrowException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceAdminImpl implements CompilationServiceAdmin {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            events = findAllEventsOrThrowException(List.copyOf(newCompilationDto.getEvents()));
        }
        Compilation newCompilation =
                compilationRepository.save(CompilationMapper.toNewCompilation(newCompilationDto, events));
        log.info("Добавлена новая подборка событий ID: {}", newCompilation.getId());
        return CompilationMapper.toCompilationDto(newCompilation, statsClient);
    }

    @Override
    public void deleteCompilation(Integer compId) {
        findCompilationOrThrowException(compilationRepository, compId);
        compilationRepository.deleteById(compId);
        log.info("Удалена подборка событий ID: {}", compId);
    }

    @Override
    public CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = findCompilationOrThrowException(compilationRepository, compId);
        patchCompilation(compilation, updateCompilationRequest);
        compilationRepository.saveAndFlush(compilation);
        log.info("Внесены изменения в подборку событий ID: {}", compId);
        return CompilationMapper.toCompilationDto(compilation, statsClient);
    }

    private Compilation patchCompilation(Compilation compilation, UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = findAllEventsOrThrowException(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        return compilation;
    }

    private List<Event> findAllEventsOrThrowException(List<Integer> eventsIds) {
        List<Event> events = eventRepository.findAllById(eventsIds);

        List<Integer> foundEventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Integer> missingEventIds = eventsIds.stream()
                .filter(id -> !foundEventIds.contains(id))
                .collect(Collectors.toList());
        if (!missingEventIds.isEmpty()) {
            throw new EntityNotFoundException("Не были найдены события ID: " + missingEventIds);
        }
        return events;
    }
}
