package ru.practicum.ewm.rating.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.rating.dto.Rating;
import ru.practicum.ewm.rating.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, VoteId> {

    @Query("SELECT new ru.practicum.ewm.rating.dto.Rating(" +
            "SUM(CASE WHEN v.liked = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN v.liked = false THEN 1 ELSE 0 END), " +
            "CAST((SUM(CASE WHEN v.liked = true THEN 1 ELSE 0 END) - SUM(CASE WHEN v.liked = false THEN 1 ELSE 0 END)) AS float) / " +
                "(SELECT SUM(confirmedRequests) FROM Event WHERE id = ?1)) " +
            "FROM Vote v " +
            "JOIN Event e ON v.eventId = e.id " +
            "WHERE v.eventId = ?1")
    Rating getEventRating(Integer eventId);

    @Query("SELECT " +
            "CAST((SUM(CASE WHEN v.liked = true THEN 1 ELSE 0 END) - SUM(CASE WHEN v.liked = false THEN 1 ELSE 0 END)) AS float) / " +
            "(SELECT SUM(confirmedRequests) FROM Event WHERE initiator.id = ?1) " +
            "FROM Vote v " +
            "JOIN Event e ON v.eventId = e.id " +
            "WHERE e.initiator.id = ?1")
    Float getUserRating(Long userId);
}