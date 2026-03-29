package com.example.DATN.dto;

public record AdminReadingArticleDto(
        Long id,
        String title,
        Long topicId,
        String topic,
        String difficulty,
        int wordsHighlighted,
        String sourceUrl,
        String status
) {
}
