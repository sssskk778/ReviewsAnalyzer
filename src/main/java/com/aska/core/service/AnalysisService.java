package com.aska.core.service;

import com.aska.core.dto.AnalysisDetailsResponse;
import com.aska.core.dto.AnalysisResponse;
import com.aska.core.model.Analysis;
import com.aska.core.model.AnalysisStatus;
import com.aska.core.model.Project;
import com.aska.core.repository.AnalysisRepository;
import com.aska.core.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ProjectRepository projectRepository;
    private final AnalysisRepository analysisRepository;

    private static final List<AnalysisStatus> ACTIVE = List.of(AnalysisStatus.PENDING, AnalysisStatus.PROCESSING);

    @Transactional
    public AnalysisResponse createAnalysis(Long projectId, String email) {
        Project project = getOwnedProject(projectId, email);

        if(analysisRepository.existsByProjectProjectIdAndStatusIn(projectId, ACTIVE)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "");
        }

        Analysis analysis = new Analysis();
        analysis.setProject(project);
        analysis.setStatus(AnalysisStatus.PENDING);
        analysis.setCreatedAt(LocalDateTime.now());
        analysisRepository.save(analysis);
        // TODO: положить задачу {analysisId, projectId, url} в очередь Redis для Consumer service
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " "));
        return AnalysisDetailsResponse.from(analysis);
    }


    private Project getOwnedProject(Long projectId, String email) {
        return projectRepository.findByProjectIdAndUserEmail(projectId, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Товар не найден"));
    }

}
