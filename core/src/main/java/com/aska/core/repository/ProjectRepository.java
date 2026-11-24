package com.aska.core.repository;

import com.aska.core.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserEmailOrderByCreatedAtDesc(String email);

    Optional<Project> findByProjectIdAndUserEmail(Long projectId, String email);

    boolean existsByUserEmailAndMarketplaceUrl(String email, String marketplaceUrl);

}
