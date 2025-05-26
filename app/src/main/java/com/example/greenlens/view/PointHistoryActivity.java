package com.example.greenlens.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.greenlens.R;
import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.databinding.ActivityPointHistoryBinding;
import com.example.greenlens.manager.UserManager;
import com.example.greenlens.model.Point;
import com.example.greenlens.repository.PointRepository;
import com.example.greenlens.util.DevLog;
import com.example.greenlens.view.adapter.PointHistoryAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointHistoryActivity extends AppCompatActivity {
    private ActivityPointHistoryBinding binding;
    private PointHistoryAdapter adapter;
    private UserManager userManager;
    private PointRepository pointRepository;
    private ApiService apiService;
    private static final String TAG = "PointHistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPointHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뒤로가기 버튼 설정
        binding.btnBack.setOnClickListener(v -> finish());

        userManager = UserManager.getInstance(this);
        pointRepository = PointRepository.getInstance();
        apiService = ApiClient.getInstance().getApiService();

        setupRecyclerView();

        // API 연동이 완료되면 실제 데이터 로드
        // 현재는 샘플 데이터 사용
        loadSamplePointHistory();

        // 실제 API 연동 코드 (주석 처리)
        // loadPointHistory();
    }

    private void setupRecyclerView() {
        adapter = new PointHistoryAdapter();
        binding.recyclerPointHistory.setAdapter(adapter);
        binding.recyclerPointHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * 샘플 포인트 내역 데이터 로드 (API 연동 전까지 사용)
     */
    private void loadSamplePointHistory() {
        // 로딩 상태 표시
        showLoading(true);

        // 샘플 데이터 생성
        List<Point> samplePoints = new ArrayList<>();
        samplePoints.add(new Point("2023-10-01", "플라스틱", 50, 50));
        samplePoints.add(new Point("2023-10-03", "종이", 30, 80));
        samplePoints.add(new Point("2023-10-05", "유리", 70, 150));
        samplePoints.add(new Point("2023-10-07", "플라스틱", 50, 200));
        samplePoints.add(new Point("2023-10-10", "캔", 40, 240));

        // 잠시 대기 후 데이터 표시 (로딩 효과)
        new android.os.Handler().postDelayed(() -> {
            showLoading(false);
            if (samplePoints.isEmpty()) {
                showEmptyView(true);
            } else {
                adapter.setPoints(samplePoints);
                showEmptyView(false);
            }
        }, 1000);
    }

    /**
     * 실제 포인트 내역 API 호출 메서드 (API 연동 시 사용)
     */
    private void loadPointHistory() {
        // 로딩 상태 표시
        showLoading(true);

        if (!userManager.isLoggedIn()) {
            showError("로그인이 필요합니다.");
            showEmptyView(true);
            showLoading(false);
            return;
        }

        String token = userManager.getToken();
        if (token == null || token.isEmpty()) {
            showError("로그인 세션이 만료되었습니다.");
            showEmptyView(true);
            showLoading(false);
            return;
        }

        DevLog.d(TAG, "포인트 내역 불러오기 시작...");

        // PointRepository를 통해 데이터 로드
        pointRepository.getPointHistory(token, new PointRepository.PointCallback<List<Point>>() {
            @Override
            public void onSuccess(List<Point> result) {
                runOnUiThread(() -> {
                    showLoading(false);
                    if (result != null && !result.isEmpty()) {
                        // 내림차순 정렬 (최신순)
                        Collections.reverse(result);
                        adapter.setPoints(result);
                        showEmptyView(false);
                        DevLog.d(TAG, "포인트 내역 " + result.size() + "개 로드 완료");
                    } else {
                        showEmptyView(true);
                        DevLog.d(TAG, "포인트 내역이 없습니다.");
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError(message);
                    // 오류 발생 시 빈 화면 표시
                    showEmptyView(true);
                    DevLog.e(TAG, "포인트 내역 로드 실패: " + message);
                });
            }
        });
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showEmptyView(boolean isEmpty) {
        binding.recyclerPointHistory.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
