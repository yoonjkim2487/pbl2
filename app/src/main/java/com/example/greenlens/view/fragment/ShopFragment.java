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
import java.util.stream.Collectors;

public class ShopFragment extends Fragment {
    private static final String TAG = "ShopFragment";
    private RecyclerView recyclerCoupons;
    private ShopCouponAdapter adapter;
    private CouponRepository repository;
    private String currentCategory = "전체";  // 현재 선택된 카테고리

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new CouponRepository();
        setupCouponList(view);
        initCategories(view);
    }

    private void initCategories(View view) {
        // 각 카테고리 View 찾기
        View categoryCafe = view.findViewById(R.id.category_cafe);
        View categoryRestaurant = view.findViewById(R.id.category_restaurant);
        View categoryStore = view.findViewById(R.id.category_store);
        View categoryMovie = view.findViewById(R.id.category_movie);
        View categoryEtc = view.findViewById(R.id.category_etc);

        // 각 카테고리 초기화
        setupCategory(categoryCafe, R.drawable.ic_cafe, "카페");
        setupCategory(categoryRestaurant, R.drawable.ic_restaurant, "식당");
        setupCategory(categoryStore, R.drawable.ic_convenience_store, "편의점");
        setupCategory(categoryMovie, R.drawable.ic_movie, "영화");
        setupCategory(categoryEtc, R.drawable.ic_etc, "기타");
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
            // TODO: 상품 상세 페이지로 이동
            Toast.makeText(requireContext(),
                    coupon.getProductName() + " 상세 페이지로 이동합니다.",
                    Toast.LENGTH_SHORT).show();
        });

        loadCoupons("전체");  // 초기 로딩
    }

    private void loadCoupons(String category) {
        currentCategory = category;  // 현재 카테고리 업데이트
        List<Coupon> allCoupons = new ArrayList<>();

        // 더미 데이터 추가
        allCoupons.add(new Coupon("CU", "ABC초코쿠키쿠앤크", 1500, "편의점", "2025-12-31", R.drawable.img_cookie));
        allCoupons.add(new Coupon("스타벅스", "아메리카노", 5000, "카페", "2025-12-31", R.drawable.img_americano));
        allCoupons.add(new Coupon("투썸플레이스", "아이스박스", 7500, "카페", "2025-12-31", R.drawable.img_icebox));
        allCoupons.add(new Coupon("CGV", "영화관람권", 9000, "영화", "2025-12-31", R.drawable.img_cgv));

        // 카테고리가 "전체"이면 모든 상품을 보여주고, 아니면 해당 카테고리 상품만 필터링
        List<Coupon> filteredCoupons;
        if (category.equals("전체")) {
            filteredCoupons = allCoupons;
        } else {
            filteredCoupons = allCoupons.stream()
                    .filter(coupon -> coupon.getCategory().equals(category))
                    .collect(Collectors.toList());
        }

        adapter.setItems(filteredCoupons);
        adapter.notifyDataSetChanged();

        // 카테고리 제목 업데이트
        TextView titleText = requireView().findViewById(R.id.text_all_title);
        titleText.setText(category);
    }

    private void setupCategory(View categoryView, int iconResId, String categoryName) {
        if (categoryView != null) {
            try {
                ImageView iconView = categoryView.findViewById(R.id.image_category);
                TextView textView = categoryView.findViewById(R.id.text_category);
                androidx.cardview.widget.CardView cardView = categoryView.findViewById(R.id.card_category);

                if (iconView != null && textView != null) {
                    iconView.setImageResource(iconResId);
                    textView.setText(categoryName);

                    // 카테고리 클릭 이벤트
                    cardView.setOnClickListener(v -> {
                        // 이전에 선택된 카테고리의 배경색을 흰색으로 변경
                        resetCategoryBackgrounds();

                        // 선택된 카테고리의 배경색을 main_green으로 변경
                        cardView.setCardBackgroundColor(requireContext().getColor(R.color.main_green));
                        textView.setTextColor(requireContext().getColor(R.color.white));

                        // 상품 목록 업데이트
                        loadCoupons(categoryName);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, categoryName + " 설정 중 오류 발생: " + e.getMessage());
            }
        }
    }

    private void resetCategoryBackgrounds() {
        View[] categoryViews = {
                requireView().findViewById(R.id.category_cafe),
                requireView().findViewById(R.id.category_restaurant),
                requireView().findViewById(R.id.category_store),
                requireView().findViewById(R.id.category_movie),
                requireView().findViewById(R.id.category_etc)
        };

        for (View categoryView : categoryViews) {
            if (categoryView != null) {
                androidx.cardview.widget.CardView cardView = categoryView.findViewById(R.id.card_category);
                TextView textView = categoryView.findViewById(R.id.text_category);
                if (cardView != null && textView != null) {
                    cardView.setCardBackgroundColor(requireContext().getColor(R.color.white));
                    textView.setTextColor(requireContext().getColor(R.color.black));
                }
            }
        }
    }
}