package com.example.greenlens.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.databinding.ActivityEditProfileBinding;
import com.example.greenlens.manager.UserManager;
import com.example.greenlens.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private UserManager userManager;
    private ApiService apiService;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = UserManager.getInstance(this);
        apiService = ApiClient.getInstance().getApiService();

        setupViews();
        loadUserProfile();
    }

    private void setupViews() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> updateProfile());
    }

    private void loadUserProfile() {
        showLoading(true);
        String token = "Bearer " + userManager.getToken();

        apiService.getUserProfile(token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    updateUI(currentUser);
                } else {
                    Toast.makeText(EditProfileActivity.this, "프로필 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                Toast.makeText(EditProfileActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(User user) {
        binding.etNickname.setText(user.getUsername());
        binding.etEmail.setText(user.getEmail());
        binding.tvPoints.setText(String.valueOf(user.getPoints()));
        binding.tvRecycleCount.setText(String.valueOf(user.getRecycleCount()));
    }

    private void updateProfile() {
        if (!validateInputs()) return;

        showLoading(true);
        String token = "Bearer " + userManager.getToken();

        // 먼저 프로필 정보를 가져와서 userId를 얻습니다
        apiService.getUserProfile(token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if (user.getUserId() != null) {
                        // userId를 얻었으면 수정 진행
                        user.setUsername(binding.etNickname.getText().toString());
                        user.setEmail(binding.etEmail.getText().toString());
                        performUpdateProfile(token, user);
                    } else {
                        showLoading(false);
                        Toast.makeText(EditProfileActivity.this, "사용자 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showLoading(false);
                    Toast.makeText(EditProfileActivity.this, "사용자 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                Toast.makeText(EditProfileActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performUpdateProfile(String token, User user) {
        apiService.updateUserProfile(token, user.getUserId(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditProfileActivity.this, "프로필이 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    // 로그아웃 처리
                    userManager.clearUserSession();
                    // 로그인 화면으로 이동
                    Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "프로필 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                Toast.makeText(EditProfileActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        String nickname = binding.etNickname.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();

        if (nickname.isEmpty()) {
            binding.etNickname.setError("닉네임을 입력해주세요");
            return false;
        }

        if (email.isEmpty()) {
            binding.etEmail.setError("이메일을 입력해주세요");
            return false;
        }

        return true;
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnSave.setEnabled(!show);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}