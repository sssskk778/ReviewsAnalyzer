package com.aska.core.service;

import com.aska.core.dto.AnalysisDetailsResponse;
import com.aska.core.dto.AnalysisResponse;
import com.aska.core.dto.AnalysisTaskMessage;
import com.aska.core.exception.ConflictException;
import com.aska.core.exception.NotFoundException;
import com.aska.core.model.Analysis;
import com.aska.core.model.AnalysisStatus;
import com.aska.core.model.Project;
import com.aska.core.repository.AnalysisRepository;
import com.aska.core.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ProjectRepository projectRepository;
    private final AnalysisRepository analysisRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.exchange}")
    private String exchange;

    @Value("${app.rabbit.task-routing-key}")
    private String taskRoutingKey;


    private static final List<AnalysisStatus> ACTIVE = List.of(AnalysisStatus.PENDING, AnalysisStatus.PROCESSING);

    @Transactional
    public AnalysisResponse createAnalysis(Long projectId, String email) {
        Project project = getOwnedProject(projectId, email);

        if(analysisRepository.existsByProjectProjectIdAndStatusIn(projectId, ACTIVE)){
            throw new ConflictException("Анализ этого продукта уже выполняется") {
            };
        }

        Analysis analysis = new Analysis();
        analysis.setProject(project);
        analysis.setStatus(AnalysisStatus.PENDING);
        analysis.setCreatedAt(LocalDateTime.now());
        analysisRepository.save(analysis);
        AnalysisTaskMessage analysisTaskMessage = new AnalysisTaskMessage(analysis.getAnalysisId(),projectId, project.getMarketplaceUrl());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                rabbitTemplate.convertAndSend(exchange,taskRoutingKey, analysisTaskMessage);
            }
        });
        return AnalysisResponse.from(analysis);
    }

    @Transactional(readOnly = true)
    public List<AnalysisResponse> listAnalysis(Long projectId, String email){
        getOwnedProject(projectId, email);
        return analysisRepository.findByProjectProjectIdOrderByCreatedAtDesc(projectId)
                .stream().map(AnalysisResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AnalysisDetailsResponse detailsAnalysis(Long analysisId, String email){
        Analysis analysis = analysisRepository.findByAnalysisIdAndProjectUserEmail(analysisId, email)
                .orElseThrow(() -> new NotFoundException("Анализ этого продукта не найден"));
        return AnalysisDetailsResponse.from(analysis);
    }

    private Project getOwnedProject(Long projectId, String email) {
        return projectRepository.findByProjectIdAndUserEmail(projectId, email)
                .orElseThrow(() -> new NotFoundException("Продукт не найден"));
    }
}
