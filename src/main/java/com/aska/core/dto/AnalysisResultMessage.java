package com.aska.core.dto;

import com.aska.core.model.AnalysisStatus;

import java.util.List;

public record AnalysisResultMessage(Long analysisId,
                                    AnalysisStatus status,
                                    int totalReviews,
                                    double averageRating,
                                    int positiveReviews,
                                    int neutralReviews,
                                    int negativeReviews,
                                    String summary,
                                    String recommendations,
                                    String errorMessage,
                                    List<AspectDto> aspects) {
    public record AspectDto(String name, String category,
                            int mentionCount, int positiveMentions,
                            int negativeMentions){
    }
}
