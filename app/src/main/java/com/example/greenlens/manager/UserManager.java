package com.example.greenlens.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.model.User;
import com.example.greenlens.repository.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Map;

public class UserManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

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
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();

        // 사용자 프로필 정보 가져오기
        userRepository.fetchUserProfile(token, new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                // 프로필 정보 저장 완료
                userRepository.saveUser(user);
            }

            @Override
            public void onError(String message) {
                // 에러 처리
                clearUserSession();
            }
        });
    }

    public void clearUserSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_EMAIL);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
        userRepository.clearUser();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
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

    public interface PointsCallback {
        void onSuccess(int points, String lastUpdated);
        void onError(String message);
    }

    public void getUserPoints(PointsCallback callback) {
        String token = getToken();
        User currentUser = getCurrentUser();

        if (token == null || currentUser == null) {
            if (callback != null) {
                callback.onError("로그인이 필요합니다.");
            }
            return;
        }

        String authToken = "Bearer " + token;
        apiService.getUserPoints(authToken, currentUser.getId()).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> result = response.body();
                    try {
                        int points = ((Number) result.get("points")).intValue();
                        String lastUpdated = (String) result.get("last_updated");
                        if (callback != null) {
                            callback.onSuccess(points, lastUpdated);
                        }
                    } catch (Exception e) {
                        if (callback != null) {
                            callback.onError("포인트 정보를 처리하는 중 오류가 발생했습니다.");
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onError("포인트 정보를 가져오는데 실패했습니다.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (callback != null) {
                    callback.onError("네트워크 오류가 발생했습니다.");
                }
            }
        });
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