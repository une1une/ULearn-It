package com.ulearnit.ulearnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import java.util.ArrayList;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    private ArrayList<DeckModel> deckList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DeckModel deck);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public DeckAdapter(ArrayList<DeckModel> deckList) {
        this.deckList = deckList;
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deck, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int position) {
        DeckModel deck = deckList.get(position);
        holder.tvTitle.setText(deck.getTitle());
        holder.tvCardCount.setText(String.format("%d Cards", deck.getCardCount()));
        holder.tvProgressPercent.setText(String.format("%d%%", deck.getProgress()));
        holder.pbProgress.setProgress(deck.getProgress());
        
        holder.ivIcon.setImageResource(deck.getIconResId());
        holder.iconContainer.setBackgroundTintList(ColorStateList.valueOf(deck.getIconBgColor()));
        holder.ivIcon.setImageTintList(ColorStateList.valueOf(deck.getIconTintColor()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(deck);
            }
        });

        // 3-dots options menu logic
        holder.ivOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add("Edit");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Edit")) {
                    showEditDialog(v.getContext(), deck, position);
                }
                return true;
            });
            popup.show();
        });
    }

    private void showEditDialog(Context context, DeckModel deck, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Deck Title");

        final EditText input = new EditText(context);
        input.setText(deck.getTitle());
        
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * context.getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, padding);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                String oldTitle = deck.getTitle();
                deck.setTitle(newTitle);
                
                // Update persistent storage
                SharedPreferences prefs = context.getSharedPreferences("ULearnItPrefs", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                
                // Note: deckList here might be a filtered list if search is active.
                // In a production app, we should update the master list in DecksActivity.
                String json = gson.toJson(deckList);
                prefs.edit().putString("UserDecks", json).apply();
                
                // Migration: Move flashcards to the new title key so data isn't lost
                String oldFlashKey = "_flashcards_" + oldTitle;
                String newFlashKey = "_flashcards_" + newTitle;
                if (prefs.contains(oldFlashKey)) {
                    String flashJson = prefs.getString(oldFlashKey, null);
                    prefs.edit().putString(newFlashKey, flashJson).remove(oldFlashKey).apply();
                }

                // Migration: Move mastery progress
                String oldMasteryKey = oldTitle + "_mastery";
                String newMasteryKey = newTitle + "_mastery";
                if (prefs.contains(oldMasteryKey)) {
                    int mastery = prefs.getInt(oldMasteryKey, 0);
                    prefs.edit().putInt(newMasteryKey, mastery).remove(oldMasteryKey).apply();
                }

                notifyItemChanged(position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    public void filterList(ArrayList<DeckModel> filteredList) {
        this.deckList = filteredList;
        notifyDataSetChanged();
    }

    public static class DeckViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCardCount, tvProgressPercent;
        ProgressBar pbProgress;
        ImageView ivIcon, ivOptions;
        FrameLayout iconContainer;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCardCount = itemView.findViewById(R.id.tvCardCount);
            tvProgressPercent = itemView.findViewById(R.id.tvProgressPercent);
            pbProgress = itemView.findViewById(R.id.pbProgress);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivOptions = itemView.findViewById(R.id.ivOptions);
            iconContainer = itemView.findViewById(R.id.iconContainer);
        }
    }
}
