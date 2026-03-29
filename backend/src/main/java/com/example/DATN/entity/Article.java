package com.example.DATN.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "title", length = 255)
    public String title;

    @Column(name = "content", columnDefinition = "TEXT")
    public String content;

    @Column(name = "source", length = 255)
    public String source;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    public ArticleTopic topic;

    @Column(name = "created_at")
    public Date createdAt;

    public Article() {}
}

