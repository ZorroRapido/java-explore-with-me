package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.request.model.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ParticipationRequestDto {

    private Integer id;

    private Integer event;

    private Integer requester;

    private Status status;

    private LocalDateTime created;
}
