package com.example.greenlens.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenlens.databinding.ActivitySettingBinding;
import com.example.greenlens.manager.UserManager;
import com.example.greenlens.model.User;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = UserManager.getInstance(this);

        setupViews();
    }

    private void setupViews() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
        binding.btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("로그아웃")
                .setMessage("정말로 로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", (dialog, which) -> logout())
                .setNegativeButton("취소", null)
                .show();
    }

    private void logout() {
        showLoading(true);
        userManager.logout(new UserManager.LogoutCallback() {
            @Override
            public void onSuccess() {
                showLoading(false);
                // 로그인 화면으로 이동
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("계정 삭제")
                .setMessage("정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.")
                .setPositiveButton("삭제", (dialog, which) -> deleteAccount())
                .setNegativeButton("취소", null)
                .show();
    }

    private void deleteAccount() {
        showLoading(true);
        String token = "Bearer " + userManager.getToken();

        // 먼저 사용자 프로필 정보를 가져옵니다
        userManager.getApiService().getUserProfile(token).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(retrofit2.Call<User> call, retrofit2.Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User currentUser = response.body();
                    if (currentUser.getUserId() == null) {
                        showLoading(false);
                        Toast.makeText(SettingActivity.this, "사용자 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 사용자 정보를 가져온 후 계정 삭제 진행
                    performDeleteAccount(token, currentUser.getUserId().intValue());
                } else {
                    showLoading(false);
                    Toast.makeText(SettingActivity.this, "사용자 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<User> call, Throwable t) {
                showLoading(false);
                Toast.makeText(SettingActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performDeleteAccount(String token, int userId) {
        userManager.getApiService().deleteAccount(token, userId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    userManager.clearUserSession();
                    Toast.makeText(SettingActivity.this, "계정이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    // 로그인 화면으로 이동
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SettingActivity.this, "계정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                showLoading(false);
                Toast.makeText(SettingActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnEditProfile.setEnabled(!show);
        binding.btnLogout.setEnabled(!show);
        binding.btnDeleteAccount.setEnabled(!show);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}