package ru.practicum.ewm.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utility.EventViewsManager.getEventViews;

@UtilityClass
public class CompilationMapper {

    public static Compilation toNewCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return new Compilation(null,
                newCompilationDto.getTitle(),
                events,
                newCompilationDto.getPinned() != null && newCompilationDto.getPinned());
    }

    public static CompilationDto toCompilationDto(Compilation compilation, StatsClient statsClient) {
        List<EventShortDto> eventDtos = compilation.getEvents().stream()
                .map(event -> EventMapper.toEventShortDto(event, getEventViews(statsClient, event)))
                .collect(Collectors.toList());

        return new CompilationDto(eventDtos, compilation.getId(), compilation.getTitle(), compilation.getPinned());
    }
}