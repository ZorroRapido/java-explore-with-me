package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "Поле 'email' не должно быть пустым!")
    @Email(message = "Неверный формат поля 'email'!")
    private String email;

    @NotBlank(message = "Поле 'name' не должно быть пустым!")
    private String name;
}
