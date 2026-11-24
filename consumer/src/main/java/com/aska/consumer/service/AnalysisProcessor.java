package com.aska.consumer.service;

import com.aska.consumer.dto.AnalysisResultMessage;
import com.aska.consumer.dto.AnalysisTaskMessage;
import com.aska.consumer.dto.LlmAnalysisResult;
import com.aska.consumer.dto.Review;
import com.aska.consumer.llm.ReviewAnalyzer;
import com.aska.consumer.model.AnalysisStatus;
import com.aska.consumer.parser.WildberriesParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisProcessor {
    private final RabbitTemplate rabbitTemplate;
    private final ReviewAnalyzer analyzer;

    @Value("${app.rabbit.result-routing-key}")
    private String resultRoutingKey;
    @Value("${app.rabbit.exchange}")
    private String exchange;

    private final WildberriesParser parser;


    public void process(AnalysisTaskMessage task){
        Long id = task.analysisId();
        log.info("Получена задача {}", task);
        send(AnalysisResultMessage.started(id));
        try{
            List<Review> reviews = parser.fetchReviews(task.url());
            if(reviews.isEmpty()){
                send(AnalysisResultMessage.noData(id));
                log.info("Анализ {}: отзывов нет, статус NO_DATA", id);
                return;
            }
            LlmAnalysisResult llm = analyzer.analyze(reviews);
            send(buildResult(id,reviews, llm));
            log.info("Анализ {}: завершён успешно, результат отправлен", id);
        }catch (Exception ex){
            log.error("Анализ {} не удался", id, ex);
            send(AnalysisResultMessage.failed(id,ex.getMessage()));
        }
    }

    private void send(AnalysisResultMessage message){
        rabbitTemplate.convertAndSend(exchange,resultRoutingKey, message);
    }

    private AnalysisResultMessage buildResult(Long id, List<Review> reviews, LlmAnalysisResult llm){

        double average = reviews.stream().mapToInt(Review::rating).average().orElse(0);

        List<AnalysisResultMessage.AspectDto> aspects = llm.aspects().stream()
                .map(aspect -> new AnalysisResultMessage.AspectDto(aspect.name(),aspect.category(),
                        aspect.mentionCount(), aspect.positiveMentions(), aspect.negativeMentions())).toList();

        return new AnalysisResultMessage(id,AnalysisStatus.COMPLETED,reviews.size(),
                Math.round(average * 100)/100.0, llm.positiveReviews(), llm.neutralReviews(),llm.negativeReviews(),
                llm.summary(), llm.recommendations(), null, aspects);
    }
}
