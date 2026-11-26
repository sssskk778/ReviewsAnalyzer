package com.aska.core.dto;

import com.aska.core.model.Analysis;
import com.aska.core.model.AnalysisAspect;
import com.aska.core.enums.AnalysisStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Подробные данные анализа отзывов, включая аспекты товара")
public record AnalysisDetailsResponse(
        @Schema(description = "Идентификатор анализа", example = "26")
        Long id,

        @Schema(description = "Идентификатор проекта, к которому относится анализ", example = "11")
        Long projectId,

        @Schema(description = "Статус анализа")
        AnalysisStatus status,

        @Schema(description = "Дата и время создания анализа", example = "2026-11-27T01:41:00")
        LocalDateTime createdAt,

        @Schema(description = "Дата и время начала обработки",
                example = "2026-11-27T01:41:05")
        LocalDateTime startedAt,

        @Schema(description = "Дата и время завершения",
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

        @Schema(description = "Рекомендации для продавца", example = "Уточнить размерную сетку в описании товара")
        String recommendations,

        @Schema(description = "Текст ошибки, если анализ не удался",
                example = "LLM API недоступен после 5 попыток")
        String errorMessage,

        @Schema(description = "Аспекты товара, которые упоминают в отзывах")
        List<Aspect> aspects
) {
    @Schema(description = "Аспект товара с количеством упоминаний")
    public record Aspect(
            @Schema(description = "Название аспекта", example = "Качество ткани")
            String name,

            @Schema(description = "Категория аспекта", example = "Материал")
            String category,

            @Schema(description = "Сколько раз аспект упомянут в отзывах", example = "25")
            int mentionCount,

            @Schema(description = "Сколько упоминаний положительные", example = "20")
            int positiveMentions,

            @Schema(description = "Сколько упоминаний отрицательные", example = "5")
            int negativeMentions
    ){
        public static Aspect from(AnalysisAspect aspect){
            return new Aspect(aspect.getName(), aspect.getCategory(), aspect.getMentionCount(),
                    aspect.getPositiveMentions(), aspect.getNegativeMentions());
        }
    }
    public static AnalysisDetailsResponse from(Analysis analysis){
        return new AnalysisDetailsResponse(analysis.getAnalysisId(), analysis.getProject().getProjectId(),
                analysis.getStatus(),analysis.getCreatedAt(), analysis.getStartedAt(),analysis.getFinishedAt(),
                analysis.getTotalReviews(), analysis.getAverageRating(), analysis.getPositiveReviews(),
                analysis.getNeutralReviews(), analysis.getNegativeReviews(), analysis.getSummary(),
                analysis.getRecommendations(), analysis.getErrorMessage(),
                analysis.getAspects().stream().map(Aspect::from).toList());
    }
}