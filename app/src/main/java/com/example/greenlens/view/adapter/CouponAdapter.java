package com.example.greenlens.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greenlens.R;
import com.example.greenlens.model.Coupon;

import java.util.ArrayList;
import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {
    private List<Coupon> items = new ArrayList<>();
    protected OnCouponClickListener listener;
    private Context context;

    // 생성자
    public CouponAdapter() {
        this.items = new ArrayList<>();
    }

    public interface OnCouponClickListener {
        void onUseClick(Coupon coupon, int position);
    }

    public void setOnCouponClickListener(OnCouponClickListener listener) {
        this.listener = listener;
    }

    public List<Coupon> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop_coupon, parent, false);  // item_shop_coupon.xml 사용
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Coupon> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardImage;
        private final ImageView imageCoupon;
        private final TextView textBrand;
        private final TextView textName;
        private final TextView textDate;
        private final TextView textPoints;  // 포인트 표시용 TextView 추가

        public ViewHolder(@NonNull View view) {
            super(view);
            cardImage = view.findViewById(R.id.card_image);
            imageCoupon = view.findViewById(R.id.image_coupon);
            textBrand = view.findViewById(R.id.text_brand);
            textName = view.findViewById(R.id.text_name);
            textDate = view.findViewById(R.id.text_date);
            textPoints = view.findViewById(R.id.text_points);  // points TextView 초기화

            // 전체 아이템 클릭 리스너 설정
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUseClick(items.get(position), position);
                }
            });
        }

        public void bind(Coupon item) {
            textBrand.setText(item.getBrand());
            textName.setText(item.getFullName());  // [브랜드명] 쿠폰명 형식으로 표시
            textDate.setText(item.getExpireDate());
            textPoints.setText(item.getPoints() + "P");  // 포인트 표시

            // 이미지 로딩 (리소스 ID 사용)
            if (item.getImageResId() != 0) {
                Glide.with(itemView.getContext())
                        .load(item.getImageResId())
                        .centerCrop()
                        .into(imageCoupon);
            }

            // 쿠폰샵에서는 모든 아이템을 동일한 투명도로 표시
            itemView.setAlpha(1.0f);
        }
    }
}