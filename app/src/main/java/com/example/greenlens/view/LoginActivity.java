package com.example.greenlens.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenlens.R;
import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.databinding.ActivityLoginBinding;
import com.example.greenlens.manager.UserManager;
import com.example.greenlens.model.request.LoginRequest;
import com.example.greenlens.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = UserManager.getInstance(this);

        // 이미 로그인된 경우 메인 화면으로 이동
        if (userManager.isLoggedIn()) {
            startMainActivity();
            finish();
            return;
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.tvSignUp.setOnClickListener(v -> startSignupActivity());

        // 소셜 로그인 버튼들
        binding.btnGoogle.setOnClickListener(v -> performGoogleLogin());
        binding.btnKakao.setOnClickListener(v -> performKakaoLogin());
        binding.btnNaver.setOnClickListener(v -> performNaverLogin());

        // 비밀번호 찾기
        binding.tvForgotPassword.setOnClickListener(v -> {
            // TODO: 비밀번호 찾기 기능 구현
            Toast.makeText(this, "비밀번호 찾기 기능 준비 중입니다.", Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        ApiService apiService = ApiClient.getInstance().getApiService();
        LoginRequest loginRequest = new LoginRequest(email, password);

        try {
            apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        userManager.saveUserSession(loginResponse.getToken(), loginResponse.getEmail());
                        startMainActivity();
                        finish();
                    } else {
                        String errorMessage = "로그인에 실패했습니다.";
                        if (response.code() == 401) {
                            errorMessage = "이메일 또는 비밀번호가 올바르지 않습니다.";
                        } else if (response.code() == 404) {
                            errorMessage = "존재하지 않는 계정입니다.";
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    showLoading(false);
                    String errorMessage = "네트워크 오류가 발생했습니다: " + t.getMessage();
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    t.printStackTrace(); // 로그캣에 에러 출력
                }
            });
        } catch (Exception e) {
            showLoading(false);
            String errorMessage = "예기치 않은 오류가 발생했습니다: " + e.getMessage();
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            e.printStackTrace(); // 로그캣에 에러 출력
        }
    }

    private void showLoading(boolean show) {
        binding.loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!show);

        // 로딩 중일 때는 모든 입력 필드와 버튼 비활성화
        binding.etEmail.setEnabled(!show);
        binding.etPassword.setEnabled(!show);
        binding.tvForgotPassword.setEnabled(!show);
        binding.tvSignUp.setEnabled(!show);
        binding.btnGoogle.setEnabled(!show);
        binding.btnKakao.setEnabled(!show);
        binding.btnNaver.setEnabled(!show);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startSignupActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void performGoogleLogin() {
        // TODO: Google 로그인 구현
        Toast.makeText(this, "Google 로그인 준비 중입니다.", Toast.LENGTH_SHORT).show();
    }

    private void performKakaoLogin() {
        // TODO: Kakao 로그인 구현
        Toast.makeText(this, "Kakao 로그인 준비 중입니다.", Toast.LENGTH_SHORT).show();
    }

    private void performNaverLogin() {
        // TODO: Naver 로그인 구현
        Toast.makeText(this, "Naver 로그인 준비 중입니다.", Toast.LENGTH_SHORT).show();
    }
}