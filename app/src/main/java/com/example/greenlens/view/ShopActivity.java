package com.example.greenlens.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenlens.R;
import com.example.greenlens.model.Coupon;
import com.example.greenlens.repository.CouponRepository;
import com.example.greenlens.view.adapter.CouponAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {
    private static final String TAG = "ShopActivity";
    private RecyclerView recyclerCoupons;
    private CouponAdapter adapter;
    private CouponRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        repository = new CouponRepository();
        initCategories();
        setupCouponList();  // 쿠폰 리스트 설정 추가
    }

    private void initCategories() {
        // 각 카테고리 뷰 찾기
        View cafeView = findViewById(R.id.category_cafe);
        View restaurantView = findViewById(R.id.category_restaurant);
        View storeView = findViewById(R.id.category_store);
        View movieView = findViewById(R.id.category_movie);
        View etcView = findViewById(R.id.category_etc);

        // 디버깅을 위한 로그
        Log.d(TAG, "cafeView: " + (cafeView != null));
        Log.d(TAG, "restaurantView: " + (restaurantView != null));

        setupCategory(cafeView, R.drawable.ic_cafe, "카페");
        setupCategory(restaurantView, R.drawable.ic_restaurant, "식당");
        setupCategory(storeView, R.drawable.ic_cafe, "편의점");
        setupCategory(movieView, R.drawable.ic_cafe, "영화");
        setupCategory(etcView, R.drawable.ic_cafe, "기타");
    }

    private void setupCouponList() {
        recyclerCoupons = findViewById(R.id.recycler_products);  // activity_shop.xml의 RecyclerView ID
        recyclerCoupons.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new CouponAdapter();
        recyclerCoupons.setAdapter(adapter);

        // 쿠폰 클릭 리스너 설정
        adapter.setOnCouponClickListener((coupon, position) -> {
            // 쿠폰 교환 다이얼로그 표시 또는 처리
            Toast.makeText(this, coupon.getName() + " 교환하기", Toast.LENGTH_SHORT).show();
        });

        // 초기 전체 쿠폰 목록 표시
        loadCoupons("전체");
    }

    // 카테고리 선택 시 호출될 메소드
    private void loadCoupons(String category) {
        List<Coupon> coupons = new ArrayList<>();

        // 더미 데이터 생성 (나중에 repository로 이동)
        if (category.equals("전체") || category.equals("카페")) {
            coupons.add(new Coupon("스타벅스", "[스타벅스] 아메리카노", 5000, "카페", "2025-12-31", R.drawable.ic_cafe));
            coupons.add(new Coupon("투썸플레이스", "[투썸플레이스] 카페라떼", 5500, "카페", "2025-12-31", R.drawable.ic_cafe));
        }
        if (category.equals("전체") || category.equals("식당")) {
            coupons.add(new Coupon("버거킹", "[버거킹] 와퍼", 8000, "식당", "2025-12-31", R.drawable.ic_restaurant));
        }
        // ... 더 많은 카테고리 추가 가능

        adapter.setItems(coupons);
    }
    // setupCategory 메소드 수정
    private void setupCategory(View categoryView, int iconResId, String categoryName) {
        if (categoryView != null) {
            try {
                ImageView iconView = categoryView.findViewById(R.id.image_category);
                TextView textView = categoryView.findViewById(R.id.text_category);

                if (iconView != null && textView != null) {
                    iconView.setImageResource(iconResId);
                    textView.setText(categoryName);

                    categoryView.setOnClickListener(v -> {
                        Toast.makeText(this, categoryName + " 선택됨", Toast.LENGTH_SHORT).show();
                        loadCoupons(categoryName);  // 카테고리별 쿠폰 로드
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, categoryName + " 설정 중 오류 발생: " + e.getMessage());
            }
        }
    }
}