package com.ulearnit.ulearnit;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class HomeDeckAdapter extends RecyclerView.Adapter<HomeDeckAdapter.ViewHolder> {

    private ArrayList<DeckModel> deckList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DeckModel deck);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public HomeDeckAdapter(ArrayList<DeckModel> deckList) {
        this.deckList = deckList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_deck, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeckModel deck = deckList.get(position);
        holder.tvTitle.setText(deck.getTitle());
        holder.tvCardCount.setText(String.format(Locale.getDefault(), "%d Cards", deck.getCardCount()));
        
        holder.ivIcon.setImageResource(deck.getIconResId());
        holder.iconBg.setBackgroundTintList(ColorStateList.valueOf(deck.getIconBgColor()));
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCardCount;
        ImageView ivIcon;
        FrameLayout iconBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDeckTitle);
            tvCardCount = itemView.findViewById(R.id.tvCardCount);
            ivIcon = itemView.findViewById(R.id.deckIcon);
            iconBg = itemView.findViewById(R.id.deckIconBg);
        }
    }
}
