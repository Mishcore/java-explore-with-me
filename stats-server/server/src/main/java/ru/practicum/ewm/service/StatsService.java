package ru.practicum.ewm.service;

import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStats;

import java.util.List;

public interface StatsService {
    EndpointHitDto saveHit(EndpointHitDto hit);

    List<ViewStats> getStats(String start, String end, String[] uris, Boolean unique);
}