package com.example.DATN.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "User_Vocabulary_Custom")
public class UserVocabularyCustom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @Column(name = "word", length = 100)
    public String word;

    @Column(name = "pronunciation", length = 100)
    public String pronunciation;

    @Column(name = "part_of_speech", length = 50)
    public String partOfSpeech;

    @Column(name = "meaning_en", columnDefinition = "TEXT")
    public String meaningEn;

    @Column(name = "meaning_vi", columnDefinition = "TEXT")
    public String meaningVi;

    @Column(name = "example", columnDefinition = "TEXT")
    public String example;

    @Column(name = "created_at")
    public Date createdAt;

    public UserVocabularyCustom() {}
}

