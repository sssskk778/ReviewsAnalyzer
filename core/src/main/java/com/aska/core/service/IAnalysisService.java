package com.aska.core.service;

import com.aska.core.dto.AnalysisDetailsResponse;
import com.aska.core.dto.AnalysisResponse;
import java.util.List;

public interface IAnalysisService {

    AnalysisResponse createAnalysis(Long projectId, String email);

    List<AnalysisResponse> listAnalysis(Long projectId, String email);

    AnalysisDetailsResponse detailsAnalysis(Long analysisId, String email);
}
