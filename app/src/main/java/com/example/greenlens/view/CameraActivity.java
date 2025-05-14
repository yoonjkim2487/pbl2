package com.example.greenlens.view;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.databinding.ActivityCameraBinding;
import com.example.greenlens.model.response.AnalysisResultResponse;
import com.example.greenlens.model.response.AnalyzeResponse;
import com.example.greenlens.view.fragment.ResultBottomSheetDialog;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraActivity extends AppCompatActivity {
    private ActivityCameraBinding binding;
    private ImageCapture imageCapture;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private File currentPhotoFile;
    private ApiService apiService;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // API 서비스 초기화
        apiService = ApiClient.getInstance().getApiService();

        // SharedPreferences에서 토큰 가져오기
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        authToken = prefs.getString("auth_token", "");

        // 토큰이 없으면 에러 메시지 표시 후 종료
        if (authToken.isEmpty()) {
            Toast.makeText(this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bearer 접두사 추가
        if (!authToken.startsWith("Bearer ")) {
            authToken = "Bearer " + authToken;
        }

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnCapture.setOnClickListener(v -> takePhoto());
        binding.btnRetake.setOnClickListener(v -> {
            // 재촬영 버튼 클릭 시
            binding.viewFinder.setVisibility(View.VISIBLE);
            binding.capturedImageView.setVisibility(View.GONE);
            binding.btnCapture.setVisibility(View.VISIBLE);
            binding.btnRetake.setVisibility(View.GONE);
            binding.btnConfirm.setVisibility(View.GONE);
            startCamera();
        });
        binding.btnConfirm.setOnClickListener(v -> {
            // 확인 버튼 클릭 시 이미지 분석 시작
            binding.progressBar.setVisibility(View.VISIBLE);
            analyzeImage();
        });
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "카메라를 시작할 수 없습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        binding.overlayView.setVisibility(View.VISIBLE);

        currentPhotoFile = new File(getExternalCacheDir(),
                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.KOREA).format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(currentPhotoFile).build();

        imageCapture.takePicture(outputOptions, executor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        runOnUiThread(() -> {
                            binding.overlayView.setVisibility(View.GONE);
                            showCapturedImage();
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(() -> {
                            binding.overlayView.setVisibility(View.GONE);
                            Toast.makeText(CameraActivity.this, "사진 촬영에 실패했습니다: " + exception.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void showCapturedImage() {
        // 촬영된 이미지 표시
        binding.viewFinder.setVisibility(View.GONE);
        binding.capturedImageView.setVisibility(View.VISIBLE);
        binding.btnCapture.setVisibility(View.GONE);
        binding.btnRetake.setVisibility(View.VISIBLE);
        binding.btnConfirm.setVisibility(View.VISIBLE);

        // Glide를 사용하여 이미지 로드
        Glide.with(this)
                .load(currentPhotoFile)
                .into(binding.capturedImageView);
    }

    private void analyzeImage() {
        if (currentPhotoFile == null || !currentPhotoFile.exists()) {
            Toast.makeText(this, "이미지 파일이 없습니다.", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        // 이미지 파일을 MultipartBody.Part로 변환
        RequestBody requestFile = RequestBody.create(
                MediaType.parse("image/*"),
                currentPhotoFile
        );

        // 'image'라는 이름으로 파일 전송
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                "image",
                currentPhotoFile.getName(),
                requestFile
        );

        // API 호출
        apiService.analyzeImage(authToken, imagePart).enqueue(new Callback<AnalyzeResponse>() {
            @Override
            public void onResponse(Call<AnalyzeResponse> call, Response<AnalyzeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AnalyzeResponse analyzeResponse = response.body();
                    Long analysisId = analyzeResponse.getAnalysisId();

                    // 분석이 완료될 때까지 잠시 대기
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        getAnalysisResult(analysisId);
                    }, 2000); // 2초 후 결과 조회
                } else {
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(CameraActivity.this,
                                "이미지 분석 요청에 실패했습니다: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<AnalyzeResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(CameraActivity.this,
                            "네트워크 오류: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void getAnalysisResult(Long analysisId) {
        apiService.getAnalysisResult(authToken, analysisId).enqueue(new Callback<AnalysisResultResponse>() {
            @Override
            public void onResponse(Call<AnalysisResultResponse> call, Response<AnalysisResultResponse> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    AnalysisResultResponse result = response.body();
                    String disposalMethod = result.getDisposalMethod();
                    String type = result.getTypeForApp();

                    showResult(type, disposalMethod);
                } else {
                    // 에러 메시지가 있으면 표시, 없으면 기본 메시지 표시
                    String errorMessage = "결과 조회에 실패했습니다: " + response.code();
                    Toast.makeText(CameraActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                    // 테스트용 임의 결과 표시
                    showResult("plastic", null);
                }
            }

            @Override
            public void onFailure(Call<AnalysisResultResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(CameraActivity.this,
                        "네트워크 오류: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

                // 테스트용 임의 결과 표시
                showResult("plastic", null);
            }
        });
    }

    private void showResult(String type) {
        showResult(type, null);
    }

    private void showResult(String type, String disposalMethod) {
        ResultBottomSheetDialog bottomSheet = ResultBottomSheetDialog.newInstance(type, disposalMethod);
        bottomSheet.show(getSupportFragmentManager(), "result_bottom_sheet");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}