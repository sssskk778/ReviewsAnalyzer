package com.aska.core.listener;

import com.aska.core.dto.AnalysisResultMessage;
import com.aska.core.service.IAnalysisResultService;
import com.aska.core.service.impl.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalysisResultListener {

    private final IAnalysisResultService analysisResultService;

    @RabbitListener(queues = "${app.rabbit.result-queue}")
    public void handle(AnalysisResultMessage message) {
        analysisResultService.apply(message);
    }
}

