package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.dao.StatsRepository;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public EndpointHitDto saveHit(EndpointHitDto hit) {
        log.info("Принят запрос на сохранение информации о запросе к эндпоинту {}", hit);
        EndpointHit newHit = statsRepository.save(EndpointHitMapper.toHit(hit));
        log.info("Сохранена информация о запросе к эндпоинту {}", newHit);
        return EndpointHitMapper.toHitDto(newHit);
    }

    @Override
    public List<ViewStats> getStats(String startString, String endString, String[] urisArray, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startString, formatter);
        LocalDateTime end = LocalDateTime.parse(endString, formatter);

        List<ViewStats> viewStats;
        if (urisArray != null) {
            List<String> uris = Arrays.asList(urisArray);
            if (unique) {
                log.info("Принят запрос на получение статистики по уникальным посещениям для URI: {}", uris);
                viewStats = statsRepository.getUniqueStatsForUris(start, end, uris);
            } else {
                log.info("Принят запрос на получение статистики по посещениям для URI: {}", uris);
                viewStats = statsRepository.getStatsForUris(start, end, uris);
            }
        } else {
            if (unique) {
                log.info("Принят запрос на получение статистики по уникальным посещениям для всех URI");
                viewStats = statsRepository.getAllUniqueStats(start, end);
            } else {
                log.info("Принят запрос на получение статистики по посещениям для всех URI");
                viewStats = statsRepository.getAllStats(start, end);
            }
        }
        log.info("Статистика по посещениям получена");
        return viewStats;
    }
}