package com.aska.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@Schema(description = "Запрос на создание товара")
public record ProjectRequest(

        @Schema(description = "Ссылка на товар на маркетплейсе",
                example = "https://www.wildberries.ru/catalog/275942265/detail.aspx")
        @NotBlank(message = "URL обязателен")
        @URL(message = "Пожалуйста, укажите корректный URL")
        @Size(max = 1000, message = "URL не должен превышать 1000 символов")
        String marketplaceUrl,

        @Schema(description = "Название товара", example = "Носки короткие, набор 10 пар")
        @NotBlank(message = "Название товара обязательно")
        @Size(min = 3, max = 255, message = "Название товара должно быть от 3 до 255 символов")
        String productName,

        @Schema(description = "Ссылка на изображение товара", example = "https://example.com/images/1.webp")
        @Size(max = 1000, message = "URL изображения не должен превышать 1000 символов")
        String imageUrl
) {
}
