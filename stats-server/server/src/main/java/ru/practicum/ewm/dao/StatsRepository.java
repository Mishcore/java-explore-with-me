package ru.practicum.ewm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("select new ru.practicum.ewm.ViewStats(hit.app, hit.uri, COUNT(hit.ip) AS count_ip) " +
            "from EndpointHit hit " +
            "where hit.timestamp between ?1 and ?2 " +
            "and hit.uri in ?3 " +
            "group by hit.app, hit.uri " +
            "order by count_ip desc")
    List<ViewStats> getStatsForUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ewm.ViewStats(hit.app, hit.uri, COUNT(DISTINCT(hit.ip)) AS count_ip) " +
            "from EndpointHit hit " +
            "where hit.timestamp between ?1 and ?2 " +
            "and hit.uri in ?3 " +
            "group by hit.app, hit.uri " +
            "order by count_ip desc")
    List<ViewStats> getUniqueStatsForUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ewm.ViewStats(hit.app, hit.uri, COUNT(hit.ip) AS count_ip) " +
            "from EndpointHit hit " +
            "where hit.timestamp between ?1 and ?2 " +
            "group by hit.app, hit.uri " +
            "order by count_ip desc")
    List<ViewStats> getAllStats(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.ViewStats(hit.app, hit.uri, COUNT(hit.ip) AS count_ip) " +
            "from EndpointHit hit " +
            "where hit.timestamp between ?1 and ?2 " +
            "group by hit.app, hit.uri " +
            "order by count_ip desc")
    List<ViewStats> getAllUniqueStats(LocalDateTime start, LocalDateTime end);
}