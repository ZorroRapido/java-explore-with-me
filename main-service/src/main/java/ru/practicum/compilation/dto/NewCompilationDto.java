package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class NewCompilationDto {

    @NotBlank(message = "Поле 'title' не должно быть пустым!")
    private String title;

    @NotNull(message = "Поле 'pinned' не должно быть пустым!")
    private Boolean pinned;

    private List<Integer> events;
}
