package com.example.DATN.dto;

public record UpsertVideoRequest(
        String title,
        String youtubeUrl,
        Long channelId,
        Long topicId,
        String difficulty,
        String duration,
        String status
) {
}
