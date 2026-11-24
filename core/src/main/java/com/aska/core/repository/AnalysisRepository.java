package com.aska.core.repository;

import com.aska.core.model.Analysis;
import com.aska.core.model.AnalysisStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    List<Analysis> findByProjectProjectIdOrderByCreatedAtDesc(Long projectId);

    Optional<Analysis> findByAnalysisIdAndProjectUserEmail(Long analysisId, String email);

    boolean existsByProjectProjectIdAndStatusIn(Long projectId, Collection<AnalysisStatus> statuses);

}
