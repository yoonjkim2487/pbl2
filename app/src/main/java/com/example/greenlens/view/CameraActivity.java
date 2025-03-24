package com.example.greenlens.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.greenlens.databinding.ActivityCameraBinding;
import com.example.greenlens.view.fragment.ResultBottomSheetDialog;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    private ActivityCameraBinding binding;
    private ImageCapture imageCapture;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private File currentPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        // TODO: 여기서 이미지 분석 API 호출
        // 임시로 플라스틱으로 분류
        showResult("plastic");
    }

    private void showResult(String type) {
        ResultBottomSheetDialog bottomSheet = ResultBottomSheetDialog.newInstance(type);
        bottomSheet.show(getSupportFragmentManager(), "result_bottom_sheet");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}