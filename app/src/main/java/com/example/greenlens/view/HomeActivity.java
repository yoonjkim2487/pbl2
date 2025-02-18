package com.example.greenlens.view;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenlens.R;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 상단바 타이틀 설정
        TextView titleText = findViewById(R.id.text_title);
        titleText.setText("분리배출방법");
    }
}