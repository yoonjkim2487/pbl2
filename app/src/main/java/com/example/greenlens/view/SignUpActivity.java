package com.example.greenlens.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.databinding.ActivitySignUpBinding;
import com.example.greenlens.model.request.SignupRequest;
import com.example.greenlens.model.response.SignupResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();
        setupClickListeners();
    }

    private void setupViews() {
        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener(v -> finish());

        // 전체 동의 체크박스 설정
        binding.cbAllAgree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.cbTerms.setChecked(isChecked);
            binding.cbPrivacy.setChecked(isChecked);
            binding.cbMarketing.setChecked(isChecked);
        });

        // 개별 체크박스 변경 시 전체 동의 상태 업데이트
        setupCheckBoxListener();
    }

    private void setupCheckBoxListener() {
        binding.cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> updateAllAgreeState());
        binding.cbPrivacy.setOnCheckedChangeListener((buttonView, isChecked) -> updateAllAgreeState());
        binding.cbMarketing.setOnCheckedChangeListener((buttonView, isChecked) -> updateAllAgreeState());
    }

    private void updateAllAgreeState() {
        boolean allChecked = binding.cbTerms.isChecked() &&
                binding.cbPrivacy.isChecked() &&
                binding.cbMarketing.isChecked();
        binding.cbAllAgree.setChecked(allChecked);
    }

    private void setupClickListeners() {
        binding.btnSignUp.setOnClickListener(v -> {
            if (validateInputs() && checkRequiredAgreements()) {
                performSignUp();
            }
        });
        binding.btnBack.setOnClickListener(v -> finish());

        // 약관 상세보기 버튼들
        binding.tvTermsDetail.setOnClickListener(v -> showTermsDialog("서비스 이용약관"));
        binding.tvPrivacyDetail.setOnClickListener(v -> showTermsDialog("개인정보 처리방침"));
        binding.tvMarketingDetail.setOnClickListener(v -> showTermsDialog("마케팅 정보 수신"));
    }

    private boolean validateInputs() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String passwordConfirm = binding.etPasswordConfirm.getText().toString().trim();
        String nickname = binding.etNickname.getText().toString().trim();

        // 이메일 검증
        if (!isValidEmail(email)) {
            binding.etEmail.setError("올바른 이메일 형식이 아닙니다");
            return false;
        }

        // 비밀번호 검증
        if (password.length() < 8) {
            binding.etPassword.setError("비밀번호는 8자 이상이어야 합니다");
            return false;
        }

        // 비밀번호 확인
        if (!password.equals(passwordConfirm)) {
            binding.etPasswordConfirm.setError("비밀번호가 일치하지 않습니다");
            return false;
        }

        // 닉네임 검증
        if (nickname.isEmpty()) {
            binding.etNickname.setError("닉네임을 입력해주세요");
            return false;
        }

        return true;
    }

    private boolean checkRequiredAgreements() {
        if (!binding.cbTerms.isChecked() || !binding.cbPrivacy.isChecked()) {
            Toast.makeText(this, "필수 약관에 동의해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performSignUp() {
        showLoading(true);
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String username = binding.etNickname.getText().toString().trim();

        ApiService apiService = ApiClient.getInstance().getApiService();
        SignupRequest signupRequest = new SignupRequest(username, email, password);

        try {
            apiService.signup(signupRequest).enqueue(new Callback<SignupResponse>() {
                @Override
                public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SignUpActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        startLoginActivity();
                    } else {
                        String errorMessage = "회원가입에 실패했습니다.";
                        if (response.code() == 409) {
                            errorMessage = "이미 가입된 이메일입니다.";
                        } else if (response.code() == 400) {
                            errorMessage = "잘못된 요청입니다.";
                        }
                        Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SignupResponse> call, Throwable t) {
                    showLoading(false);
                    String errorMessage = "네트워크 오류가 발생했습니다: " + t.getMessage();
                    Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showTermsDialog(String title) {
        // 약관 내용 (실제로는 서버에서 받아오거나 리소스에서 가져와야 함)
        String content = "약관 내용이 들어갈 자리입니다...";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnSignUp.setEnabled(!show);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}