package com.example.DATN.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "title", length = 255)
    public String title;

    @Column(name = "url", length = 255)
    public String url;

    @Column(name = "transcript", columnDefinition = "TEXT")
    public String transcript;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    public YouTubeChannel channel;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    public Topic topic;

    public Video() {}
}

