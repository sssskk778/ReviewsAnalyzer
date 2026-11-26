package com.aska.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Schema(description = "Запрос на изменение товара")
public record ProjectUpdateRequest(

        @Schema(description = "Название товара", example = "Носки короткие, набор 10 пар")
        @NotBlank(message = "Название товара обязательно")
        @Size(min = 3, max = 255, message = "Название товара должно быть от 3 до 255 символов")
        String productName,

        @Schema(description = "Ссылка на изображение товара", example = "https://example.com/images/1.webp")
        @Size(max = 1000, message = "URL изображения не должен превышать 1000 символов")
        String imageUrl
) {
}
