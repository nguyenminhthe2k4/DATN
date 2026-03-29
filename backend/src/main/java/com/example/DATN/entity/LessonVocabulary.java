package com.example.DATN.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Lesson_Vocabulary")
@IdClass(LessonVocabularyId.class)
public class LessonVocabulary {
    @Id
    @Column(name = "lesson_id")
    public Integer lessonId;

    @Id
    @Column(name = "vocab_id")
    public Integer vocabId;

    public LessonVocabulary() {}
}

