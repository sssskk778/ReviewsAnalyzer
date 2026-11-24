package com.aska.consumer.dto;

import java.util.List;

public record LlmAnalysisResult(
        int positiveReviews,
        int neutralReviews,
        int negativeReviews,
        List<AspectStat> aspects,
        String summary,
        String recommendations) {

    public record AspectStat(
            String name,
            String category,
            int mentionCount,
            int positiveMentions,
            int negativeMentions){
    }
}
