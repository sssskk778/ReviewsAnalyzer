package com.aska.consumer.llm;

import com.aska.consumer.dto.LlmAnalysisResult;
import com.aska.consumer.dto.Review;
import com.aska.consumer.exception.LlmException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewAnalyzer {

    private static final String POSITIVE = "positive";
    private static final String NEUTRAL = "neutral";
    private static final String NEGATIVE = "negative";

    private static final int ATTEMPTS = 2;
    private static final int MAX_ASPECTS = 15;
    private static final int MAX_NEGATIVE = 40;
    private static final int MAX_REVIEW_LENGTH = 500;
    private static final int MAX_ASPECT_NAME_LENGTH = 60;
    private static final Pattern JSON_OBJECT = Pattern.compile("\\{.*}", Pattern.DOTALL);

    private final LlmClient client;
    private final JsonMapper mapper = JsonMapper.builder().build();

    @Value("${analysis.batch-size:40}")
    private int batchSize;

    public LlmAnalysisResult analyze(List<Review> reviews) {

        String[] tones = new String[reviews.size()];
        for (int i = 0; i < reviews.size(); i++) {
            tones[i] = toneByRating(reviews.get(i).rating());
        }

        Map<String, AspectCounter> aspects = new LinkedHashMap<>();
        classifyInBatches(reviews, tones, aspects);

        int positive = countTone(tones, POSITIVE);
        int negative = countTone(tones, NEGATIVE);
        int neutral = tones.length - positive - negative;

        List<AspectCounter> topAspects = aspects.values().stream()
                .sorted((a,b) -> Integer.compare(b.mentions, a.mentions))
                .limit(MAX_ASPECTS)
                .toList();

        JsonNode summary = askSummary(reviews, tones, topAspects, positive, neutral, negative);
        List<LlmAnalysisResult.AspectStat> aspectStats = topAspects.stream()
                .map(aspect -> new LlmAnalysisResult.AspectStat(aspect.name, aspect.category,
                        aspect.mentions, aspect.positive, aspect.negative)).toList();
        return new LlmAnalysisResult(positive, neutral, negative, aspectStats,
                text(summary.path("summary")), text(summary.path("recommendations")));
    }

    private void classifyInBatches(List<Review>reviews, String[] tones, Map<String, AspectCounter> aspects){
        List<Integer> withText = new ArrayList<>();
        for (int i = 0; i < reviews.size(); i++) {
            if(reviews.get(i).hasText()){
                withText.add(i);
            }
        }

        for (int from = 0; from < withText.size(); from+=batchSize){
            int to = Math.min(from + batchSize, withText.size());
            classifyOneBatch(reviews,withText.subList(from,to),tones, aspects);

        }
    }
    private void classifyOneBatch(List<Review> reviews, List<Integer> batch, String[] tones, Map<String, AspectCounter> aspects){
        String prompt = Prompts.classification(numberedList(reviews,batch));

        for(int attempt = 1; attempt <= ATTEMPTS; attempt++){
            try {
                String[] batchTones = new String[batch.size()];
                Map<String,AspectCounter> batchAspects = new LinkedHashMap<>();
                readClassification(client.complete(prompt), batchTones, batchAspects);

                for(int i = 0; i < batch.size(); i++){
                    if (batchTones[i] != null){
                        tones[batch.get(i)] = batchTones[i];
                    }
                }
                for(AspectCounter counter : batchAspects.values()){
                    aspects.computeIfAbsent(counter.name, n -> new AspectCounter(n, counter.category))
                            .merge(counter);
                }
                return;
            }catch (LlmException ex){
                throw ex;
            }catch (Exception ex){
                log.warn("Не удалось разобрать ответ LLM (попытка {}/{}): {}", attempt,ATTEMPTS,ex.getMessage());
            }
        }
        log.warn("Пакет пропущен: используется тон на основе рейтинга для этих отзывов");
    }

    private void readClassification(String answer, String[] batchTones, Map<String,AspectCounter> batchAspects){
        JsonNode items = parseJson(answer).path("reviews");
        if(!items.isArray()){
            throw new IllegalStateException("Массив 'reviews' не найден в ответе");
        }

        for(JsonNode item : items){
            int index = item.path("id").asInt(0) - 1;
            if(index < 0 || index >= batchTones.length){
                continue;
            }
            batchTones[index] = normalizeTone(item.path("tone").textValue());

            JsonNode aspectNodes = item.path("aspects");
            if(!aspectNodes.isArray()){
                continue;
            }
            for(JsonNode aspect : aspectNodes) {
                String name = normalizeName(aspect.path("name").textValue());
                if (name.isEmpty()) {
                    continue;
                }
                String category = text(aspect.path("category"));
                String sentiment = normalizeTone(aspect.path("sentiment").textValue());
                batchAspects.computeIfAbsent(name, n -> new AspectCounter(n, category))
                        .add(sentiment == null ? NEUTRAL : sentiment);
            }
        }
    }

    private JsonNode askSummary(List<Review> reviews, String[] tones, List<AspectCounter> topAspects,
                                int positive, int neutral, int negative){
        String stats = statsText(reviews.size(), positive, neutral, negative, topAspects);
        String negativeReviews = negativeReviewsText(reviews,tones);
        String prompt = Prompts.summary(stats, negativeReviews);

        for(int attempt = 1; attempt <= ATTEMPTS; attempt++){
            try {
                JsonNode answer = parseJson(client.complete(prompt));
                if(text(answer.path("summary")).isBlank() || text(answer.path("recommendations")).isBlank()){
                    throw new IllegalStateException("В ответе отсутствуют 'summary' или 'recommendations'");
                }
                return answer;
            }catch (LlmException ex){
                throw ex;
            }catch (Exception ex){
                log.warn("Не удалось прочитать сводку от LLM (попытка {}/{}): {}", attempt, ATTEMPTS, ex.getMessage());
            }
        }
        throw new LlmException("LLM вернул недопустимую сводку");
    }


    private String numberedList(List<Review> reviews, List<Integer> batch){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < batch.size(); i++) {
            sb.append(i + 1).append(". ").append(clean(reviews.get(batch.get(i)).fullText())).append("\n");
        }
        return sb.toString();
    }



    private String toneByRating(int rating) {
        if (rating >= 4) {
            return POSITIVE;
        }
        if (rating == 3) {
            return NEUTRAL;
        }
        return NEGATIVE;
    }

    private static final class AspectCounter {
        private final String name;
        private final String category;
        private int mentions;
        private int positive;
        private int negative;

        private AspectCounter(String name, String category) {
            this.name = name;
            this.category = category;
        }

        private void add(String sentiment) {
            mentions++;
            if (sentiment.equals(POSITIVE)) {
                positive++;
            } else if (sentiment.equals(NEGATIVE)) {
                negative++;
            }
        }

        private void merge(AspectCounter other) {
            mentions += other.mentions;
            positive += other.positive;
            negative += other.negative;
        }
    }

    private String clean(String text){
        String oneLine = text.replace('\n',' ').replace('\r', ' ').trim();
        return oneLine.length() > MAX_REVIEW_LENGTH ? oneLine.substring(0,MAX_REVIEW_LENGTH) : oneLine;
    }

    private JsonNode parseJson(String content){
        try{
            return mapper.readTree(content);
        }catch (Exception ex){
            Matcher matcher = JSON_OBJECT.matcher(content);
            if(matcher.find()){
                return mapper.readTree(matcher.group());
            }
            throw new IllegalArgumentException("JSON не найден в ответе LLM");
        }
    }

    private String normalizeTone(String raw){
        if(raw == null){
            return null;
        }
        String tone = raw.trim().toLowerCase(Locale.ROOT);
        if(tone.startsWith("pos") || tone.startsWith("позит")){
            return POSITIVE;
        }
        if(tone.startsWith("neg") || tone.startsWith("негат") || tone.startsWith("отриц")){
            return NEGATIVE;
        }
        if(tone.startsWith("neu") || tone.startsWith("нейтр")){
            return NEUTRAL;
        }
        return null;
    }

    private String normalizeName(String raw){
        if(raw == null){
            return "";
        }
        String name = raw.trim().toLowerCase(Locale.ROOT);
        return name.length() > MAX_ASPECT_NAME_LENGTH ? name.substring(0,MAX_ASPECT_NAME_LENGTH) : name;
    }

    private String text(JsonNode node){
        if(node.isArray()){
            List<String> parts = new ArrayList<>();
            for(JsonNode element : node){
                String value = element.textValue();
                if(value != null && !value.isBlank()){
                    parts.add(value.trim());
                }
            }
            return String.join(" ", parts);
        }
        String value = node.textValue();
        return value == null ? "" : value.trim();
    }

    private int countTone(String[] tones, String tone){
        int count = 0;
        for(String t : tones){
            if(t.equals(tone)){
                count++;
            }
        }
        return count;
    }

    private String statsText(int total, int positive, int neutral, int negative, List<AspectCounter> topAspects) {
        StringBuilder sb = new StringBuilder();
        sb.append("Всего отзывов: ").append(total)
                .append(". Положительных: ").append(positive)
                .append(", нейтральных: ").append(neutral)
                .append(", отрицательных: ").append(negative).append(".\n");
        for (AspectCounter a : topAspects) {
            sb.append("Тема «").append(a.name).append("»: упоминаний ").append(a.mentions)
                    .append(" (положительных ").append(a.positive)
                    .append(", отрицательных ").append(a.negative).append(")\n");
        }
        return sb.toString();
    }

    private String negativeReviewsText(List<Review> reviews, String[] tones){
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for(int i = 0; i < reviews.size() && count < MAX_NEGATIVE; i++){
            if (tones[i].equals(NEGATIVE) && reviews.get(i).hasText()){
                sb.append("- ").append(clean(reviews.get(i).fullText())).append("\n");
                count++;
            }
        }
        if (count == 0){
            sb.append("Отрицательных отзывов с текстом не найдено.\n");
        }
        return sb.toString();
    }
}