package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateCompilationRequest {

    private String title;

    private Boolean pinned;

    private List<Integer> events;
}
