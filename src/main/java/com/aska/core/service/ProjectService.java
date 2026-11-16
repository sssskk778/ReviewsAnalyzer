package com.aska.core.service;

import com.aska.core.model.Project;
import com.aska.core.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;


    public Project update(Long id, Project project) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        existingProject.setMarketplaceUrl(project.getMarketplaceUrl());
        existingProject.setProductName(project.getProductName());
        existingProject.setImageUrl(project.getImageUrl());

        return projectRepository.save(existingProject);
    }


}
