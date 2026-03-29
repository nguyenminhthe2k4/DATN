package com.example.DATN.dto;

public record UpsertReadingArticleRequest(
        String title,
        Long topicId,
        String difficulty,
        String sourceUrl,
        String status
) {
}
