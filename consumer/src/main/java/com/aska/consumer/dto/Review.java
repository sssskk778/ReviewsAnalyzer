package com.aska.consumer.dto;

import java.util.List;

public record Review(
        String text,
        String pros,
        String cons,
        int rating,
        List<String> bables,
        String color,
        String size,
        String matchingSize,
        String matchingPhoto,
        String matchingDescription
) {
    public boolean hasText() {
        return !text.isBlank() || !pros.isBlank() || !cons.isBlank();
    }

    public String fullText() {
        StringBuilder sb = new StringBuilder(text);
        if (!pros.isBlank()) sb.append(" Достоинства: ").append(pros);
        if (!cons.isBlank()) sb.append(" Недостатки: ").append(cons);
        return sb.toString().trim();
    }
}
