package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.request.model.Status;

import java.util.List;

@Data
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Integer> requestIds;

    private Status status;
}
