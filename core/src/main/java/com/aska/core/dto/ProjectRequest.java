package com.aska.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ProjectRequest(
        @NotBlank(message = "URL обязателен")
        @URL(message = "Пожалуйста, укажите корректный URL")
        @Size(max = 1000, message = "URL не должен превышать 1000 символов")
        String marketplaceUrl,

        @NotBlank(message = "Название продукта обязательно")
        @Size(min = 3, max = 255, message = "Название продукта должно быть от 3 до 255 символов")
        String productName,

        @Size(max = 1000, message = "URL изображения не должен превышать 1000 символов")
        String imageUrl
) {
}
