package com.example.greenlens.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.greenlens.R;
import com.example.greenlens.databinding.ActivityMainBinding;
import com.example.greenlens.view.fragment.CameraFragment;
import com.example.greenlens.view.fragment.HomeFragment;
import com.example.greenlens.view.fragment.MapFragment;
import com.example.greenlens.view.fragment.ProfileFragment;
import com.example.greenlens.view.fragment.RecycleGuideFragment;
import com.example.greenlens.view.fragment.ShopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.bottomNavigationView.setBackground(null);

        // NavHostFragment 가져오기
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // BottomNavigationView와 NavController 연결
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        // FloatingActionButton 클릭 시 cameraFragment로 이동
        binding.fab.setOnClickListener(v -> {
            navController.navigate(R.id.cameraFragment);
        });


    }

//    private void initView() {
//        // 바텀 네비게이션 배경 제거
//        binding.mainBottomNav.setBackground(null);
//
//        // 바텀 네비게이션 리스너 설정
//        binding.mainBottomNav.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.menu_home) {
//                changeFragment(new HomeFragment());
//                Log.d("MainActivity", "HomeFragment");
//                return true;
//            } else if (itemId == R.id.menu_map) {
//                changeFragment(new MapFragment());
//                Log.d("MainActivity", "MapFragment");
//                return true;
//            } else if (itemId == R.id.menu_shop) {
//                changeFragment(new ShopFragment());
//                Log.d("MainActivity", "ShopFragment");
//                return true;
//            } else if (itemId == R.id.menu_profile) {
//                changeFragment(new ProfileFragment());
//                Log.d("MainActivity", "ProfileFragment");
//                return true;
//            }
//            return false;
//        });
//
//        // 카메라 FAB 버튼 설정
//        binding.mainFloatingAddBtn.setOnClickListener(v -> {
//            changeFragment(new CameraFragment());
//            Log.d("MainActivity", "CameraFragment");
//        });
//    }
//
//    private void showInit() {
//        manager.beginTransaction()
//                .add(R.id.main_frm, new HomeFragment())
//                .commitAllowingStateLoss();
//    }
//
//    private void changeFragment(Fragment fragment) {
//        manager.beginTransaction()
//                .replace(R.id.main_frm, fragment)
//                .commitAllowingStateLoss();
//    }
//
//    // 분리수거 가이드 프래그먼트 표시
//    public void showRecycleGuide(String type) {
//        manager.beginTransaction()
//                .replace(R.id.main_frm, RecycleGuideFragment.newInstance(type))
//                .addToBackStack(null)  // 뒤로가기 스택에 추가
//                .commitAllowingStateLoss();
//    }
//
//    @Override
//    public void onBackPressed() {
//        Fragment currentFragment = manager.findFragmentById(R.id.main_frm);
//        if (currentFragment instanceof HomeFragment) {
//            super.onBackPressed();
//        } else if (currentFragment instanceof RecycleGuideFragment) {
//            // RecycleGuideFragment에서 뒤로가기 시 스택에서 제거
//            manager.popBackStack();
//        } else {
//            changeFragment(new HomeFragment());
//        }
//    }
//
//    // 하단 내비게이션 숨기기
//    public void hideBottomNavigation() {
//        if (binding.mainBottomNav != null) {
//            binding.mainBottomNav.setVisibility(View.GONE);
//            binding.mainFloatingAddBtn.setVisibility(View.GONE);
//            // 하단 네비게이션 배경도 함께 숨기기
//            binding.mainBottomNav.setBackground(null);
//        }
//    }
//
//    // 하단 내비게이션 보이기
//    public void showBottomNavigation() {
//        if (binding.mainBottomNav != null) {
//            binding.mainBottomNav.setVisibility(View.VISIBLE);
//            // FAB 버튼도 함께 보이기
//            binding.mainFloatingAddBtn.setVisibility(View.VISIBLE);
//        }
//    }

}