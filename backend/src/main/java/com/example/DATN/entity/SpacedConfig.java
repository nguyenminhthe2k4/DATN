package com.example.DATN.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Spaced_Config")
public class SpacedConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "beta_0")
    public Double beta0;

    @Column(name = "beta_1")
    public Double beta1;

    @Column(name = "beta_2")
    public Double beta2;

    @Column(name = "beta_3")
    public Double beta3;

    @Column(name = "K")
    public Integer k;

    @Column(name = "max_interval")
    public Integer maxInterval;

    public SpacedConfig() {}
}

