package com.example.greenlens.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenlens.R;
import com.example.greenlens.model.Coupon;
import com.bumptech.glide.Glide;

public class ShopCouponAdapter extends CouponAdapter {
    @Override
    protected int getItemLayout() {
        return R.layout.item_shop_coupon;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View view = LayoutInflater.from(parent.getContext())
                .inflate(getItemLayout(), parent, false);
        return new ShopViewHolder(view);
    }

    private class ShopViewHolder extends ViewHolder {
        private ImageView imageCoupon;
        private TextView textName;
        private TextView textPoints;

        public ShopViewHolder(@NonNull android.view.View view) {
            super(view);
        }

        @Override
        protected void setupViews(@NonNull android.view.View view) {
            imageCoupon = view.findViewById(R.id.image_coupon);
            textName = view.findViewById(R.id.text_name);
            textPoints = view.findViewById(R.id.text_points);
        }

        @Override
        protected void bind(Coupon item) {
            String displayName = String.format("[%s] %s", item.getBrandName(), item.getProductName());
            textName.setText(displayName);
            textPoints.setText(item.getPoints() + "P");

            if (item.getImageResId() != 0) {
                Glide.with(itemView.getContext())
                        .load(item.getImageResId())
                        .centerCrop()
                        .into(imageCoupon);
            }
        }
    }
}