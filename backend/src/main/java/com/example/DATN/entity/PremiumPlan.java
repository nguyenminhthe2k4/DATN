package com.example.DATN.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Premium_Plans")
public class PremiumPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "name", length = 100)
    public String name;

    @Column(name = "price")
    public Double price;

    @Column(name = "duration")
    public Integer duration;

    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    public PremiumPlan() {}
}

