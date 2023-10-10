package ru.practicum.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.enums.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {

    private Integer id;

    private String created;

    private Integer event;

    private Long requester;

    private Status status;
}