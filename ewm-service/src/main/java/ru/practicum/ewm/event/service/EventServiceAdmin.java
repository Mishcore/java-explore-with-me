package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.enums.State;

import java.util.List;

public interface EventServiceAdmin {
    List<EventFullDto> getEvents(Long[] users, State[] states, Integer[] categories,
                                 String rangeStart, String rangeEnd,
                                 Integer from, Integer size);

    EventFullDto patchEventByAdmin(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
