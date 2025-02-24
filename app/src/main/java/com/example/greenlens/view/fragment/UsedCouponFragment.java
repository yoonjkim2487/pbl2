package com.example.greenlens.view.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenlens.R;
import com.example.greenlens.model.Coupon;
import com.example.greenlens.view.adapter.UsedCouponAdapter;

import java.util.ArrayList;
import java.util.List;

public class UsedCouponFragment extends Fragment {
    private RecyclerView recyclerView;
    private UsedCouponAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_used_coupon, container, false);

        recyclerView = view.findViewById(R.id.recycler_used_coupons);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UsedCouponAdapter();
        recyclerView.setAdapter(adapter);

        // 테스트 데이터 로드
        loadUsedCoupons();

        return view;
    }

    private void loadUsedCoupons() {
        // TODO: 실제 데이터를 로드하는 코드로 대체
        List<Coupon> coupons = new ArrayList<>();
        // 테스트 데이터 추가
        adapter.setCoupons(coupons);
    }
}