package com.example.mandairnlearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameEndAdapter extends RecyclerView.Adapter<GameEndAdapter.ViewHolder> {
    Context context;
    private ArrayList<String> list;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_num, txt_score;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_num = itemView.findViewById(R.id.txt_Game_Rank);
            txt_score = itemView.findViewById(R.id.txt_Game_TopScore);
        }
    }

    public GameEndAdapter (ArrayList<String> list, Context context){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_game_score, parent, false);
        GameEndAdapter.ViewHolder viewHolder = new GameEndAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String current_item = list.get(position);
        holder.txt_score.setText(current_item);
        int temp_num = position + 1;
        holder.txt_num.setText(temp_num + ".");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
