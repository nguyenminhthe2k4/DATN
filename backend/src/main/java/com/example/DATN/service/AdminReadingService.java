package com.example.DATN.service;

import com.example.DATN.dto.AdminReadingArticleDto;
import com.example.DATN.dto.AdminReadingTopicDto;
import com.example.DATN.dto.UpsertReadingArticleRequest;
import com.example.DATN.dto.UpsertReadingTopicRequest;
import com.example.DATN.entity.Article;
import com.example.DATN.entity.ArticleTopic;
import com.example.DATN.repository.ArticleRepository;
import com.example.DATN.repository.ArticleTopicRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminReadingService {
    private final ArticleRepository articleRepository;
    private final ArticleTopicRepository articleTopicRepository;

    public AdminReadingService(ArticleRepository articleRepository, ArticleTopicRepository articleTopicRepository) {
        this.articleRepository = articleRepository;
        this.articleTopicRepository = articleTopicRepository;
    }

    public List<AdminReadingArticleDto> findAllArticles() {
        return articleRepository.findArticleManagementRows().stream().map(this::toArticleDto).toList();
    }

    public AdminReadingArticleDto findArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        return new AdminReadingArticleDto(
                toLong(article.id),
                defaultString(article.title, ""),
                article.topic == null ? null : toLong(article.topic.id),
                article.topic == null ? "Chưa gán chủ đề" : defaultString(article.topic.name, "Chưa gán chủ đề"),
                "Trung bình",
                0,
                defaultString(article.source, ""),
                normalizeArticleStatus(null)
        );
    }

    public AdminReadingArticleDto createArticle(UpsertReadingArticleRequest request) {
        Article article = new Article();
        applyArticle(article, request);
        Article saved = articleRepository.save(article);
        return findArticleById(toLong(saved.id));
    }

    public AdminReadingArticleDto updateArticle(Long id, UpsertReadingArticleRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        applyArticle(article, request);
        articleRepository.save(article);
        return findArticleById(id);
    }

    public void deleteArticle(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found");
        }
        articleRepository.deleteById(id);
    }

    public List<AdminReadingTopicDto> findAllTopics() {
        return articleTopicRepository.findArticleTopicManagementRows().stream().map(this::toTopicDto).toList();
    }

    public AdminReadingTopicDto findTopicById(Long id) {
        ArticleTopic topic = articleTopicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading topic not found"));

        long articleCount = articleRepository.findArticleManagementRows().stream()
                .filter(row -> id.equals(row.getTopicId()))
                .count();

        return new AdminReadingTopicDto(
            toLong(topic.id),
                defaultString(topic.name, ""),
                defaultString(topic.description, ""),
            "Trung bình",
            "Hoạt động",
                articleCount
        );
    }

    public AdminReadingTopicDto createTopic(UpsertReadingTopicRequest request) {
        ArticleTopic topic = new ArticleTopic();
        applyTopic(topic, request);
        ArticleTopic saved = articleTopicRepository.save(topic);
        return findTopicById(toLong(saved.id));
    }

    public AdminReadingTopicDto updateTopic(Long id, UpsertReadingTopicRequest request) {
        ArticleTopic topic = articleTopicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading topic not found"));
        applyTopic(topic, request);
        articleTopicRepository.save(topic);
        return findTopicById(id);
    }

    public void deleteTopic(Long id) {
        long linkedArticles = articleRepository.findArticleManagementRows().stream()
                .filter(row -> id.equals(row.getTopicId()))
                .count();
        if (linkedArticles > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete topic with linked articles");
        }
        if (!articleTopicRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading topic not found");
        }
        articleTopicRepository.deleteById(id);
    }

    private void applyArticle(Article article, UpsertReadingArticleRequest request) {
        String title = request == null ? "" : defaultString(request.title(), "").trim();
        if (title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article title is required");
        }
        if (request == null || request.topicId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article topic is required");
        }

        ArticleTopic topic = articleTopicRepository.findById(request.topicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reading topic not found"));

        article.title = title;
        article.topic = topic;
        article.source = defaultString(request.sourceUrl(), "").trim();
    }

    private void applyTopic(ArticleTopic topic, UpsertReadingTopicRequest request) {
        String name = request == null ? "" : defaultString(request.name(), "").trim();
        if (name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reading topic name is required");
        }
        topic.name = name;
        topic.description = request == null ? "" : defaultString(request.description(), "").trim();
    }

    private AdminReadingArticleDto toArticleDto(ArticleRepository.ArticleManagementProjection row) {
        return new AdminReadingArticleDto(
                row.getId(),
                defaultString(row.getTitle(), ""),
                row.getTopicId(),
                defaultString(row.getTopicName(), "Chưa gán chủ đề"),
                "Trung bình",
                0,
                defaultString(row.getSource(), ""),
                normalizeArticleStatus(null)
        );
    }

    private AdminReadingTopicDto toTopicDto(ArticleTopicRepository.ArticleTopicManagementProjection row) {
        return new AdminReadingTopicDto(
                row.getId(),
                defaultString(row.getName(), ""),
                defaultString(row.getDescription(), ""),
                "Trung bình",
                "Hoạt động",
                row.getArticleCount() == null ? 0 : row.getArticleCount()
        );
    }

    private String normalizeArticleStatus(String value) {
        String normalized = defaultString(value, "Chờ biên tập").trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "da xuat ban", "đã xuất bản", "published" -> "Đã xuất bản";
            case "nhap", "nháp", "draft" -> "Nháp";
            default -> "Chờ biên tập";
        };
    }

    private String defaultString(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    private Long toLong(Integer value) {
        return value == null ? null : value.longValue();
    }
}
