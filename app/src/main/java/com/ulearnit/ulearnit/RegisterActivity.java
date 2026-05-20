package com.ulearnit.ulearnit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etCreatePassword, etConfirmPassword;
    private CheckBox cbTerms;
    private Button btnRegister;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etCreatePassword = findViewById(R.id.etCreatePassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String pass = etCreatePassword.getText().toString().trim();
                String confirmPass = etConfirmPassword.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pass.equals(confirmPass)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!cbTerms.isChecked()) {
                    Toast.makeText(RegisterActivity.this, "Please agree to the terms", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save user data to SharedPreferences
                android.content.SharedPreferences prefs = getSharedPreferences("ULearnItPrefs", MODE_PRIVATE);
                android.content.SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_fullname", name);
                editor.putString("user_email", email);
                editor.putString("user_password", pass);
                editor.apply();

                Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to LoginActivity
                finish();
            }
        });
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
