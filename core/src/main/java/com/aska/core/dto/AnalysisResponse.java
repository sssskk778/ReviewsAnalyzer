package com.aska.core.dto;

import com.aska.core.model.Analysis;
import com.aska.core.enums.AnalysisStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Данные анализа отзывов на товар")
public record AnalysisResponse(

        @Schema(description = "Идентификатор анализа", example = "26")
        Long id,

        @Schema(description = "Статус анализа", example = "PENDING")
        AnalysisStatus status,

        @Schema(description = "Дата и время создания анализа", example = "2026-11-27T01:41:00")
        LocalDateTime createdAt,

        @Schema(description = "Дата и время начала обработки",
                example = "2026-11-27T01:41:05")
        LocalDateTime startedAt,

        @Schema(description = "Дата и время завершения (null, если анализ не завершён)",
                example = "2026-11-27T01:45:30")
        LocalDateTime finishedAt,

        @Schema(description = "Количество проанализированных отзывов", example = "120")
        int totalReviews,

        @Schema(description = "Средняя оценка по отзывам", example = "4.5")
        double averageRating,

        @Schema(description = "Количество положительных отзывов", example = "90")
        int positiveReviews,

        @Schema(description = "Количество нейтральных отзывов", example = "15")
        int neutralReviews,

        @Schema(description = "Количество отрицательных отзывов", example = "15")
        int negativeReviews,

        @Schema(description = "Краткое резюме анализа", example = "Покупатели хвалят качество, но жалуются на размер")
        String summary,

        @Schema(description = "Текст ошибки, если анализ не удался",
                example = "LLM API недоступен после 5 попыток")
        String errorMessage
) {
    public static AnalysisResponse from(Analysis analysis) {
        return new AnalysisResponse(
                analysis.getAnalysisId(),
                analysis.getStatus(),
                analysis.getCreatedAt(),
                analysis.getStartedAt(),
                analysis.getFinishedAt(),
                analysis.getTotalReviews(),
                analysis.getAverageRating(),
                analysis.getPositiveReviews(),
                analysis.getNeutralReviews(),
                analysis.getNegativeReviews(),
                analysis.getSummary(),
                analysis.getErrorMessage());
    }
}