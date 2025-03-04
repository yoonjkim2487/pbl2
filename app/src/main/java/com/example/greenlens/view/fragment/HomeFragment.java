package com.example.greenlens.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.example.greenlens.view.MainActivity;
import com.example.greenlens.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

//        // 종이류 카드 클릭
//        binding.cardPaper.setOnClickListener(v -> {
//            ((MainActivity) requireActivity()).showRecycleGuide("paper");
//        });
//
//        // 플라스틱 카드 클릭
//        binding.cardPlastic.setOnClickListener(v -> {
//            ((MainActivity) requireActivity()).showRecycleGuide("plastic");
//        });
//
//        // 유리류 카드 클릭
//        binding.cardGlass.setOnClickListener(v -> {
//            ((MainActivity) requireActivity()).showRecycleGuide("glass");
//        });
//
//        // 캔류 카드 클릭
//        binding.cardCan.setOnClickListener(v -> {
//            ((MainActivity) requireActivity()).showRecycleGuide("metal");
//        });
//
//        // 비닐류 카드 클릭
//        binding.cardVinyl.setOnClickListener(v -> {
//            ((MainActivity) requireActivity()).showRecycleGuide("vinyl");
//        });
//
//        // 스티로폼 카드 클릭
//        binding.cardStyrofoam.setOnClickListener(v -> {
//            ((MainActivity) requireActivity()).showRecycleGuide("styrofoam");
//        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}