package com.example.greenlens.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.greenlens.manager.UserManager;
import com.example.greenlens.model.User;
import com.example.greenlens.model.response.AnalysisResultResponse;
import com.example.greenlens.model.response.AnalyzeResponse;        
import com.example.greenlens.util.DevLog;
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

import java.util.HashMap;
import java.util.Map;

public class CameraActivity extends AppCompatActivity {
    private ActivityCameraBinding binding;
    private ImageCapture imageCapture;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private File currentPhotoFile;
    private ApiService apiService;
    private String authToken;
    private UserManager userManager;
    private static final String TAG = "CameraActivity";

    // 카메라 관련 변수 추가
    private ProcessCameraProvider cameraProvider;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private boolean isCameraInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // API 서비스 초기화
        apiService = ApiClient.getInstance().getApiService();

        // UserManager 초기화 및 토큰 가져오기
        userManager = UserManager.getInstance(this);

        // 로그인 상태 확인
        if (!userManager.isLoggedIn()) {
            Toast.makeText(this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 인증 토큰 가져오기 (자동으로 Bearer 접두사 추가됨)
        authToken = userManager.getAuthToken();
        DevLog.d(TAG, "Auth token for API calls: " + authToken);

        // 토큰이 없으면 에러 메시지 표시 후 종료
        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(this, "로그인 세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
            // 로그인 화면으로 이동
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        if (allPermissionsGranted()) {
            // 권한이 있으면 onCreate에서는 카메라를 초기화만 하고 onResume에서 시작
            initCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 액티비티가 다시 보일 때 카메라 시작
        if (allPermissionsGranted()) {
            if (binding.viewFinder.getVisibility() == View.VISIBLE) {
                // 카메라 상태 확인 후 필요시 리소스 해제 및 재시작
                try {
                    if (cameraProvider != null) {
                        // 카메라 리소스가 이미 있는 경우 일단 해제
                        cameraProvider.unbindAll();
                    }

                    if (isCameraInitialized) {
                        startCamera();
                    } else {
                        initCamera();
                    }

                    DevLog.d(TAG, "onResume: 카메라 재시작 완료");
                } catch (Exception e) {
                    DevLog.e(TAG, "onResume: 카메라 재시작 실패", e);
                    // 초기화 상태 재설정 후 다시 시도
                    isCameraInitialized = false;
                    initCamera();
                }
            }
        } else if (!isFinishing()) {
            // 권한이 없는 경우 요청
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 액티비티가 일시 중지될 때 카메라 리소스 해제
        releaseCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 액티비티가 더 이상 보이지 않을 때 카메라 리소스 강제 해제
        if (cameraProvider != null) {
            try {
                DevLog.d(TAG, "onStop에서 카메라 리소스 해제");
                cameraProvider.unbindAll();
            } catch (Exception e) {
                DevLog.e(TAG, "onStop에서 카메라 리소스 해제 실패", e);
            }
        }
    }

    private void initCamera() {
        try {
            cameraProviderFuture = ProcessCameraProvider.getInstance(this);
            isCameraInitialized = true;
            startCamera();
        } catch (Exception e) {
            isCameraInitialized = false;
            DevLog.e(TAG, "카메라 초기화 실패", e);
            Toast.makeText(this, "카메라를 초기화할 수 없습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void releaseCamera() {
        try {
            if (cameraProvider != null) {
                DevLog.d(TAG, "카메라 리소스 해제 중...");
                cameraProvider.unbindAll();
            }
        } catch (Exception e) {
            DevLog.e(TAG, "카메라 리소스 해제 실패", e);
        }
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
                initCamera();
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startCamera() {
        if (cameraProviderFuture == null) {
            initCamera();
            return;
        }

        cameraProviderFuture.addListener(() -> {
            try {
                // 이전 인스턴스가 존재할 경우 먼저 해제
                if (cameraProvider != null) {
                    cameraProvider.unbindAll();
                }

                // 새로운 인스턴스 가져오기
                cameraProvider = cameraProviderFuture.get();

                // 예방 차원에서 다시 한 번 이전 바인딩 모두 해제
                cameraProvider.unbindAll();

                // 뷰가 보이지 않으면 카메라를 시작하지 않음
                if (binding.viewFinder.getVisibility() != View.VISIBLE) {
                    DevLog.d(TAG, "뷰파인더가 보이지 않아 카메라 시작 취소");
                    return;
                }

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                try {
                    // 카메라를 현재 액티비티의 라이프사이클에 바인딩
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                    DevLog.d(TAG, "카메라 시작 성공");
                } catch (Exception e) {
                    DevLog.e(TAG, "카메라 바인딩 오류", e);
                    Toast.makeText(this, "카메라 초기화 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException | InterruptedException e) {
                DevLog.e(TAG, "카메라 시작 실패", e);
                Toast.makeText(this, "카메라를 시작할 수 없습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                DevLog.e(TAG, "카메라 시작 중 예상치 못한 오류", e);
                Toast.makeText(this, "카메라 초기화 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) {
            DevLog.e(TAG, "imageCapture가 null입니다. 카메라가 초기화되지 않았습니다.");
            Toast.makeText(this, "카메라가 초기화되지 않았습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

            // 카메라 재초기화 시도
            try {
                if (cameraProvider != null) {
                    cameraProvider.unbindAll();
                }
                initCamera();
                // 재시도를 권장하는 메시지 표시
                Toast.makeText(this, "카메라를 다시 초기화했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                DevLog.e(TAG, "카메라 재초기화 실패", e);
            }
            return;
        }

        try {
            binding.overlayView.setVisibility(View.VISIBLE);

            // 파일명 생성 확인
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.KOREA).format(System.currentTimeMillis());
            currentPhotoFile = new File(getExternalCacheDir(), timeStamp + ".jpg");

            // 에러 처리를 위한 디렉토리 확인
            File directory = getExternalCacheDir();
            if (directory == null || !directory.exists()) {
                if (directory != null) {
                    boolean created = directory.mkdirs();
                    DevLog.d(TAG, "디렉토리 생성 결과: " + created);
                } else {
                    DevLog.e(TAG, "외부 캐시 디렉토리가 null입니다");
                    Toast.makeText(this, "저장 공간에 접근할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    binding.overlayView.setVisibility(View.GONE);
                    return;
                }
            }

            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(currentPhotoFile).build();

            imageCapture.takePicture(outputOptions, executor,
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            runOnUiThread(() -> {
                                binding.overlayView.setVisibility(View.GONE);
                                if (currentPhotoFile != null && currentPhotoFile.exists() && currentPhotoFile.length() > 0) {
                                    showCapturedImage();
                                } else {
                                    DevLog.e(TAG, "저장된 이미지 파일이 없거나 크기가 0입니다.");
                                    Toast.makeText(CameraActivity.this, "이미지 저장에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    // 카메라 재시작
                                    startCamera();
                                }
                            });
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            runOnUiThread(() -> {
                                binding.overlayView.setVisibility(View.GONE);
                                DevLog.e(TAG, "이미지 캡처 오류", exception);
                                Toast.makeText(CameraActivity.this, "사진 촬영에 실패했습니다: " + exception.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                // 카메라 재시작
                                startCamera();
                            });
                        }
                    });
        } catch (Exception e) {
            binding.overlayView.setVisibility(View.GONE);
            DevLog.e(TAG, "사진 촬영 중 예외 발생", e);
            Toast.makeText(this, "사진 촬영 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showCapturedImage() {
        // 사진이 찍혔으면 카메라 리소스 해제
        releaseCamera();

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

        // 로그인 및 토큰 상태 재확인
        if (!userManager.isLoggedIn()) {
            Toast.makeText(this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        // 이미지 파일을 MultipartBody.Part로 변환
        RequestBody requestFile = RequestBody.create(
                currentPhotoFile,
                MediaType.parse("image/*")
        );

        // 'image'라는 이름으로 파일 전송
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                "image",
                currentPhotoFile.getName(),
                requestFile
        );

        DevLog.d(TAG, "API 호출 시작 - 토큰: " + authToken);
        DevLog.d(TAG, "원본 토큰: " + userManager.getToken());
        DevLog.d(TAG, "로그인 상태: " + userManager.isLoggedIn());
        DevLog.d(TAG, "토큰 길이: " + (authToken != null ? authToken.length() : "null"));
        DevLog.d(TAG, "토큰 앞 20자: " + (authToken != null && authToken.length() > 20 ? authToken.substring(0, 20) + "..." : authToken));
        DevLog.d(TAG, "이미지 파일 경로: " + currentPhotoFile.getAbsolutePath());
        DevLog.d(TAG, "이미지 파일 크기: " + currentPhotoFile.length() + " bytes");

        // API 호출
        apiService.analyzeImage(authToken, imagePart).enqueue(new Callback<AnalyzeResponse>() {
            @Override
            public void onResponse(Call<AnalyzeResponse> call, Response<AnalyzeResponse> response) {
                DevLog.d(TAG, "API 응답 - 상태 코드: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    AnalyzeResponse analyzeResponse = response.body();
                    Long analysisId = analyzeResponse.getAnalysisId();

                    DevLog.d(TAG, "분석 ID: " + analysisId);

                    // 분석이 완료될 때까지 잠시 대기
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        getAnalysisResult(analysisId);
                    }, 2000); // 2초 후 결과 조회
                } else {
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        String errorCode = String.valueOf(response.code());
                        String errorMessage = "이미지 분석 요청에 실패했습니다: " + errorCode;

                        // 403 에러인 경우 특별 처리
                        if (response.code() == 403) {
                            errorMessage = "권한이 없습니다. 서버 응답을 확인해주세요.";
                            DevLog.e(TAG, "403 오류 발생 - 토큰: " + authToken);
                            DevLog.e(TAG, "403 오류 발생 - 원본 토큰: " + userManager.getToken());

                            // 임시로 토큰 삭제하지 않고 로그만 출력
                            // userManager.clearUserSession();
                        }

                        try {
                            // 에러 응답 바디를 확인하여 더 자세한 오류 정보 표시
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                DevLog.e(TAG, "에러 응답: " + errorBody);

                                if (!errorBody.isEmpty()) {
                                    errorMessage += "\n서버 응답: " + errorBody;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(CameraActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<AnalyzeResponse> call, Throwable t) {
                DevLog.e(TAG, "API 호출 실패", t);
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

                    // 포인트 적립 요청 추가
                    if (type != null && !type.isEmpty()) {
                        // 포인트 적립 금액 (백엔드 API에 따라 조정 필요)
                        int pointValue = 50;  // 기본값

                        // 분리수거 종류별 포인트 차등 지급 (예시)
                        if ("plastic".equalsIgnoreCase(type)) {
                            pointValue = 50;
                        } else if ("paper".equalsIgnoreCase(type)) {
                            pointValue = 30;
                        } else if ("glass".equalsIgnoreCase(type)) {
                            pointValue = 70;
                        } else if ("metal".equalsIgnoreCase(type)) {
                            pointValue = 100;
                        }

                        // 실제 포인트 적립 요청
                        addUserPoints(type, pointValue);

                        // 포인트 적립 안내 토스트 메시지
                        String wasteTypeKorean = getWasteTypeKorean(type);
                        Toast.makeText(CameraActivity.this,
                                wasteTypeKorean + " 분리수거 성공! " + pointValue + "P가 적립되었습니다.",
                                Toast.LENGTH_LONG).show();
                    }

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

    /**
     * 사용자 포인트를 적립하는 함수
     * @param wasteType 분리수거한 쓰레기 종류
     * @param pointValue 적립할 포인트 값
     */
    private void addUserPoints(String wasteType, int pointValue) {
        if (userManager == null || !userManager.isLoggedIn()) {
            DevLog.e(TAG, "포인트 적립 실패: 사용자가 로그인되어 있지 않습니다.");
            return;
        }

        User currentUser = userManager.getCurrentUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            DevLog.e(TAG, "포인트 적립 실패: 사용자 정보가 없습니다.");
            return;
        }

        Long userId = currentUser.getUserId();

        // 서버에 포인트 적립 요청 (API 예시, 실제 백엔드에 맞게 수정 필요)
        Map<String, Object> pointData = new HashMap<>();
        pointData.put("points", pointValue);
        pointData.put("reason", wasteType + " 분리수거 성공");
        pointData.put("type", "적립");

        // 실제 API 호출 (백엔드에 맞게 구현 필요)
        apiService.logRecycleActivity(authToken, pointData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    DevLog.d(TAG, "포인트 적립 성공: " + pointValue + "P");

                    // 현재 사용자의 포인트 업데이트 (UserManager에 메서드 추가 필요)
                    if (currentUser != null) {
                        int updatedPoints = currentUser.getPoints() + pointValue;
                        currentUser.setPoints(updatedPoints);
                        userManager.saveUser(currentUser);
                    }
                } else {
                    DevLog.e(TAG, "포인트 적립 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                DevLog.e(TAG, "포인트 적립 API 호출 실패", t);
            }
        });
    }

    // 쓰레기 유형을 한글로 변환
    private String getWasteTypeKorean(String wasteType) {
        switch (wasteType != null ? wasteType.toLowerCase() : "") {
            case "plastic":
                return "플라스틱";
            case "paper":
                return "종이";
            case "glass":
                return "유리";
            case "metal":
                return "금속";
            case "vinyl":
                return "비닐";
            case "styrofoam":
                return "스티로폼";
            default:
                return wasteType;
        }
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
        // 카메라 리소스 명시적 해제
        releaseCamera();
        binding = null;
    }
}