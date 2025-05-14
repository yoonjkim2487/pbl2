package com.example.greenlens.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.model.User;
import com.example.greenlens.repository.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String TAG = "UserManager";

    private static UserManager instance;
    private SharedPreferences prefs;
    private UserRepository userRepository;
    private ApiService apiService;

    private UserManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userRepository = UserRepository.getInstance(context);
        apiService = ApiClient.getInstance().getApiService();
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUserSession(String token, String email) {
        Log.d(TAG, "Saving user session - Token: " + token);

        // 토큰이 "Bearer "로 시작하는지 확인하고 저장
        String tokenToSave = token;
        if (token != null && !token.startsWith("Bearer ")) {
            tokenToSave = "Bearer " + token;
        }

        Log.d(TAG, "Final token to save: " + tokenToSave);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, tokenToSave);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();

        // 사용자 프로필 정보 가져오기
        userRepository.fetchUserProfile(tokenToSave, new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                // 프로필 정보 저장 완료
                userRepository.saveUser(user);
            }

            @Override
            public void onError(String message) {
                // 에러 처리
                Log.e(TAG, "Error fetching user profile: " + message);
                clearUserSession();
            }
        });
    }

    public void clearUserSession() {
        Log.d(TAG, "Clearing user session");
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_EMAIL);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
        userRepository.clearUser();
    }

    public String getToken() {
        String token = prefs.getString(KEY_TOKEN, null);
        Log.d(TAG, "Getting token: " + token);
        return token;
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public boolean isLoggedIn() {
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        String token = getToken();
        Log.d(TAG, "isLoggedIn check: " + isLoggedIn + ", token present: " + (token != null && !token.isEmpty()));

        // 로그인 상태이지만 토큰이 없는 경우, 로그인 상태를 false로 업데이트
        if (isLoggedIn && (token == null || token.isEmpty())) {
            Log.w(TAG, "User marked as logged in but token is missing, updating status");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, false);
            editor.apply();
            return false;
        }

        return isLoggedIn;
    }

    public User getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public void updateUserProfile(User user, UserRepository.UserProfileCallback callback) {
        String token = getToken();
        if (token != null) {
            userRepository.updateUserProfile(token, user, callback);
        } else {
            if (callback != null) {
                callback.onError("로그인이 필요합니다.");
            }
        }
    }

    public interface LogoutCallback {
        void onSuccess();
        void onError(String message);
    }

    public void logout(final LogoutCallback callback) {
        String token = getToken();
        if (token == null) {
            clearUserSession();
            if (callback != null) {
                callback.onSuccess();
            }
            return;
        }

        apiService.logout("Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                clearUserSession();
                if (callback != null) {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("로그아웃 실패");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (callback != null) {
                    callback.onError("네트워크 오류");
                }
            }
        });
    }

    public ApiService getApiService() {
        return apiService;
    }
} 