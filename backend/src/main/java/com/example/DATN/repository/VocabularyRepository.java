package com.example.DATN.repository;

import com.example.DATN.entity.Vocabulary;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(LocalDateTime start, LocalDateTime end);

    @Query("""
            select v.id as id,
                   v.word as word,
                   v.pronunciation as pronunciation,
                   v.partOfSpeech as partOfSpeech,
                   v.meaningEn as meaningEn,
                   v.meaningVi as meaningVi,
                   v.example as example,
                   v.level as level,
                     true as status,
                     null as lessonId,
                     null as topicId
            from Vocabulary v
                 group by v.id, v.word, v.pronunciation, v.partOfSpeech, v.meaningEn, v.meaningVi, v.example, v.level
            order by v.id desc
            """)
    List<VocabularyManagementProjection> findVocabularyManagementRows();

    interface VocabularyManagementProjection {
        Long getId();

        String getWord();

        String getPronunciation();

        String getPartOfSpeech();

        String getMeaningEn();

        String getMeaningVi();

        String getExample();

        String getLevel();

        Boolean getStatus();

        Long getLessonId();

        Long getTopicId();
    }
}
