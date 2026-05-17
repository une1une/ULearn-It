package com.ulearnit.ulearnit;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

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

                // Remove the swipe animation immediately after starting the activity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0);
                } else {
                    overridePendingTransition(0, 0);
                }
            }
        });

        navStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeDashboardActivity.this, DecksActivity.class);
                startActivity(intent);

                // Remove the swipe animation immediately after starting the activity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0);
                } else {
                    overridePendingTransition(0, 0);
                }
            }
        });
    }
}