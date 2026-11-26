package com.aska.core.controller;

import com.aska.core.dto.AnalysisDetailsResponse;
import com.aska.core.dto.AnalysisResponse;
import com.aska.core.dto.ErrorResponse;
import com.aska.core.service.IAnalysisService;
import com.aska.core.service.impl.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Анализы", description = "Запуск анализа отзывов по товару и просмотр результатов")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalysisController {

    private final IAnalysisService analysisService;

    @Operation(
            summary = "Запустить анализ",
            description = "Создаёт анализ отзывов по товару и отправляет его в очередь на обработку. "
                    + "Результат появится позже: статус анализа можно проверить отдельным запросом"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Анализ принят и поставлен в очередь"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/projects/{projectId}/analyses")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AnalysisResponse create(@PathVariable Long projectId, Principal principal) {
        return analysisService.createAnalysis(projectId, principal.getName());
    }

    @Operation(
            summary = "Список анализов товара",
            description = "Возвращает все анализы выбранного товара"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список анализов получен"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/projects/{projectId}/analyses")
    public List<AnalysisResponse> list(@PathVariable Long projectId, Principal principal) {
        return analysisService.listAnalysis(projectId, principal.getName());
    }

    @Operation(
            summary = "Получить анализ",
            description = "Возвращает подробные результаты анализа, включая аспекты товара и рекомендации"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Подробный анализ получен"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Анализ не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/analyses/{analysisId}")
    public AnalysisDetailsResponse details(@PathVariable Long analysisId, Principal principal) {
        return analysisService.detailsAnalysis(analysisId, principal.getName());
    }
}