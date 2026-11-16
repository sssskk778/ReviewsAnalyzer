package com.aska.core.controller;

import com.aska.core.model.Analysis;
import com.aska.core.repository.AnalysisRepository;
import com.aska.core.repository.ProjectRepository;
import com.aska.core.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisRepository analysisRepository;
    private final ProjectRepository projectRepository;
    private final AnalysisService analysisService;

    @PostMapping("/{id}/analyses")
    public ResponseEntity<Analysis> createAnalysis(@PathVariable Long id) {
        Analysis savedAnalysis = analysisService.createAnalysis(id);
        return ResponseEntity.ok(savedAnalysis);
    }
    // TODO GET /api/projects/{id}/analyses	история анализов товара
    //	TODO GET /api/analyses/{id}	детали: summary, счётчики, аспекты (по нему фронтенд опрашивает статус)
}
