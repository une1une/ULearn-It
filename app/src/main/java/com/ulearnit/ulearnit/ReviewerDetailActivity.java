package com.ulearnit.ulearnit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ReviewerDetailActivity extends AppCompatActivity {

    private String deckTitle;
    private ArrayList<FlashcardModel> flashcardList;
    private FlashcardAdapter adapter;
    private RecyclerView rvFlashcards;
    private TextView tvEmptyState;

    private static final String PREFS_NAME = "ULearnItPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewer_detail);

        deckTitle = getIntent().getStringExtra("DECK_TITLE");
        
        ImageView btnBack = findViewById(R.id.btnBack);
        TextView tvTitle = findViewById(R.id.tvDeckTitleDetail);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        rvFlashcards = findViewById(R.id.rvFlashcards);
        Button btnQuizNow = findViewById(R.id.btnQuizNow);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddFlashcard);

        if (deckTitle != null) {
            tvTitle.setText(deckTitle);
        }

        loadFlashcards();

        adapter = new FlashcardAdapter(flashcardList);
        rvFlashcards.setLayoutManager(new LinearLayoutManager(this));
        rvFlashcards.setAdapter(adapter);

        updateEmptyState();

        fabAdd.setOnClickListener(v -> showAddFlashcardDialog());

        btnQuizNow.setOnClickListener(v -> {
            if (flashcardList.size() < 4) {
                Toast.makeText(this, "Please add at least 4 flashcards to start a quiz.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("DECK_TITLE", deckTitle);
                intent.putExtra("FLASHCARD_LIST_JSON", new Gson().toJson(flashcardList));
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateEmptyState() {
        if (flashcardList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvFlashcards.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvFlashcards.setVisibility(View.VISIBLE);
        }
    }

    private void loadFlashcards() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String key = "_flashcards_" + deckTitle;
        String json = prefs.getString(key, null);
        Gson gson = new Gson();
        if (json == null) {
            flashcardList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<FlashcardModel>>() {}.getType();
            flashcardList = gson.fromJson(json, type);
        }
    }

    private void showAddFlashcardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Flashcard");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, padding);

        final EditText etQuestion = new EditText(this);
        etQuestion.setHint("Enter Question");
        container.addView(etQuestion);

        final EditText etAnswer = new EditText(this);
        etAnswer.setHint("Enter Answer");
        container.addView(etAnswer);

        builder.setView(container);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String question = etQuestion.getText().toString().trim();
            String answer = etAnswer.getText().toString().trim();

            if (!question.isEmpty() && !answer.isEmpty()) {
                saveFlashcard(question, answer);
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveFlashcard(String question, String answer) {
        flashcardList.add(new FlashcardModel(question, answer));

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(flashcardList);
        String key = "_flashcards_" + deckTitle;
        editor.putString(key, json);
        editor.apply();

        adapter.notifyItemInserted(flashcardList.size() - 1);
        updateEmptyState();
        Toast.makeText(this, "Flashcard Saved!", Toast.LENGTH_SHORT).show();
    }
}
