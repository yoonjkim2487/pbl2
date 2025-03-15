package com.example.greenlens.view;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.example.greenlens.databinding.ActivitySignUpBinding;

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
        // 회원가입 버튼
        binding.btnSignUp.setOnClickListener(v -> {
            if (validateInputs() && checkRequiredAgreements()) {
                performSignUp();
            }
        });

        // 약관 상세보기 버튼들
        binding.tvTermsDetail.setOnClickListener(v -> showTermsDialog("서비스 이용약관"));
        binding.tvPrivacyDetail.setOnClickListener(v -> showTermsDialog("개인정보 처리방침"));
        binding.tvMarketingDetail.setOnClickListener(v -> showTermsDialog("마케팅 정보 수신"));
    }

    private boolean validateInputs() {
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        String passwordConfirm = binding.etPasswordConfirm.getText().toString();
        String nickname = binding.etNickname.getText().toString();

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
        // TODO: 실제 회원가입 로직 구현
        // 임시 회원가입 성공 처리
        Toast.makeText(this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
        finish(); // 로그인 화면으로 돌아가기
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}