package com.example.greenlens.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.greenlens.R;
import com.example.greenlens.databinding.ActivityPointHistoryBinding;
import com.example.greenlens.manager.UserManager;
import com.example.greenlens.model.Point;
import com.example.greenlens.view.adapter.PointHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class PointHistoryActivity extends AppCompatActivity {
    private ActivityPointHistoryBinding binding;
    private PointHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPointHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뒤로가기 버튼 설정
        binding.btnBack.setOnClickListener(v -> finish());

        initViews();
        setupRecyclerView();
        loadSampleData(); // 샘플 데이터 로드
    }

    private void initViews() {
        // 이미 뒤로가기 버튼은 설정되었음
    }

    private void setupRecyclerView() {
        adapter = new PointHistoryAdapter();
        binding.recyclerPointHistory.setAdapter(adapter);
        binding.recyclerPointHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadSampleData() {
        // 샘플 데이터 생성
        List<Point> samplePoints = new ArrayList<>();
        samplePoints.add(new Point("2023-10-01", "플라스틱", 50, 50));
        samplePoints.add(new Point("2023-10-03", "종이", 30, 80));
        samplePoints.add(new Point("2023-10-05", "유리", 70, 150));

        // 어댑터에 데이터 설정
        adapter.setPoints(samplePoints);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
