package com.example.mandairnlearn;

public class ActivityPerformItem {
    private String category;
    private int score;

    public ActivityPerformItem (String category, Integer score) {
        this.category = category;
        this.score = score;
    }

    public String getCategory() {
        return category;
    }

    public Integer getScore() {
        return score;
    }

}
