package com.example.greenlens.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenlens.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
    }

    private void setupClickListeners() {
        // 로그인 버튼
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (validateInputs(email, password)) {
                performLogin(email, password);
            }
        });

        // 회원가입 텍스트
        binding.tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

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

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            binding.etEmail.setError("이메일을 입력해주세요");
            return false;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("비밀번호를 입력해주세요");
            return false;
        }
        return true;
    }

    private void performLogin(String email, String password) {
        // TODO: 실제 로그인 로직 구현
        // 임시 로그인 성공 처리
        Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();
        startMainActivity();
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

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}