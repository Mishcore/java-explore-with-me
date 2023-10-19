package ru.practicum.ewm.rating.model;

import lombok.*;
import ru.practicum.ewm.rating.dao.VoteId;

import javax.persistence.*;

@Entity
@IdClass(VoteId.class)
@Table(name = "event_votes")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Vote {

    @Id
    @Column(name = "event_id")
    private Integer eventId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private Boolean liked;
}
