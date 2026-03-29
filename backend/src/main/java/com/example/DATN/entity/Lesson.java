package com.example.DATN.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    public Topic topic;

    @Column(name = "name", length = 100)
    public String name;

    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    public Lesson() {}
}

