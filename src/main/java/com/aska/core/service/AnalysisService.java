package com.aska.core.service;

import com.aska.core.model.Analysis;
import com.aska.core.model.Project;
import com.aska.core.repository.AnalysisRepository;
import com.aska.core.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ProjectRepository projectRepository;
    private final AnalysisRepository analysisRepository;

    public Analysis createAnalysis(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        Analysis analysis = new Analysis();
        analysis.setProject(project);
        analysis.setStatus("PENDING");
        analysis.setCreatedAt(java.time.LocalDateTime.now());
        analysisRepository.save(analysis);
        // TODO: Call AI service to generate analysis
        // analysisRepository.save(analysis);
        return analysis;
    }
}
