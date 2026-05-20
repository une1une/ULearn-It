package com.ulearnit.ulearnit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HomeDashboardActivity extends AppCompatActivity {

    private TextView tvCourseTitle, tvProgressPercent, tvSeeAllDecks;
    private ProgressBar courseProgress;
    private RecyclerView rvDecksHome;
    private HomeDeckAdapter adapter;
    private ArrayList<DeckModel> deckList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dashboard);

        tvCourseTitle = findViewById(R.id.tvCourseTitle);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        courseProgress = findViewById(R.id.courseProgress);
        tvSeeAllDecks = findViewById(R.id.tvSeeAllDecks);
        rvDecksHome = findViewById(R.id.rvDecksHome);

        tvSeeAllDecks.setOnClickListener(v -> {
            startActivity(new Intent(this, DecksActivity.class));
            overrideTransition();
        });

        setupNavbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionManager.startSession();
        updateRecentDeck();
        loadHomeDecks();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SessionManager.endSession(this);
    }

    private void updateRecentDeck() {
        SharedPreferences prefs = getSharedPreferences("ULearnItPrefs", MODE_PRIVATE);
        String recentTitle = prefs.getString("recent_deck_title", null);
        int recentPercent = prefs.getInt("recent_deck_percent", 0);

        if (recentTitle != null) {
            tvCourseTitle.setText(recentTitle);
            tvProgressPercent.setText(String.format(java.util.Locale.getDefault(), "%d%%", recentPercent));
            courseProgress.setProgress(recentPercent);
            findViewById(R.id.tvModuleInfo).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tvModuleInfo)).setText("Recent Study Session");
        } else {
            tvCourseTitle.setText("No quizzes taken yet");
            tvProgressPercent.setText("0%");
            courseProgress.setProgress(0);
            findViewById(R.id.tvModuleInfo).setVisibility(View.GONE);
        }
    }

    private void loadHomeDecks() {
        SharedPreferences prefs = getSharedPreferences("ULearnItPrefs", MODE_PRIVATE);
        String json = prefs.getString("UserDecks", null);
        Gson gson = new Gson();
        
        ArrayList<DeckModel> fullList;
        if (json == null) {
            fullList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<DeckModel>>() {}.getType();
            fullList = gson.fromJson(json, type);
            
            // Calculate dynamic card count for each deck
            for (DeckModel deck : fullList) {
                String flashcardsKey = "_flashcards_" + deck.getTitle();
                String flashcardsJson = prefs.getString(flashcardsKey, null);
                if (flashcardsJson != null) {
                    Type fType = new TypeToken<ArrayList<FlashcardModel>>() {}.getType();
                    ArrayList<FlashcardModel> flashcards = gson.fromJson(flashcardsJson, fType);
                    deck.setCardCount(flashcards != null ? flashcards.size() : 0);
                } else {
                    deck.setCardCount(0);
                }
            }
        }

        // Limit to first 4 decks
        deckList = new ArrayList<>();
        int limit = Math.min(fullList.size(), 4);
        for (int i = 0; i < limit; i++) {
            deckList.add(fullList.get(i));
        }

        adapter = new HomeDeckAdapter(deckList);
        adapter.setOnItemClickListener(deck -> {
            Intent intent = new Intent(HomeDashboardActivity.this, ReviewerDetailActivity.class);
            intent.putExtra("DECK_TITLE", deck.getTitle());
            startActivity(intent);
        });
        rvDecksHome.setLayoutManager(new GridLayoutManager(this, 2));
        rvDecksHome.setAdapter(adapter);
    }

    private void setupNavbar() {
        LinearLayout navProfile = findViewById(R.id.navProfile);
        LinearLayout navStyle = findViewById(R.id.navStyle);
        LinearLayout navWhatshot = findViewById(R.id.navWhatshot);
        View btnNavAdd = findViewById(R.id.btnNavAdd);

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overrideTransition();
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
        } else {
            overridePendingTransition(0, 0);
        }
    }
}
