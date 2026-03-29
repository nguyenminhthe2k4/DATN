package com.example.DATN.service;

import com.example.DATN.dto.AdminVocabularyDto;
import com.example.DATN.dto.UpsertVocabularyRequest;
import com.example.DATN.entity.Vocabulary;
import com.example.DATN.repository.LessonRepository;
import com.example.DATN.repository.VocabularyRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminVocabularyService {
    private final VocabularyRepository vocabularyRepository;
    private final LessonRepository lessonRepository;

    public AdminVocabularyService(VocabularyRepository vocabularyRepository, LessonRepository lessonRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.lessonRepository = lessonRepository;
    }

    public List<AdminVocabularyDto> findAll() {
        return vocabularyRepository.findVocabularyManagementRows().stream().map(this::toDto).toList();
    }

    public AdminVocabularyDto findById(Long id) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vocabulary not found"));

        return new AdminVocabularyDto(
                toLong(vocabulary.id),
                defaultString(vocabulary.word, ""),
                defaultString(vocabulary.pronunciation, ""),
                defaultString(vocabulary.partOfSpeech, "noun"),
                defaultString(vocabulary.meaningEn, ""),
                defaultString(vocabulary.meaningVi, ""),
                defaultString(vocabulary.example, ""),
                normalizeDifficulty(vocabulary.level),
                "Đã duyệt",
                null,
                null
        );
    }

    public AdminVocabularyDto create(UpsertVocabularyRequest request) {
        Vocabulary vocabulary = new Vocabulary();
        apply(vocabulary, request);
        try {
            Vocabulary saved = vocabularyRepository.save(vocabulary);
            return findById(toLong(saved.id));
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vocabulary word already exists");
        }
    }

    public AdminVocabularyDto update(Long id, UpsertVocabularyRequest request) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vocabulary not found"));

        apply(vocabulary, request);
        try {
            vocabularyRepository.save(vocabulary);
            return findById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vocabulary word already exists");
        }
    }

    public void delete(Long id) {
        if (!vocabularyRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vocabulary not found");
        }
        vocabularyRepository.deleteById(id);
    }

    private void apply(Vocabulary vocabulary, UpsertVocabularyRequest request) {
        String word = request == null ? "" : defaultString(request.word(), "").trim();
        if (word.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vocabulary word is required");
        }

        if (request == null || request.lessonId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lesson is required");
        }

        lessonRepository.findById(request.lessonId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lesson not found"));

        vocabulary.word = word;
        vocabulary.pronunciation = defaultString(request.pronunciation(), "").trim();
        vocabulary.partOfSpeech = defaultString(request.partOfSpeech(), "noun").trim();
        vocabulary.meaningEn = defaultString(request.meaningEn(), "").trim();
        vocabulary.meaningVi = defaultString(request.meaningVi(), "").trim();
        vocabulary.example = defaultString(request.example(), "").trim();
        vocabulary.level = normalizeDifficulty(request.level());
    }

    private AdminVocabularyDto toDto(VocabularyRepository.VocabularyManagementProjection row) {
        return new AdminVocabularyDto(
                row.getId(),
                defaultString(row.getWord(), ""),
                defaultString(row.getPronunciation(), ""),
                defaultString(row.getPartOfSpeech(), "noun"),
                defaultString(row.getMeaningEn(), ""),
                defaultString(row.getMeaningVi(), ""),
                defaultString(row.getExample(), ""),
                normalizeDifficulty(row.getLevel()),
                "Đã duyệt",
                null,
                null
        );
    }

    private String normalizeDifficulty(String value) {
        String normalized = defaultString(value, "Trung bình").trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "co ban", "cơ bản", "basic" -> "Cơ bản";
            case "nang cao", "nâng cao", "advanced" -> "Nâng cao";
            default -> "Trung bình";
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
