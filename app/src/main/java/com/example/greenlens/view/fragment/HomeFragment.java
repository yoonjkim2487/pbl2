package com.example.greenlens.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.greenlens.R;
import com.example.greenlens.databinding.FragmentHomeBinding;
import com.example.greenlens.view.GlassRecycleActivity;
import com.example.greenlens.view.PaperRecycleActivity;
import com.example.greenlens.view.PlasticRecycleActivity;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // 종이류 카드 클릭
        binding.cardPaper.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PaperRecycleActivity.class));
        });

        // 플라스틱 카드 클릭
        binding.cardPlastic.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PlasticRecycleActivity.class));
        });

        // 유리류 카드 클릭
        binding.cardGlass.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), GlassRecycleActivity.class));
        });

        // ... 다른 카드들도 같은 방식으로 추가

        return binding.getRoot();
    }
}