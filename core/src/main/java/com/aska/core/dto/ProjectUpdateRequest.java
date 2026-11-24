package com.aska.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectUpdateRequest(
        @NotBlank(message = "Название продукта обязательно")
        @Size(min = 3, max = 255, message = "Название продукта должно быть от 3 до 255 символов")
        String productName,

        @Size(max = 1000, message = "URL изображения не должен превышать 1000 символов")
        String imageUrl
) {
}
