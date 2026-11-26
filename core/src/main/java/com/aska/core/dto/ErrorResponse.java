package com.aska.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "Ответ с описанием ошибки")
public record ErrorResponse(

        @Schema(description = "Путь запроса, в котором произошла ошибка")
        String path,

        @Schema(description = "HTTP-статус ошибки")
        int status,

        @Schema(description = "Описание ошибки")
        String message,

        @Schema(description = "Дата и время ошибки")
        LocalDateTime timestamp,

        @Schema(description = "Ошибки валидации по полям: ключ - название поля, значение - список сообщений")
        Map<String, List<String>> errors
) {

    public static ErrorResponse of(String path, int status, String message) {
        return new ErrorResponse(path, status, message, LocalDateTime.now(), Map.of());
    }
}
