package com.example.greenlens.view.fragment;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.greenlens.R;
import com.example.greenlens.view.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.graphics.Matrix;
import android.media.ExifInterface;
import java.io.IOException;

public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private FloatingActionButton captureButton;

    private ImageView capturedImageView; // 추가
    private View overlayView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewFinder = view.findViewById(R.id.viewFinder);
        captureButton = view.findViewById(R.id.btnCapture);
        capturedImageView = view.findViewById(R.id.capturedImageView);
        overlayView = view.findViewById(R.id.overlayView);

        // MainActivity의 하단 내비바 숨기기
        if (getActivity() instanceof MainActivity) {
//            ((MainActivity) getActivity()).hideBottomNavigation();
        }

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // 뒤로가기 버튼
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // 촬영 버튼
        captureButton.setOnClickListener(v -> {
            // 버튼 비활성화 (중복 클릭 방지)
            captureButton.setEnabled(false);
            takePhoto();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // MainActivity의 하단 내비바 다시 보이기
        if (getActivity() instanceof MainActivity) {
//            ((MainActivity) getActivity()).showBottomNavigation();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(requireActivity().getWindowManager()
                                .getDefaultDisplay().getRotation())
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        cameraSelector,
                        preview,
                        imageCapture
                );

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void takePhoto() {
        ImageCapture imageCapture = this.imageCapture;
        if (imageCapture == null) {
            captureButton.setEnabled(true);
            return;
        }

        // 촬영 시작 시 UI 업데이트
        captureButton.setEnabled(false);
        overlayView.setVisibility(View.VISIBLE);
        Toast.makeText(requireContext(), "사진을 촬영중입니다...", Toast.LENGTH_SHORT).show();

        File photoFile = new File(
                getOutputDirectory(),
                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                        .format(System.currentTimeMillis()) + ".jpg"
        );

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        // 촬영된 사진 표시
                        showCapturedImage(photoFile);

                        // 1.5초 후 결과창 표시
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            overlayView.setVisibility(View.GONE);
                            analyzeImage(photoFile);
                        }, 1500);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage());
                        Toast.makeText(requireContext(),
                                "사진 촬영에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        overlayView.setVisibility(View.GONE);
                        captureButton.setEnabled(true);
                    }
                }
        );
    }

    private void analyzeImage(File imageFile) {
        // TODO: 이미지 분석 로직 구현
        // 임시로 플라스틱으로 분류
        String resultType = "plastic";

        // 결과 BottomSheet 표시
        ResultBottomSheetDialog bottomSheet = ResultBottomSheetDialog.newInstance(resultType);
        bottomSheet.show(getChildFragmentManager(), "result");
    }

    private File getOutputDirectory() {
        File mediaDir = requireActivity().getExternalMediaDirs()[0];
        File appDir = new File(mediaDir, requireContext().getString(R.string.app_name));
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        return appDir;
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void showCapturedImage(File photoFile) {
        // PreviewView 숨기기
        viewFinder.setVisibility(View.GONE);

        // 촬영된 이미지 표시
        capturedImageView.setVisibility(View.VISIBLE);

        // 이미지 회전 및 비율 조정
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

        // EXIF 정보로부터 이미지 회전 각도 얻기
        int rotation = getImageRotation(photoFile);

        // 이미지 회전
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        capturedImageView.setImageBitmap(bitmap);
        captureButton.setEnabled(true);
    }

    private int getImageRotation(File imageFile) {
        try {
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error getting image rotation: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(requireContext(),
                        "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        }
    }
}