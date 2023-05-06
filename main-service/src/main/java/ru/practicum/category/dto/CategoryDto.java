package ru.practicum.category.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CategoryDto {

    private Integer id;

    @NotNull(message = "Field: name. Error: must not be blank.")
    private String name;
}
