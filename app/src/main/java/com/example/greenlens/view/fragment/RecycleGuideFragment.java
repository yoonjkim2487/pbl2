package com.example.greenlens.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.greenlens.R;
import com.example.greenlens.databinding.FragmentRecycleGuideBinding;

public class RecycleGuideFragment extends Fragment {
    private FragmentRecycleGuideBinding binding;
    private static final String ARG_TYPE = "type";

    public static RecycleGuideFragment newInstance(String type) {
        RecycleGuideFragment fragment = new RecycleGuideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecycleGuideBinding.inflate(inflater, container, false);

        String type = getArguments().getString(ARG_TYPE);
        setGuideContent(type);

        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return binding.getRoot();
    }

    private void setGuideContent(String type) {
        switch(type) {
            case "glass":
                binding.textTitle.setText("유리병류");
                View glassContent = LayoutInflater.from(requireContext())
                        .inflate(R.layout.fragment_glass_recycle, binding.layoutContent, false);
                binding.layoutContent.removeAllViews();
                binding.layoutContent.addView(glassContent);
                break;
            case "paper":
                binding.textTitle.setText("종이류");
                View paperContent = LayoutInflater.from(requireContext())
                        .inflate(R.layout.fragment_paper_recycle, binding.layoutContent, false);
                binding.layoutContent.removeAllViews();
                binding.layoutContent.addView(paperContent);
                break;
            case "plastic":
                binding.textTitle.setText("플라스틱 용기류");
                View plasticContent = LayoutInflater.from(requireContext())
                        .inflate(R.layout.fragment_plastic_recycle, binding.layoutContent, false);
                binding.layoutContent.removeAllViews();
                binding.layoutContent.addView(plasticContent);
                break;
            case "vinyl":
                binding.textTitle.setText("비닐류");
                View vinylContent = LayoutInflater.from(requireContext())
                        .inflate(R.layout.fragment_vinyl_recycle, binding.layoutContent, false);
                binding.layoutContent.removeAllViews();
                binding.layoutContent.addView(vinylContent);
                break;

            case "metal":
                binding.textTitle.setText("금속캔");
                View metalContent = LayoutInflater.from(requireContext())
                        .inflate(R.layout.fragment_metal_recycle, binding.layoutContent, false);
                binding.layoutContent.removeAllViews();
                binding.layoutContent.addView(metalContent);
                break;

            case "styrofoam":
                binding.textTitle.setText("스티로폼");
                View styrofoamContent = LayoutInflater.from(requireContext())
                        .inflate(R.layout.fragment_styrofoam_recycle, binding.layoutContent, false);
                binding.layoutContent.removeAllViews();
                binding.layoutContent.addView(styrofoamContent);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}