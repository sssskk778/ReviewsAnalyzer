package com.aska.core.dto;

import com.aska.core.model.Analysis;
import com.aska.core.model.AnalysisAspect;
import com.aska.core.model.AnalysisStatus;

import java.time.LocalDateTime;
import java.util.List;

public record AnalysisDetailsResponse(
        Long id,
        Long projectId,
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
        String recommendations,
        String errorMessage,
        List<Aspect> aspects
) {
    public record Aspect(String name, String category, int mentionCount,
                         int positiveMentions, int negativeMentions){
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
                analysis.getAspects().stream().map(aspect -> Aspect.from(aspect)).toList());
    }
}
