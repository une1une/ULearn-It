package com.ulearnit.ulearnit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        android.widget.TextView tvProfileName = findViewById(R.id.tvProfileName);
        SharedPreferences prefs = getSharedPreferences("ULearnItPrefs", MODE_PRIVATE);
        String fullName = prefs.getString("user_fullname", "Alex Rivera");
        tvProfileName.setText(fullName);

        // Settings Navigation
        RelativeLayout itemSettings = findViewById(R.id.itemSettings);
        itemSettings.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
        });

        // Sign Out Logic
        android.widget.Button btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(v -> {
            // Clear logged-in session
            prefs.edit().putBoolean("is_logged_in", false).apply();

            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finishAffinity(); // Prevent back navigation
        });

        setupNavbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionManager.startSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SessionManager.endSession(this);
    }

    private void setupNavbar() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navStyle = findViewById(R.id.navStyle);
        LinearLayout navWhatshot = findViewById(R.id.navWhatshot);
        android.view.View btnNavAdd = findViewById(R.id.btnNavAdd);

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

        btnNavAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, DecksActivity.class);
            intent.putExtra("OPEN_CREATE_DIALOG", true);
            startActivity(intent);
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
