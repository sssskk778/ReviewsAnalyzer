package com.aska.core.dto;

import com.aska.core.enums.Marketplace;
import com.aska.core.model.Project;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
@Schema(description = "Данные товара")
public record ProjectResponse(
        @Schema(description = "Идентификатор товара", example = "11")
        Long id,

        @Schema(description = "Ссылка на товар на маркетплейсе",
                example = "https://www.wildberries.ru/catalog/275942265/detail.aspx")
        String marketplaceUrl,

        @Schema(description = "Маркетплейс, на котором размещён товар")
        Marketplace marketplace,

        @Schema(description = "Название товара", example = "Носки короткие, набор 10 пар")
        String productName,

        @Schema(description = "Ссылка на изображение товара", example = "https://example.com/images/1.webp")
        String imageUrl,

        @Schema(description = "Текущий рейтинг товара", example = "4.7")
        double currentRating,

        @Schema(description = "Дата и время создания товара", example = "2026-11-26T16:34:03")
        LocalDateTime createdAt,

        @Schema(description = "Дата и время последнего изменения товара",
                example = "2026-11-27T10:15:00")
        LocalDateTime updatedAt
) {
    public static ProjectResponse from(Project project){
        return new ProjectResponse(project.getProjectId(), project.getMarketplaceUrl(),
                project.getMarketplace(), project.getProductName(), project.getImageUrl(),
                project.getCurrentRating(), project.getCreatedAt(), project.getUpdatedAt());
    }
}
