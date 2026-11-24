package com.aska.consumer.parser;

import com.aska.consumer.dto.Review;
import com.aska.consumer.exception.ParserException;
import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class WildberriesParser {

    private static final Pattern NM_ID = Pattern.compile("/catalog/(\\d+)");
    private static final int MAX_REVIEWS = 500;
    private static final String USER_AGENT =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36";
    private JsonMapper mapper = new JsonMapper().builder().build();

    public List<Review> fetchReviews(String productUrl){
        long nmId = extractNmId(productUrl);
        String json = downloadReviewsJson(nmId);
        if(json == null){
            return List.of();
        }
        return parse(json, nmId);
    }

    private long extractNmId(String productUrl) {
        Matcher matcher = NM_ID.matcher(productUrl);
        if(!matcher.find()){
            throw new ParserException("Неверный URL продукта");
        }
        return Long.parseLong(matcher.group(1));
    }

    private String downloadReviewsJson(long nmId){
        String pageUrl = "https://www.wildberries.ru/catalog/" + nmId + "/feedbacks";
        try(Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false))){
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setLocale("ru-RU")
                    .setUserAgent(USER_AGENT)
            );
            Page page = context.newPage();

            Response response = page.waitForResponse(
                    res -> res.url().contains("/imt/list/") && res.url().endsWith(".json"),
                    new Page.WaitForResponseOptions().setTimeout(20_000),
                    ()-> page.navigate(pageUrl)

            );
            if(response.status() != 200){
                log.warn("Файл отзывов для nmId {} вернул статус {}", nmId, response.status());
                return null;
            }
            return response.text();
        }catch (TimeoutError error){
            log.warn("Запрос отзывов не появился для nmId {}", nmId);
            return null;
        }catch (Exception ex){
            throw new ParserException("Не удалось загрузить отзывы: " + ex.getMessage(), ex);
        }
    }

    private List<Review> parse(String json, long nmId){
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode items = root.isArray() ? root : root.path("feedbacks");

            List<Review> result = new ArrayList<>();
            for(JsonNode item : items){
                if(item.path("nmId").asLong() != nmId){
                    continue;
                }
                int rating = item.path("productValuation").asInt(0);
                if(rating < 1 || rating > 5){
                    continue;
                }
                result.add(new Review(
                        str(item,"text"),
                        str(item,"pros"),
                        str(item, "cons"),
                        rating,
                        strings(item.path("bables")),
                        str(item,"color"),
                        str(item,"size"),
                        str(item,"matchingSize"),
                        str(item,"matchingPhoto"),
                        str(item, "matchingDescription")
                ));
                if(result.size() > MAX_REVIEWS){
                    break;
                }
            }
            return result;
        }catch (Exception ex){
            throw  new ParserException("Не удалось разобрать JSON отзывов", ex);
        }
    }

    private String str(JsonNode item, String field){
        String value = item.path(field).textValue();
        return value == null ? "" : value.trim();
    }

    private List<String> strings(JsonNode array){
        List<String> result = new ArrayList<>();
        for(JsonNode node : array){
            String value = node.textValue();
            if(value != null && !value.isBlank()){
                result.add(value);
            }
        }
        return result;
    }
}
