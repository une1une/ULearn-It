package com.ulearnit.ulearnit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private EditText etSearchDecks;

    private static final String PREFS_NAME = "ULearnItPrefs";
    private static final String DECKS_KEY = "UserDecks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);

        // 1. Initialize Views
        rvDecks = findViewById(R.id.recyclerViewDecks);
        btnCreateDeck = findViewById(R.id.btnCreateDeck);
        etSearchDecks = findViewById(R.id.etSearchDecks);

        // 2. Initialize Empty List
        deckList = new ArrayList<>();
        adapter = new DeckAdapter(deckList);
        rvDecks.setLayoutManager(new LinearLayoutManager(this));
        rvDecks.setAdapter(adapter);

        // 3. Listeners
        btnCreateDeck.setOnClickListener(v -> showCreateDeckDialog());

        etSearchDecks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        setupNavbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionManager.startSession();
        
        // 4. Load real data and refresh UI
        loadDecksFromStorage();
        
        adapter.setOnItemClickListener(deck -> {
            Intent intent = new Intent(DecksActivity.this, ReviewerDetailActivity.class);
            intent.putExtra("DECK_TITLE", deck.getTitle());
            startActivity(intent);
        });

        // 5. Shortcut logic
        if (getIntent().getBooleanExtra("OPEN_CREATE_DIALOG", false)) {
            showCreateDeckDialog();
            getIntent().removeExtra("OPEN_CREATE_DIALOG");
        }

        // Re-apply filter if text exists
        String query = etSearchDecks.getText().toString();
        if (!query.isEmpty()) {
            filter(query);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SessionManager.endSession(this);
    }

    private void loadDecksFromStorage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(DECKS_KEY, null);
        Gson gson = new Gson();
        
        ArrayList<DeckModel> loadedList;
        if (json == null || json.isEmpty()) {
            loadedList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<DeckModel>>() {}.getType();
            loadedList = gson.fromJson(json, type);
            
            for (DeckModel deck : loadedList) {
                // Update dynamic counts
                String flashcardsKey = "_flashcards_" + deck.getTitle();
                String flashcardsJson = prefs.getString(flashcardsKey, null);
                if (flashcardsJson != null) {
                    Type fType = new TypeToken<ArrayList<FlashcardModel>>() {}.getType();
                    ArrayList<FlashcardModel> flashcards = gson.fromJson(flashcardsJson, fType);
                    deck.setCardCount(flashcards != null ? flashcards.size() : 0);
                } else {
                    deck.setCardCount(0);
                }

                String masteryKey = deck.getTitle() + "_mastery";
                deck.setProgress(prefs.getInt(masteryKey, 0));
            }
        }
        
        deckList.clear();
        deckList.addAll(loadedList);
        adapter.notifyDataSetChanged();
    }

    private void saveDecksToStorage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(deckList);
        editor.putString(DECKS_KEY, json);
        editor.apply();
    }

    private void filter(String text) {
        ArrayList<DeckModel> filteredList = new ArrayList<>();
        for (DeckModel deck : deckList) {
            if (deck.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(deck);
            }
        }
        adapter.filterList(filteredList);
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
        // Load latest list first to prevent overwriting
        loadDecksFromStorage();
        
        DeckModel newDeck = new DeckModel(name, 0, 0, android.R.drawable.ic_menu_help, Color.parseColor("#E3F2FD"), Color.parseColor("#3371FF"));
        deckList.add(newDeck);
        saveDecksToStorage();
        
        adapter.notifyDataSetChanged();
        rvDecks.smoothScrollToPosition(deckList.size() - 1);
        filter(etSearchDecks.getText().toString());
    }

    private void setupNavbar() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        LinearLayout navWhatshot = findViewById(R.id.navWhatshot);
        View btnNavAdd = findViewById(R.id.btnNavAdd);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overrideTransition();
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overrideTransition();
        });

        navWhatshot.setOnClickListener(v -> {
            startActivity(new Intent(this, ProgressActivity.class));
            overrideTransition();
        });

        btnNavAdd.setOnClickListener(v -> showCreateDeckDialog());
    }

    private void overrideTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0);
        } else {
            overridePendingTransition(0, 0);
        }
    }
}
