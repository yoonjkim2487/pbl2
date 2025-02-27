package com.example.greenlens.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.greenlens.R;
import com.example.greenlens.model.Coupon;

public class ShopDetailActivity extends AppCompatActivity {

    private ImageView imageProduct;
    private TextView textBrand;
    private TextView textName;
    private TextView textPoint;
    private TextView textValidity;
    private TextView textGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        // View 초기화
        imageProduct = findViewById(R.id.image_product);
        textBrand = findViewById(R.id.text_brand);
        textName = findViewById(R.id.text_name);
        textPoint = findViewById(R.id.text_point);
        textValidity = findViewById(R.id.text_validity);
        textGuide = findViewById(R.id.text_guide);

        // 뒤로가기 버튼
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // 더미 데이터로 테스트
        Coupon dummyCoupon = new Coupon(
                "스타벅스",
                "아메리카노",
                4500,
                "카페",
                "2025-06-10까지",
                R.drawable.ic_cafe
        );
        dummyCoupon.setImageUrl("https://example.com/starbucks.jpg");
        dummyCoupon.setValidityDays(30);

        // 데이터 설정
        setCouponData(dummyCoupon);
    }

    private void setCouponData(Coupon coupon) {
        if (coupon != null) {
            // 이미지 설정
            if (coupon.getImageUrl() != null && !coupon.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(coupon.getImageUrl())
                        .into(imageProduct);
            } else {
                imageProduct.setImageResource(coupon.getImageResId());
            }

            // 브랜드명 설정
            textBrand.setText(coupon.getBrandName());

            // 상품명 설정
            textName.setText(String.format("[%s] %s",
                    coupon.getBrandName(),
                    coupon.getProductName()));

            // 포인트 설정
            textPoint.setText(String.format("%dP", coupon.getPoints()));

            // 유효기간 설정
            textValidity.setText(String.format("유효기간 %d일",
                    coupon.getValidityDays()));

            // 이용안내 설정
            textGuide.setText(getString(R.string.shop_detail_guide,
                    coupon.getBrandName()));
        }
    }
}