package com.aska.core.service;

import com.aska.core.dto.ProjectRequest;
import com.aska.core.dto.ProjectResponse;
import com.aska.core.dto.ProjectUpdateRequest;
import com.aska.core.exception.BadRequestException;
import com.aska.core.exception.ConflictException;
import com.aska.core.exception.NotFoundException;
import com.aska.core.exception.UnauthorizedException;
import com.aska.core.model.Marketplace;
import com.aska.core.model.Project;
import com.aska.core.model.User;
import com.aska.core.repository.ProjectRepository;
import com.aska.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private static final Pattern WILDBERRIES_URL =
            Pattern.compile("^https?://(www\\.)?wildberries\\.ru/catalog/\\d+(/.*)?$");


    @Transactional(readOnly = true)
    public List<ProjectResponse> listProject(String email){
        List<Project> projects = projectRepository.findByUserEmailOrderByCreatedAtDesc(email);
        return projects.stream().map(ProjectResponse::from).toList();
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request, String email){
        String url = request.marketplaceUrl().trim();
        if(!WILDBERRIES_URL.matcher(url).matches()){
            throw new BadRequestException("Ожидаемый формат URL: https://www.wildberries.ru/catalog/<number>/detail.aspx");
        }
        if(projectRepository.existsByUserEmailAndMarketplaceUrl(email, url)){
            throw new ConflictException("Этот продукт уже есть в вашей коллекции");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Пользователь не найден"));

        Project project = new Project();
        project.setUser(user);
        project.setMarketplaceUrl(url);
        project.setMarketplace(Marketplace.WILDBERRIES);
        project.setProductName(request.productName().trim());
        project.setImageUrl(isBlank(request.imageUrl()) ? null : request.imageUrl().trim());
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long id, String email) {
        return ProjectResponse.from(getOwned(id, email));
    }


    @Transactional
    public ProjectResponse updateProject(Long id, ProjectUpdateRequest request, String email) {
        Project existingProject = getOwned(id, email);
        existingProject.setProductName(request.productName().trim());
        existingProject.setImageUrl(isBlank(request.imageUrl()) ? null : request.imageUrl().trim());
        return ProjectResponse.from(projectRepository.save(existingProject));
    }
    @Transactional
    public void deleteProject(Long id, String email){
        projectRepository.delete(getOwned(id,email));
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private Project getOwned(Long id, String email){
        return projectRepository.findByProjectIdAndUserEmail(id, email)
                .orElseThrow(() -> new NotFoundException("Продукт не найден"));
    }
}
