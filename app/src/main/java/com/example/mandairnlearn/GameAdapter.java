package com.example.mandairnlearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    Context context;
    private ArrayList<Integer> scoreList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_Game_Rank, txt_Game_TopScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_Game_Rank = itemView.findViewById(R.id.txt_Game_Rank);
            txt_Game_TopScore = itemView.findViewById(R.id.txt_Game_TopScore);
        }
    }

    public GameAdapter (Context context, ArrayList<Integer> scoreList){
        this.context = context;
        this.scoreList = scoreList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_game_score, parent, false);
        GameAdapter.ViewHolder viewHolder = new GameAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Integer currentItem = scoreList.get(position);
        holder.txt_Game_TopScore.setText(String.valueOf(currentItem));
        holder.txt_Game_Rank.setText((position + 1) + ". ");
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }
}
