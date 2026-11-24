package com.aska.consumer.llm;

import com.aska.consumer.exception.LlmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LlmClient {

    private static final int MAX_ATTEMPTS = 3;
    private final JsonMapper mapper = JsonMapper.builder().build();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();

    @Value("${llm.api-key}")
    private String apiKey;
    @Value("${llm.model}")
    private String model;
    @Value("${llm.url}")
    private String url;

    public String complete(String prompt){
        if(apiKey == null || apiKey.isBlank()){
            throw new LlmException("LLM_API_KEY не установлен");
        }

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(90))
                .header("Content-Type", "application/json")
                .header("Authorization","Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(buildBody(prompt)))
                .build();

        for(int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++){
            try {
                HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
                int status = response.statusCode();

                if (status == 200){
                    return extractContent(response.body());
                }
                if(status != 429 && status < 500){
                    throw new LlmException("Ошибка LLM API " + status + ": " + shorten(response.body()));
                }
                log.warn("LLM вернул статус {} (попытка {}/{})", status, attempt, MAX_ATTEMPTS);

            }catch (IOException ex){
                log.warn("Запрос к LLM не удался (попытка {}/{}): {}", attempt, MAX_ATTEMPTS, ex.getMessage());
            }catch (InterruptedException ex){
                Thread.currentThread().interrupt();
                throw new LlmException("Прервано при вызове LLM");
            }
            if(attempt < MAX_ATTEMPTS){
                pause(attempt * 3000L);
            }
        }
        throw new LlmException("LLM API недоступен после " + MAX_ATTEMPTS + " попыток");
    }

    private String buildBody(String prompt){
        try {
            return mapper.writeValueAsString(Map.of(
                    "model", model,
                    "temperature", 0.2,
                    "response_format", Map.of("type", "json_object"),
                    "reasoning_effort", "low",
                    "messages", List.of(Map.of("role","user","content",prompt))

            ));

        }catch (Exception ex){
            throw new LlmException("Не удалось создать запрос LLM", ex);
        }
    }

    private String extractContent(String resposeBody){
        try {
            String content = mapper.readTree(resposeBody)
                    .path("choices").path(0)
                    .path("message").path("content").textValue();
            if(content == null || content.isBlank()){
                throw new LlmException("Пустой ответ от LLM");
            }
            return content;
        }catch (LlmException ex){
            throw ex;
        }catch (Exception ex){
            throw new LlmException("Неожиданный ответ от LLM",ex);
        }
    }

    private String shorten(String text){
        if(text == null){
            return "";
        }
        return text.length() > 300 ? text.substring(0,300) : text;
    }

    private void pause(long millis){
        try{
            Thread.sleep(millis);
        }catch (InterruptedException ex){
            Thread.currentThread().interrupt();
            throw new LlmException("Прервано при ожидании повтора");
        }
    }
}
