package com.aska.consumer.service;

import com.aska.consumer.dto.AnalysisResultMessage;
import com.aska.consumer.dto.AnalysisTaskMessage;
import com.aska.consumer.dto.LlmAnalysisResult;
import com.aska.consumer.dto.Review;

import java.util.List;

public interface IAnalysisProcessor {

    void process(AnalysisTaskMessage task);

}
