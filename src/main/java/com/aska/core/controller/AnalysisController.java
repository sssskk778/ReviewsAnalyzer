package com.aska.core.controller;

import com.aska.core.dto.AnalysisDetailsResponse;
import com.aska.core.dto.AnalysisResponse;
import com.aska.core.model.Analysis;
import com.aska.core.repository.AnalysisRepository;
import com.aska.core.repository.ProjectRepository;
import com.aska.core.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    //запуск анализа
    @PostMapping("/project/{projectId}/analyses")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AnalysisResponse create(@PathVariable Long projectId, Principal principal) {
        return analysisService.createAnalysis(projectId, principal.getName());
    }

    // история анализов
    @GetMapping("/project/{projectId}/analyses")
    public List<AnalysisResponse> list(@PathVariable Long projectId, Principal principal) {
        return analysisService.listAnalysis(projectId, principal.getName());
    }

    //детали анализа
    @GetMapping("/analyses/{analysisId}")
    public AnalysisDetailsResponse details(@PathVariable Long analysisId, Principal principal) {
        return analysisService.detailsAnalysis(analysisId, principal.getName());
    }
}
