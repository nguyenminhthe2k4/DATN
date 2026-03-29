package com.example.DATN.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Vocabulary")
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "word", unique = true, length = 100)
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

    @Column(name = "level", length = 50)
    public String level;

    @Column(name = "created_at")
    public Date createdAt;

    public Vocabulary() {}
}

