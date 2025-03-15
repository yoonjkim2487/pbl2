package com.example.greenlens.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.greenlens.R;
import com.example.greenlens.view.adapter.CouponPageAdapter;  // 클래스명 수정
import com.example.greenlens.databinding.ActivityCouponHistoryBinding;

public class CouponHistoryActivity extends AppCompatActivity {
    private ActivityCouponHistoryBinding binding;
    private ViewPager2 viewPager;
    private TextView tabUnused;
    private TextView tabUsed;
    private View tabIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCouponHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        setupViewPager();
        setupClickListeners();
    }

    private void initViews() {
        viewPager = binding.viewPager;
        tabUnused = binding.tabUnused;
        tabUsed = binding.tabUsed;
        tabIndicator = binding.tabIndicator;

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupViewPager() {
        CouponPageAdapter pagerAdapter = new CouponPageAdapter(this);  // 클래스명 수정
        viewPager.setAdapter(pagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateTabs(position);
                moveIndicator(position);
            }
        });
    }

    private void setupClickListeners() {
        tabUnused.setOnClickListener(v -> viewPager.setCurrentItem(0));
        tabUsed.setOnClickListener(v -> viewPager.setCurrentItem(1));
    }

    private void updateTabs(int position) {
        if (position == 0) {
            tabUnused.setTextColor(getColor(R.color.black));
            tabUsed.setTextColor(getColor(R.color.gray));
        } else {
            tabUnused.setTextColor(getColor(R.color.gray));
            tabUsed.setTextColor(getColor(R.color.black));
        }
    }

    private void moveIndicator(int position) {
        float translationX = position == 0 ? 42 : viewPager.getWidth() / 2f + 42;
        tabIndicator.animate()
                .translationX(translationX)
                .setDuration(300)
                .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}