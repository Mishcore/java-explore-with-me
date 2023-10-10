package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.request.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long userId, Integer id);

    List<ParticipationRequest> findAllByEventId(Integer eventId);
}