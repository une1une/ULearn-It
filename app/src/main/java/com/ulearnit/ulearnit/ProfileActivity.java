package com.ulearnit.ulearnit;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupNavbar();
    }

    private void setupNavbar() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navStyle = findViewById(R.id.navStyle);
        LinearLayout navWhatshot = findViewById(R.id.navWhatshot);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overrideTransition();
            finish();
        });

        navStyle.setOnClickListener(v -> {
            startActivity(new Intent(this, DecksActivity.class));
            overrideTransition();
        });

        navWhatshot.setOnClickListener(v -> {
            startActivity(new Intent(this, ProgressActivity.class));
            overrideTransition();
        });
    }

    private void overrideTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0);
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0);
        } else {
            overridePendingTransition(0, 0);
        }
    }
}
