package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventShortDto {

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    private LocalDateTime eventDate;

    private Integer id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Integer views;

    private Integer rating;
}
