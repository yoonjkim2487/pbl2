package com.example.greenlens.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenlens.R;
import com.example.greenlens.model.User;
import com.example.greenlens.repository.UserRepository;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etNickname;
    private EditText etEmail;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userRepository = UserRepository.getInstance();

        // 뒤로가기 버튼 설정
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // 뷰 초기화
        etNickname = findViewById(R.id.et_nickname);
        etEmail = findViewById(R.id.et_email);
        Button btnSave = findViewById(R.id.btn_save);

        // 현재 사용자 정보 표시
        User currentUser = userRepository.getCurrentUser();
        etNickname.setText(currentUser.getNickname());
        etEmail.setText(currentUser.getEmail());

        // 저장 버튼 클릭 리스너
        btnSave.setOnClickListener(v -> saveUserInfo());
    }

    private void saveUserInfo() {
        String newNickname = etNickname.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();

        if (newNickname.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 현재 사용자의 포인트는 유지
        User currentUser = userRepository.getCurrentUser();
        User updatedUser = new User(newNickname, newEmail, currentUser.getPoints());

        userRepository.updateUserInfo(updatedUser);
        Toast.makeText(this, "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}