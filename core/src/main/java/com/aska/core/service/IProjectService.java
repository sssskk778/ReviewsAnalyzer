package com.aska.core.service;

import com.aska.core.dto.ProjectRequest;
import com.aska.core.dto.ProjectResponse;
import com.aska.core.dto.ProjectUpdateRequest;


import java.util.List;

public interface IProjectService {

    List<ProjectResponse> listProject(String email);

    ProjectResponse createProject(ProjectRequest request, String email);

    ProjectResponse getProject(Long id, String email);

    ProjectResponse updateProject(Long id, ProjectUpdateRequest request, String email);

    void deleteProject(Long id, String email);

}
