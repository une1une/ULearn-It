package com.ulearnit.ulearnit;

public class DeckModel {
    private String title;
    private int cardCount;
    private int progress;
    private int iconResId;
    private int iconBgColor;
    private int iconTintColor;

    public DeckModel(String title, int cardCount, int progress, int iconResId, int iconBgColor, int iconTintColor) {
        this.title = title;
        this.cardCount = cardCount;
        this.progress = progress;
        this.iconResId = iconResId;
        this.iconBgColor = iconBgColor;
        this.iconTintColor = iconTintColor;
    }

    public String getTitle() { return title; }
    public int getCardCount() { return cardCount; }
    public void setCardCount(int cardCount) { this.cardCount = cardCount; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public int getIconResId() { return iconResId; }
    public int getIconBgColor() { return iconBgColor; }
    public int getIconTintColor() { return iconTintColor; }
}
