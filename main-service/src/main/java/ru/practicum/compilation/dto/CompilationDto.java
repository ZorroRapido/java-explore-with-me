package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationDto {

    private Integer id;

    private Boolean pinned;

    private String title;

    private List<EventShortDto> events;
}
