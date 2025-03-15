package com.example.greenlens.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.greenlens.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // NavController 가져오기
        NavController navController = Navigation.findNavController(view);

        // 클릭 리스너 설정
        binding.cardPaper.setOnClickListener(v -> navigateToRecycleGuide(navController, "paper"));
        binding.cardPlastic.setOnClickListener(v -> navigateToRecycleGuide(navController, "plastic"));
        binding.cardGlass.setOnClickListener(v -> navigateToRecycleGuide(navController, "glass"));
        binding.cardCan.setOnClickListener(v -> navigateToRecycleGuide(navController, "metal"));
        binding.cardVinyl.setOnClickListener(v -> navigateToRecycleGuide(navController, "vinyl"));
        binding.cardStyrofoam.setOnClickListener(v -> navigateToRecycleGuide(navController, "styrofoam"));
    }

    private void navigateToRecycleGuide(NavController navController, String type) {
        HomeFragmentDirections.ActionHomeFragmentToRecycleGuideFragment action =
                HomeFragmentDirections.actionHomeFragmentToRecycleGuideFragment(type);
        navController.navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
