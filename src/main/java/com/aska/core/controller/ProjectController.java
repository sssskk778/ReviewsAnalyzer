package com.aska.core.controller;

import com.aska.core.dto.ProjectRequest;
import com.aska.core.dto.ProjectResponse;
import com.aska.core.dto.ProjectUpdateRequest;
import com.aska.core.model.Project;
import com.aska.core.repository.ProjectRepository;
import com.aska.core.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<ProjectResponse> list(Principal principal) {
        return projectService.listProject(principal.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request, Principal principal) {
        return projectService.createProject(request, principal.getName());
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id, Principal principal) {
        return projectService.getProject(id,principal.getName());
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectUpdateRequest request, Principal principal) {
        return projectService.updateProject(id,request, principal.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void  delete(@PathVariable Long id, Principal principal) {
        projectService.deleteProject(id,principal.getName());
    }
}
