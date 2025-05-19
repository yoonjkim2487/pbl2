package com.example.greenlens.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.model.User;
import com.example.greenlens.repository.UserRepository;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String TAG = "UserManager";

    private static UserManager instance;
    private final SharedPreferences preferences;
    private final Gson gson;
    private final ApiService apiService;
    private User currentUser;
    private String token;
    private UserRepository userRepository;

    private UserManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        apiService = ApiClient.getInstance().getApiService();
        userRepository = UserRepository.getInstance(context);
        loadUser();
        loadToken();
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUser(User user) {
        currentUser = user;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
    }

    private void loadUser() {
        String userJson = preferences.getString(KEY_USER, null);
        if (userJson != null) {
            currentUser = gson.fromJson(userJson, User.class);
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void saveToken(String token) {
        this.token = token;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    private void loadToken() {
        token = preferences.getString(KEY_TOKEN, null);
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, null);
    }

    public boolean isLoggedIn() {
        boolean tokenExists = token != null && !token.isEmpty();
        boolean isLoggedInPref = preferences.getBoolean(KEY_IS_LOGGED_IN, false);
        boolean tokenNotExpired = !isTokenExpired();

        Log.d(TAG, "isLoggedIn check - tokenExists: " + tokenExists +
                ", isLoggedInPref: " + isLoggedInPref +
                ", tokenNotExpired: " + tokenNotExpired);

        // 메모리에 토큰이 있고, SharedPreferences에 로그인 상태가 저장되어 있고, 토큰이 만료되지 않아야 로그인된 것으로 간주
        return tokenExists && isLoggedInPref && tokenNotExpired;
    }

    public void fetchUserProfile(String token, UserProfileCallback callback) {
        String authToken = token;
        if (token != null && !token.startsWith("Bearer ")) {
            authToken = "Bearer " + token;
        }

        apiService.getUserProfile(authToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    saveUser(user);
                    callback.onSuccess(user);
                } else {
                    callback.onError("사용자 정보를 가져오는데 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("네트워크 오류가 발생했습니다: " + t.getMessage());
            }
        });
    }

    public void updateUserProfile(User user, UserProfileCallback callback) {
        if (user == null || user.getUserId() == null || token == null) {
            callback.onError("사용자 정보가 올바르지 않습니다.");
            return;
        }

        apiService.updateUserProfile("Bearer " + token, user.getUserId(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body();
                    saveUser(updatedUser);
                    callback.onSuccess(updatedUser);
                } else {
                    callback.onError("사용자 정보 수정에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("네트워크 오류가 발생했습니다: " + t.getMessage());
            }
        });
    }

    public void logout() {
        currentUser = null;
        token = null;
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_USER);
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_EMAIL);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
        userRepository.clearUser();
    }

    public void logout(LogoutCallback callback) {
        try {
            logout();
            callback.onSuccess();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
            callback.onError("로그아웃 중 오류가 발생했습니다.");
        }
    }

    public interface LogoutCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface UserProfileCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public void saveUserSession(String token, String email) {
        Log.d(TAG, "Saving user session - Token: " + token);

        // 토큰 저장 (Bearer 접두사 없이 원본 토큰만 저장)
        String tokenToSave = token;
        if (token != null && token.startsWith("Bearer ")) {
            tokenToSave = token.substring(7);
        }

        Log.d(TAG, "Cleaned token to save: " + tokenToSave);
        this.token = tokenToSave;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN, tokenToSave);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // 토큰 만료 시간 저장 (예: 현재 시간 + 24시간)
        long expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24시간
        editor.putLong("token_expires_at", expiresAt);

        editor.apply();

        // 사용자 프로필 정보 가져오기
        fetchUserProfile(tokenToSave, new UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                // 프로필 정보 저장 완료
                Log.d(TAG, "User profile fetched successfully: " + user.getUsername());
                userRepository.saveUser(user);
            }

            @Override
            public void onError(String message) {
                // 에러 처리
                Log.e(TAG, "Error fetching user profile: " + message);
                logout();
            }
        });
    }

    public boolean isTokenExpired() {
        long expiresAt = preferences.getLong("token_expires_at", 0);
        return expiresAt < System.currentTimeMillis();
    }

    public String getAuthToken() {
        // API 호출에 사용할 인증 토큰 반환 (Bearer 접두사 포함)
        if (token == null || token.isEmpty()) {
            return null;
        }

        if (token.startsWith("Bearer ")) {
            return token;
        } else {
            return "Bearer " + token;
        }
    }

    public void clearUserSession() {
        Log.d(TAG, "Clearing user session");
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_EMAIL);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
        logout();
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ApiService getApiService() {
        return apiService;
    }
} 