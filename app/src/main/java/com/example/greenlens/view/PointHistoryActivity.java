package com.example.greenlens.view;

import com.example.greenlens.R;
import com.example.greenlens.model.Point;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenlens.view.adapter.PointHistoryAdapter;
import com.example.greenlens.viewmodel.PointHistoryViewModel;

import java.util.List;

public class PointHistoryActivity extends AppCompatActivity {

    private PointHistoryViewModel pointViewModel;
    private PointHistoryAdapter pointAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_history);  // 여기를 activity_point_history로 변경하세요

        pointViewModel = new ViewModelProvider(this).get(PointHistoryViewModel.class);
        pointAdapter = new PointHistoryAdapter();

        RecyclerView recyclerView = findViewById(R.id.recycler_point_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(pointAdapter);

        pointViewModel.getPoints().observe(this, new Observer<List<Point>>() {
            @Override
            public void onChanged(List<Point> points) {
                pointAdapter.setPoints(points);
            }
        });
    }
}
