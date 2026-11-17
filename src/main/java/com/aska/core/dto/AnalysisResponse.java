package com.aska.core.dto;

import com.aska.core.model.Analysis;
import com.aska.core.model.AnalysisStatus;

import java.time.LocalDateTime;

public record AnalysisResponse(Long id,
                               AnalysisStatus status,
                               LocalDateTime createdAt,
                               LocalDateTime startedAt,
                               LocalDateTime finishedAt,
                               int totalReviews,
                               double averageRating,
                               int positiveReviews,
                               int neutralReviews,
                               int negativeReviews,
                               String summary,
                               String errorMessage) {
    public static AnalysisResponse from(Analysis analysis){
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
