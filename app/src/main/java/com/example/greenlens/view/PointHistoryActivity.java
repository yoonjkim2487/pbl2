package com.example.greenlens.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenlens.R;
import com.example.greenlens.databinding.ActivityPointHistoryBinding;

public class PointHistoryActivity extends AppCompatActivity {
    private ActivityPointHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPointHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뒤로가기 버튼 설정
        binding.btnBack.setOnClickListener(v -> finish());

        initViews();
    }

    private void initViews() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
