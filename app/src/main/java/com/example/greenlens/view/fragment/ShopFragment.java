package com.example.greenlens.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenlens.R;
import com.example.greenlens.model.Coupon;
import com.example.greenlens.repository.CouponRepository;
import com.example.greenlens.view.adapter.ShopCouponAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {
    private static final String TAG = "ShopFragment";
    private RecyclerView recyclerCoupons;
    private ShopCouponAdapter adapter;  // 여기를 수정
    private CouponRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new CouponRepository();
        initCategories(view);  // view 파라미터 전달
        setupCouponList(view);  // view 파라미터 전달
    }

    private void initCategories(View view) {
        // 각 카테고리 뷰 찾기
        View cafeView = view.findViewById(R.id.category_cafe);
        View restaurantView = view.findViewById(R.id.category_restaurant);
        View storeView = view.findViewById(R.id.category_store);
        View movieView = view.findViewById(R.id.category_movie);
        View etcView = view.findViewById(R.id.category_etc);

        Log.d(TAG, "cafeView: " + (cafeView != null));
        Log.d(TAG, "restaurantView: " + (restaurantView != null));

        setupCategory(cafeView, R.drawable.ic_cafe, "카페");
        setupCategory(restaurantView, R.drawable.ic_restaurant, "식당");
        setupCategory(storeView, R.drawable.ic_convenience_store, "편의점");
        setupCategory(movieView, R.drawable.ic_movie, "영화");
        setupCategory(etcView, R.drawable.ic_etc, "기타");
    }

    private void setupCouponList(View view) {
        recyclerCoupons = view.findViewById(R.id.recycler_products);

        // GridLayoutManager 설정 - 2열로 표시
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerCoupons.setLayoutManager(layoutManager);

        adapter = new ShopCouponAdapter();
        recyclerCoupons.setAdapter(adapter);

        // 아이템 클릭 리스너
        adapter.setOnCouponClickListener((coupon, position) -> {
            Toast.makeText(requireContext(),
                    coupon.getProductName() + " 교환하기",
                    Toast.LENGTH_SHORT).show();
        });

        loadCoupons("전체");
    }

    private void loadCoupons(String category) {
        List<Coupon> coupons = new ArrayList<>();

        // 더미 데이터 추가 (브랜드명과 상품명 분리)
        coupons.add(new Coupon("CU", "ABC초코쿠키쿠앤크", 1500, "카페", "2025-12-31", R.drawable.img_cookie));
        coupons.add(new Coupon("스타벅스", "아메리카노", 5000, "카페", "2025-12-31", R.drawable.img_americano));
        coupons.add(new Coupon("투썸어플레이스", "아이스박스", 7500, "카페", "2025-12-31", R.drawable.img_icebox));
        coupons.add(new Coupon("CGV", "영화관람권", 9000, "영화", "2025-12-31", R.drawable.img_cgv));

        adapter.setItems(coupons);
        adapter.notifyDataSetChanged(); // 데이터 변경 알림
    }

    private void setupCategory(View categoryView, int iconResId, String categoryName) {
        if (categoryView != null) {
            try {
                ImageView iconView = categoryView.findViewById(R.id.image_category);
                TextView textView = categoryView.findViewById(R.id.text_category);

                if (iconView != null && textView != null) {
                    iconView.setImageResource(iconResId);
                    textView.setText(categoryName);

                    categoryView.setOnClickListener(v -> {
                        Toast.makeText(requireContext(), categoryName + " 선택됨", Toast.LENGTH_SHORT).show();
                        loadCoupons(categoryName);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, categoryName + " 설정 중 오류 발생: " + e.getMessage());
            }
        }
    }
}