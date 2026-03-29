package com.example.DATN.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Article_Topics")
public class ArticleTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "name", length = 100)
    public String name;

    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    public ArticleTopic() {}
}

