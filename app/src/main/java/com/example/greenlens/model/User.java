package com.example.greenlens.model;

public class User {
    private String nickname;
    private String email;
    private int points;

    public User(String nickname, String email, int points) {
        this.nickname = nickname;
        this.email = email;
        this.points = points;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
}