package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.exception.IllegalStartEndDatesException;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.Constants.TIME_FORMATTER;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto saveHit(@RequestBody EndpointHitDto hit) {
        log.info("Запрос на сохранение информации о запросе к эндпоинту {}", hit);
        return statsService.saveHit(hit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        if (!LocalDateTime.parse(start, TIME_FORMATTER).isBefore(LocalDateTime.parse(end, TIME_FORMATTER))) {
            throw new IllegalStartEndDatesException("Дата начала не может быть позже даты конца");
        }
        log.info("Запрос на получение статистики по посещениям");
        return statsService.getStats(start, end, uris, unique);
    }
}