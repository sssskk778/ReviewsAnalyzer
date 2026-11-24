package com.aska.core.service;

import com.aska.core.dto.AnalysisResultMessage;
import com.aska.core.model.Analysis;
import com.aska.core.model.AnalysisAspect;
import com.aska.core.model.AnalysisStatus;
import com.aska.core.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalysisResultService {

    private final AnalysisRepository analysisRepository;

    @Transactional
    public void apply(AnalysisResultMessage message){
        Analysis analysis = analysisRepository.findById(message.analysisId()).orElse(null);
        if(analysis == null){
            return;
        }
        if(analysis.getStatus() != AnalysisStatus.PENDING && analysis.getStatus() != AnalysisStatus.PROCESSING){
            return;
        }

        analysis.setStatus(message.status());

        if(message.status() == AnalysisStatus.PROCESSING){
            analysis.setStartedAt(LocalDateTime.now());
            return;
        }

        analysis.setFinishedAt(LocalDateTime.now());
        analysis.setErrorMessage(message.errorMessage());

        if(message.status() == AnalysisStatus.COMPLETED){
            analysis.setTotalReviews(message.totalReviews());
            analysis.setAverageRating(message.averageRating());
            analysis.setPositiveReviews(message.positiveReviews());
            analysis.setNeutralReviews(message.neutralReviews());
            analysis.setNegativeReviews(message.negativeReviews());
            analysis.setSummary(message.summary());
            analysis.setRecommendations(message.recommendations());

            analysis.getAspects().clear();
            analysis.getAspects().addAll(
                    message.aspects().stream().map(a -> {
                           AnalysisAspect aspect = new AnalysisAspect();
                           aspect.setName(a.name());
                           aspect.setCategory(a.category());
                           aspect.setMentionCount(a.mentionCount());
                           aspect.setPositiveMentions(a.positiveMentions());
                           aspect.setNegativeMentions(a.negativeMentions());
                           aspect.setAnalysis(analysis);
                           return aspect;
                    })
                    .toList());
        }
    }
}
