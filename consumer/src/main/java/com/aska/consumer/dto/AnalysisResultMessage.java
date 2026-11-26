package com.aska.consumer.dto;

import com.aska.consumer.enums.AnalysisStatus;
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

    public static AnalysisResultMessage started(Long id) {
        return new AnalysisResultMessage(id, AnalysisStatus.PROCESSING,
                0, 0, 0, 0, 0, null, null, null, List.of());
    }

    public static AnalysisResultMessage noData(Long id) {
        return new AnalysisResultMessage(id, AnalysisStatus.NO_DATA,
                0, 0, 0, 0, 0, null, null, null, List.of());
    }

    public static AnalysisResultMessage failed(Long id, String error) {
        return new AnalysisResultMessage(id, AnalysisStatus.FAILED,
                0, 0, 0, 0, 0, null, null, error, List.of());
    }
}