package com.example.DATN.dto;

public record UpsertReadingTopicRequest(
        String name,
        String description,
        String defaultDifficulty,
        String status
) {
}
