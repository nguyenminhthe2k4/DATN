package com.example.DATN.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "User_Subscriptions")
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    public PremiumPlan plan;

    @Column(name = "start_date")
    public Date startDate;

    @Column(name = "end_date")
    public Date endDate;

    @Column(name = "status", length = 50)
    public String status;

    public UserSubscription() {}
}

