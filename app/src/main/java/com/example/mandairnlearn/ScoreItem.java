package com.example.mandairnlearn;

public class ScoreItem {
    private String category, game, user, score;
    public ScoreItem() {}

    public void setCategory   (String category) { this.category = category; }
    public void setGame       (String game)     { this.game = game; }
    public void setUser       (String user)     { this.user =  user; }
    public void setScore      (String score)       { this.score = score; }

    public String getCategory() { return category; }
    public String getGame()     { return game; }
    public String getUser()     { return user; }
    public String getScore()       { return score; }
}
