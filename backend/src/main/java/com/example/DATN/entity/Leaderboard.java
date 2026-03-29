package com.example.DATN.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Leaderboard")
public class Leaderboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @Column(name = "score")
    public Integer score;

    @Column(name = "rank")
    public Integer rank;

    public Leaderboard() {}
}

