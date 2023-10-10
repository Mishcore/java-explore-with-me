package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {

    private Integer id;

    private String title;

    private String annotation;

    private Category category;

    private Integer confirmedRequests;

    private String eventDate;

    private UserShortDto initiator;

    private Boolean paid;

    private Long views;
}