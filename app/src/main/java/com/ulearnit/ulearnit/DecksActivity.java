package com.ulearnit.ulearnit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DecksActivity extends AppCompatActivity {

    private RecyclerView rvDecks;
    private DeckAdapter adapter;
    private ArrayList<DeckModel> deckList;
    private Button btnCreateDeck;

    private static final String PREFS_NAME = "ULearnItPrefs";
    private static final String DECKS_KEY = "UserDecks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);

        rvDecks = findViewById(R.id.recyclerViewDecks);
        btnCreateDeck = findViewById(R.id.btnCreateDeck);

        loadDecks();

        adapter = new DeckAdapter(deckList);
        rvDecks.setLayoutManager(new LinearLayoutManager(this));
        rvDecks.setAdapter(adapter);

        adapter.setOnItemClickListener(deck -> {
            Intent intent = new Intent(DecksActivity.this, ReviewerDetailActivity.class);
            intent.putExtra("DECK_TITLE", deck.getTitle());
            startActivity(intent);
        });

        btnCreateDeck.setOnClickListener(v -> showCreateDeckDialog());

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
            
            // Update dynamic card count for each deck
            for (DeckModel deck : deckList) {
                String flashcardsKey = "_flashcards_" + deck.getTitle();
                String flashcardsJson = prefs.getString(flashcardsKey, null);
                if (flashcardsJson != null) {
                    Type flashcardsType = new TypeToken<ArrayList<FlashcardModel>>() {}.getType();
                    ArrayList<FlashcardModel> flashcards = gson.fromJson(flashcardsJson, flashcardsType);
                    deck.setCardCount(flashcards != null ? flashcards.size() : 0);
                } else {
                    deck.setCardCount(0);
                }

                // Fetch saved mastery percentage
                String masteryKey = deck.getTitle() + "_mastery";
                int savedMastery = prefs.getInt(masteryKey, 0);
                deck.setProgress(savedMastery);
            }
        }
    }

    private void saveDecks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(deckList);
        editor.putString(DECKS_KEY, json);
        editor.apply();
    }

    private void showCreateDeckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Deck");

        final EditText input = new EditText(this);
        input.setHint("Deck Name");
        
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(padding, padding, padding, padding);
        container.addView(input);
        
        builder.setView(container);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String deckName = input.getText().toString().trim();
            if (!deckName.isEmpty()) {
                addNewDeck(deckName);
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void addNewDeck(String name) {
        DeckModel newDeck = new DeckModel(name, 0, 0, android.R.drawable.ic_menu_help, Color.parseColor("#E3F2FD"), Color.parseColor("#3371FF"));
        deckList.add(newDeck);
        saveDecks();
        adapter.notifyItemInserted(deckList.size() - 1);
        rvDecks.smoothScrollToPosition(deckList.size() - 1);
    }

    private void setupNavbar() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(DecksActivity.this, HomeDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0);
            } else {
                overridePendingTransition(0, 0);
            }
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DecksActivity.this, ProfileActivity.class);
            startActivity(intent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0);
            } else {
                overridePendingTransition(0, 0);
            }
        });
    }
}
