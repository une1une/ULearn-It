package com.ulearnit.ulearnit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView tvFullName = findViewById(R.id.tvFullName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPassword = findViewById(R.id.tvPassword);

        SharedPreferences prefs = getSharedPreferences("ULearnItPrefs", MODE_PRIVATE);
        String fullName = prefs.getString("user_fullname", "Alex Rivera");
        String email = prefs.getString("user_email", "alex.rivera@example.com");
        String password = prefs.getString("user_password", "********");

        tvFullName.setText(fullName);
        tvEmail.setText(email);
        
        // Masking password
        if (password != null && !password.equals("********")) {
            StringBuilder maskedPassword = new StringBuilder();
            for (int i = 0; i < password.length(); i++) {
                maskedPassword.append("*");
            }
            tvPassword.setText(maskedPassword.toString());
        } else {
            tvPassword.setText(password);
        }
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
}
