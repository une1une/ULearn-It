package com.ulearnit.ulearnit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.LinearLayout;
import android.content.Intent;

public class HomeDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dashboard);

        LinearLayout navProfile = findViewById(R.id.navProfile);
        LinearLayout navStyle = findViewById(R.id.navStyle);

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        navStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeDashboardActivity.this, DecksActivity.class);
                startActivity(intent);
            }
        });
    }
}
