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
        ImageView ivIcon;
        FrameLayout iconContainer;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCardCount = itemView.findViewById(R.id.tvCardCount);
            tvProgressPercent = itemView.findViewById(R.id.tvProgressPercent);
            pbProgress = itemView.findViewById(R.id.pbProgress);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            iconContainer = itemView.findViewById(R.id.iconContainer);
        }
    }
}
