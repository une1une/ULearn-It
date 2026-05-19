package com.ulearnit.ulearnit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class ProgressActivity extends AppCompatActivity {

    private RecyclerView rvSubjectMastery;
    private SubjectMasteryAdapter adapter;
    private ArrayList<DeckModel> deckList;

    private static final String PREFS_NAME = "ULearnItPrefs";
    private static final String DECKS_KEY = "UserDecks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        rvSubjectMastery = findViewById(R.id.rvSubjectMastery);
        
        loadDecks();

        adapter = new SubjectMasteryAdapter(deckList);
        rvSubjectMastery.setLayoutManager(new LinearLayoutManager(this));
        rvSubjectMastery.setAdapter(adapter);

        setupNavbar();
    }

    private void loadDecks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(DECKS_KEY, null);
        Gson gson = new Gson();
        if (json == null) {
            deckList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<DeckModel>>() {}.getType();
            deckList = gson.fromJson(json, type);

            // Fetch saved mastery percentage for each deck
            for (DeckModel deck : deckList) {
                String masteryKey = deck.getTitle() + "_mastery";
                int savedMastery = prefs.getInt(masteryKey, 0);
                deck.setProgress(savedMastery);
            }

            // Sort decks by progress in descending order (highest mastery first)
            Collections.sort(deckList, (d1, d2) -> Integer.compare(d2.getProgress(), d1.getProgress()));
        }
    }

    private void setupNavbar() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navWhatshot = findViewById(R.id.navWhatshot);
        LinearLayout navStyle = findViewById(R.id.navStyle);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overrideTransition();
        });

        navStyle.setOnClickListener(v -> {
            Intent intent = new Intent(this, DecksActivity.class);
            startActivity(intent);
            overrideTransition();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
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
