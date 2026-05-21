package com.ulearnit.ulearnit;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private TextView tvProgress, tvQuestionText;
    private RadioGroup rgChoices;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnNext;
    
    private ArrayList<FlashcardModel> flashcardList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String correctAnswer;
    private boolean isFirstTry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvProgress = findViewById(R.id.tvProgress);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        rgChoices = findViewById(R.id.rgChoices);
        rbA = findViewById(R.id.rbOptionA);
        rbB = findViewById(R.id.rbOptionB);
        rbC = findViewById(R.id.rbOptionC);
        rbD = findViewById(R.id.rbOptionD);
        btnNext = findViewById(R.id.btnNext);
        ImageView btnBack = findViewById(R.id.btnBack);

        String jsonList = getIntent().getStringExtra("FLASHCARD_LIST_JSON");
        String deckTitle = getIntent().getStringExtra("DECK_TITLE");
        Gson gson = new Gson();

        if (jsonList != null) {
            Type type = new TypeToken<ArrayList<FlashcardModel>>() {}.getType();
            flashcardList = gson.fromJson(jsonList, type);
        } else if (deckTitle != null) {
            SharedPreferences prefs = getSharedPreferences("ULearnItPrefs", MODE_PRIVATE);
            String flashcardsKey = "_flashcards_" + deckTitle;
            String flashcardsJson = prefs.getString(flashcardsKey, null);
            if (flashcardsJson != null) {
                Type type = new TypeToken<ArrayList<FlashcardModel>>() {}.getType();
                flashcardList = gson.fromJson(flashcardsJson, type);
            }
        }

        if (flashcardList == null || flashcardList.isEmpty()) {
            Toast.makeText(this, "No flashcards found for this deck.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Collections.shuffle(flashcardList);
        btnBack.setOnClickListener(v -> finish());

        // Set click listeners for immediate feedback
        rbA.setOnClickListener(v -> handleChoiceClick(rbA));
        rbB.setOnClickListener(v -> handleChoiceClick(rbB));
        rbC.setOnClickListener(v -> handleChoiceClick(rbC));
        rbD.setOnClickListener(v -> handleChoiceClick(rbD));

        btnNext.setOnClickListener(v -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < flashcardList.size()) {
                showQuestion();
            } else {
                showFinalScore();
            }
        });

        showQuestion();
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

    private void showQuestion() {
        isFirstTry = true;
        FlashcardModel currentCard = flashcardList.get(currentQuestionIndex);
        
        tvProgress.setText(String.format(Locale.getDefault(), "Question %d of %d", currentQuestionIndex + 1, flashcardList.size()));
        tvQuestionText.setText(currentCard.getQuestion());
        correctAnswer = currentCard.getAnswer();

        List<String> choices = new ArrayList<>();
        choices.add(correctAnswer);

        // Logic for generating wrong options
        List<FlashcardModel> others = new ArrayList<>(flashcardList);
        others.remove(currentCard);
        Collections.shuffle(others);
        
        // Add up to 3 wrong answers from other cards
        for (int i = 0; i < Math.min(3, others.size()); i++) {
            choices.add(others.get(i).getAnswer());
        }

        // If not enough cards for 4 choices, add generic ones or handle gracefully
        while (choices.size() < 4) {
            choices.add("Option " + (choices.size() + 1));
        }

        Collections.shuffle(choices);

        // Reset RadioButtons
        resetRadioButton(rbA, choices.get(0));
        resetRadioButton(rbB, choices.get(1));
        resetRadioButton(rbC, choices.get(2));
        resetRadioButton(rbD, choices.get(3));

        rgChoices.clearCheck();
        btnNext.setEnabled(false);
        
        if (currentQuestionIndex == flashcardList.size() - 1) {
            btnNext.setText("Finish Quiz");
        } else {
            btnNext.setText("Next Question");
        }
    }

    private void resetRadioButton(RadioButton rb, String text) {
        rb.setText(text);
        rb.setEnabled(true);
        rb.setBackgroundTintList(null); // Reset to default background color
        rb.setTextColor(Color.BLACK);
    }

    private void handleChoiceClick(RadioButton selected) {
        String selectedText = selected.getText().toString();
        
        if (selectedText.equals(correctAnswer)) {
            // Correct Answer
            selected.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C8E6C9"))); // Soft Green
            selected.setTextColor(Color.parseColor("#2E7D32")); // Dark Green Text
            
            if (isFirstTry) {
                score++;
            }
            
            // Disable all choices and enable Next
            rbA.setEnabled(false);
            rbB.setEnabled(false);
            rbC.setEnabled(false);
            rbD.setEnabled(false);
            btnNext.setEnabled(true);
            
        } else {
            // Wrong Answer
            selected.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFCDD2"))); // Soft Red
            selected.setTextColor(Color.parseColor("#C62828")); // Dark Red Text
            selected.setEnabled(false); // Can't click the same wrong one again
            isFirstTry = false;
        }
    }

    private void showFinalScore() {
        int percentage = (int) (((double) score / flashcardList.size()) * 100);
        saveMastery(percentage);

        new AlertDialog.Builder(this)
                .setTitle("Quiz Completed!")
                .setMessage(String.format(Locale.getDefault(), "You scored %d out of %d (%d%%)", score, flashcardList.size(), percentage))
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void saveMastery(int percentage) {
        String deckTitle = getIntent().getStringExtra("DECK_TITLE");
        if (deckTitle != null) {
            android.content.SharedPreferences prefs = getSharedPreferences("ULearnItPrefs", MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            
            // Save mastery for the specific deck
            String key = deckTitle + "_mastery";
            int currentMastery = prefs.getInt(key, 0);
            if (percentage > currentMastery) {
                editor.putInt(key, percentage);
            }
            
            // Save as the most recent deck
            editor.putString("recent_deck_title", deckTitle);
            editor.putInt("recent_deck_percent", percentage);
            
            editor.apply();
        }
    }
}
