package com.example.greenlens.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenlens.R;
import com.example.greenlens.model.Coupon;
import com.bumptech.glide.Glide;

public class ShopCouponAdapter extends CouponAdapter {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop_coupon, parent, false);
        return new ShopViewHolder(view);
    }

    class ShopViewHolder extends ViewHolder {
        private final TextView textPoints;

        public ShopViewHolder(@NonNull View view) {
            super(view);
            textPoints = view.findViewById(R.id.text_points);

            // 전체 아이템 클릭 리스너 설정
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && getOnCouponClickListener() != null) {
                    getOnCouponClickListener().onUseClick(getItems().get(position), position);
                }
            });
        }

        @Override
        public void bind(Coupon item) {
            super.bind(item);  // 부모 클래스의 기본 바인딩 수행

            // 포인트 정보 설정
            textPoints.setText(item.getPoints() + "P");

            // 이미지 설정 (이미 부모 클래스에서 처리되므로 제거)

            // 쿠폰샵 스타일 적용
            itemView.setAlpha(1.0f);
        }
    }

    // CouponAdapter의 protected/private 멤버에 접근하기 위한 getter
    protected OnCouponClickListener getOnCouponClickListener() {
        return listener;
    }
}