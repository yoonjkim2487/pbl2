package com.example.greenlens.repository;

import com.example.greenlens.model.User;

public class UserRepository {
    private static UserRepository instance;
    private User currentUser;

    private UserRepository() {
        // TODO: 실제 로그인된 사용자 정보를 서버에서 가져오도록 수정
        currentUser = new User("사용자 닉네임", "user@example.com", 5000);
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void updateUserInfo(User user) {
        // TODO: 서버에 사용자 정보 업데이트 요청
        this.currentUser = user;
    }
}