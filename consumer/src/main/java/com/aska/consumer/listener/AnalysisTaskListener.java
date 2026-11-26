package com.aska.consumer.listener;

import com.aska.consumer.dto.AnalysisTaskMessage;
import com.aska.consumer.service.IAnalysisProcessor;
import com.aska.consumer.service.impl.AnalysisProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisTaskListener {

    private final IAnalysisProcessor processor;

    @RabbitListener(queues = "${app.rabbit.task-queue}")
    public void handle(AnalysisTaskMessage message){
       processor.process(message);
    }
}
