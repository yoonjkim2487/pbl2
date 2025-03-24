package com.example.greenlens.model;

public class User {
    private Long userId;
    private String username;
    private String email;
    private int points;
    private int recycleCount;
    private String createdAt;

    public User(Long userId, String username, String email, int points, int recycleCount, String createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.points = points;
        this.recycleCount = recycleCount;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRecycleCount() {
        return recycleCount;
    }

    public void setRecycleCount(int recycleCount) {
        this.recycleCount = recycleCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}