package com.example.greenlens.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.example.greenlens.util.DevLog;
import com.example.greenlens.view.ShopDetailActivity;
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
    private EditText editSearch;
    private ImageView imgSearch;
    private List<Coupon> allCoupons = new ArrayList<>();  // 모든 쿠폰 목록
    private List<Coupon> filteredCoupons = new ArrayList<>();  // 필터링된 쿠폰 목록
    private String currentSearchQuery = "";  // 현재 검색어

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new CouponRepository();
        setupSearchView(view);
        setupCouponList(view);
        initCategories(view);
        initDummyData();  // 더미 데이터 초기화
    }

    private void setupSearchView(View view) {
        editSearch = view.findViewById(R.id.edit_search);
        imgSearch = view.findViewById(R.id.img_search);

        // 검색 아이콘 클릭 시 검색 수행
        imgSearch.setOnClickListener(v -> performSearch());

        // 키보드에서 검색 버튼 클릭 시 검색 수행
        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // 텍스트 변경 시 실시간 검색 (선택적으로 활성화)
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().trim();
                filterCoupons();
            }
        });
    }

    private void performSearch() {
        currentSearchQuery = editSearch.getText().toString().trim();
        filterCoupons();
    }

    private void initDummyData() {
        // 더미 데이터 추가
        // 편의점 카테고리
        allCoupons.add(new Coupon("CU", "ABC초코쿠키쿠앤크", 1500, "편의점", "2025-12-31", R.drawable.img_cookie));
        allCoupons.add(new Coupon("GS25", "오뚜기순후추팝콘", 1700, "편의점", "2025-12-31", R.drawable.img_corn));
        allCoupons.add(new Coupon("세븐일레븐", "트러플촉촉함박도시락", 6900, "편의점", "2025-12-31", R.drawable.img_lunchbox));

        // 카페 카테고리
        allCoupons.add(new Coupon("스타벅스", "아메리카노", 5000, "카페", "2025-12-31", R.drawable.img_americano));
        allCoupons.add(new Coupon("투썸플레이스", "아이스박스", 7500, "카페", "2025-12-31", R.drawable.img_icebox));
        allCoupons.add(new Coupon("이디야", "바닐라라떼", 4500, "카페", "2025-12-31", R.drawable.img_ediya));
        allCoupons.add(new Coupon("할리스", "몬스터아메리카노", 5400, "카페", "2025-12-31", R.drawable.img_hollys));

        // 식당 카테고리
        allCoupons.add(new Coupon("맥도날드", "맥스파이시상하이버거", 5500, "식당", "2025-12-31", R.drawable.img_mac));
        allCoupons.add(new Coupon("롯데리아", "리아불고기", 5000, "식당", "2025-12-31", R.drawable.img_ria));

        // 영화 카테고리
        allCoupons.add(new Coupon("CGV", "영화관람권", 9000, "영화", "2025-12-31", R.drawable.img_cgv));
        allCoupons.add(new Coupon("롯데시네마", "영화티켓", 9200, "영화", "2025-12-31", R.drawable.img_locinema));

        // 초기 데이터 로드
        filterCoupons();
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
            // 상품 상세 페이지로 이동
            Intent intent = new Intent(requireContext(), ShopDetailActivity.class);
            // 선택한 쿠폰 정보를 Intent에 추가
            intent.putExtra("BRAND_NAME", coupon.getBrandName());
            intent.putExtra("PRODUCT_NAME", coupon.getProductName());
            intent.putExtra("POINTS", coupon.getPoints());
            intent.putExtra("CATEGORY", coupon.getCategory());
            intent.putExtra("IMAGE_RES_ID", coupon.getImageResId());
            intent.putExtra("EXPIRE_DATE", coupon.getExpireDate());
            startActivity(intent);

            // 디버깅용 토스트 메시지
            Toast.makeText(requireContext(),
                    coupon.getProductName() + " 상세 페이지로 이동합니다.",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void filterCoupons() {
        // 카테고리 및 검색어로 필터링
        filteredCoupons = allCoupons.stream()
                .filter(coupon ->
                        (currentCategory.equals("전체") || coupon.getCategory().equals(currentCategory)) &&
                                (currentSearchQuery.isEmpty() ||
                                        coupon.getProductName().toLowerCase().contains(currentSearchQuery.toLowerCase()) ||
                                        coupon.getBrandName().toLowerCase().contains(currentSearchQuery.toLowerCase()))
                )
                .collect(Collectors.toList());

        // 검색 결과가 없을 때 메시지 표시
        if (filteredCoupons.isEmpty() && !currentSearchQuery.isEmpty()) {
            Toast.makeText(requireContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // 리스트 업데이트
        adapter.setItems(filteredCoupons);
        adapter.notifyDataSetChanged();

        // 카테고리 제목 업데이트 (검색어가 있으면 검색 결과 표시)
        TextView titleText = requireView().findViewById(R.id.text_all_title);
        if (!currentSearchQuery.isEmpty()) {
            titleText.setText("'" + currentSearchQuery + "' 검색 결과");
        } else {
            titleText.setText(currentCategory);
        }
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
                        // 이미 선택된 카테고리를 다시 클릭하면 "전체"로 변경
                        if (currentCategory.equals(categoryName)) {
                            resetCategoryBackgrounds();
                            currentCategory = "전체";
                            editSearch.setText("");
                            currentSearchQuery = "";
                            filterCoupons();
                        } else {
                            setSelectedCategory(categoryView, categoryName);
                        }
                    });
                }
            } catch (Exception e) {
                DevLog.e(TAG, categoryName + " 설정 중 오류 발생: " + e.getMessage());
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

    private void setSelectedCategory(View categoryView, String categoryName) {
        if (categoryView != null) {
            androidx.cardview.widget.CardView cardView = categoryView.findViewById(R.id.card_category);
            TextView textView = categoryView.findViewById(R.id.text_category);
            if (cardView != null && textView != null) {
                // 이전 선택 해제
                resetCategoryBackgrounds();

                // 현재 카테고리 선택
                cardView.setCardBackgroundColor(requireContext().getColor(R.color.main_green));
                textView.setTextColor(requireContext().getColor(R.color.white));

                // 카테고리 변경 및 필터링
                currentCategory = categoryName;
                editSearch.setText("");  // 검색어 초기화
                currentSearchQuery = "";
                filterCoupons();
            }
        }
    }
}