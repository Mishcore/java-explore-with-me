package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.enums.Sort;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventServicePublic {
    List<EventShortDto> getEvents(String text,
                                  Integer[] categories,
                                  Boolean paid,
                                  String rangeStart,
                                  String rangeEnd,
                                  Boolean onlyAvailable,
                                  Sort sort,
                                  Integer from,
                                  Integer size,
                                  HttpServletRequest request);

    EventFullDto getEventById(Integer id, HttpServletRequest request);
}
