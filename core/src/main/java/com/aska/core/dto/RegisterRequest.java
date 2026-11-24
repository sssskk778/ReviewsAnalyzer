package com.aska.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email обязателен")
        @Email(message = "Неверный формат email")
        String email,
        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, max = 50, message = "Пароль должен быть от 8 до 50 символов")
        String password,
        @NotBlank(message = "Имя пользователя обязательно")
        @Size(min = 2, max = 50, message = "Имя пользователя должно быть от 2 до 50 символов")
        String name
) {

}
