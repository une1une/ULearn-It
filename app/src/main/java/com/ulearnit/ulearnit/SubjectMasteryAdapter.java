package com.ulearnit.ulearnit;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class SubjectMasteryAdapter extends RecyclerView.Adapter<SubjectMasteryAdapter.ViewHolder> {

    private ArrayList<DeckModel> deckList;

    public SubjectMasteryAdapter(ArrayList<DeckModel> deckList) {
        this.deckList = deckList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_mastery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeckModel deck = deckList.get(position);
        holder.tvSubjectTitle.setText(deck.getTitle());
        holder.tvMasteryPercent.setText(String.format(Locale.getDefault(), "%d%%", deck.getProgress()));
        holder.pbMastery.setProgress(deck.getProgress());
        
        holder.ivSubjectIcon.setImageResource(deck.getIconResId());
        holder.iconContainer.setBackgroundTintList(ColorStateList.valueOf(deck.getIconBgColor()));
        holder.ivSubjectIcon.setImageTintList(ColorStateList.valueOf(deck.getIconTintColor()));
        
        // Match percentage color to progress bar color (e.g. blue or green based on model if needed)
        holder.tvMasteryPercent.setTextColor(deck.getIconTintColor());
        
        // Update progress bar drawable color if needed
        holder.pbMastery.setProgressTintList(ColorStateList.valueOf(deck.getIconTintColor()));
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectTitle, tvMasteryPercent;
        ProgressBar pbMastery;
        ImageView ivSubjectIcon;
        FrameLayout iconContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectTitle = itemView.findViewById(R.id.tvSubjectTitle);
            tvMasteryPercent = itemView.findViewById(R.id.tvMasteryPercent);
            pbMastery = itemView.findViewById(R.id.pbMastery);
            ivSubjectIcon = itemView.findViewById(R.id.ivSubjectIcon);
            iconContainer = itemView.findViewById(R.id.iconContainer);
        }
    }
}
