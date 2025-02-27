package com.example.greenlens.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.greenlens.R;
import com.example.greenlens.view.fragment.CameraFragment;
import com.example.greenlens.view.fragment.HomeFragment;
import com.example.greenlens.view.fragment.MapFragment;
import com.example.greenlens.view.fragment.ProfileFragment;
import com.example.greenlens.view.fragment.ShopFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기 프래그먼트 설정 (HomeFragment)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // 하단 내비게이션 뷰 찾기
        View bottomNavLayout = findViewById(R.id.bottom_nav_include);

        // 각 버튼에 클릭 리스너 설정
        ImageView btnHome = bottomNavLayout.findViewById(R.id.btn_home);
        ImageView btnMap = bottomNavLayout.findViewById(R.id.btn_map);
        ImageView btnCamera = bottomNavLayout.findViewById(R.id.btn_camera);
        ImageView btnNotification = bottomNavLayout.findViewById(R.id.btn_notification);
        ImageView btnProfile = bottomNavLayout.findViewById(R.id.btn_profile);

        btnHome.setOnClickListener(v -> loadFragment(new HomeFragment()));
        btnMap.setOnClickListener(v -> loadFragment(new MapFragment()));
        btnCamera.setOnClickListener(v -> loadFragment(new CameraFragment()));
        btnNotification.setOnClickListener(v -> loadFragment(new ShopFragment()));
        btnProfile.setOnClickListener(v -> loadFragment(new ProfileFragment()));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}