package com.example.mandairnlearn;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivityPerformAdapter extends RecyclerView.Adapter<ActivityPerformAdapter.ViewHolder> {
    Context context;
    private ArrayList<ActivityPerformItem> itemArrayList;

    public interface OnItemClickListener { void onItemClick (int position); }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txt_Act_Perform, txt_Act_Score, txt_Act_Level;
        public ProgressBar prog_Act_Perform;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_Act_Perform = itemView.findViewById(R.id.txt_Act_Perform);
            txt_Act_Score = itemView.findViewById(R.id.txt_Act_Score);
            txt_Act_Level = itemView.findViewById(R.id.txt_Act_Level);
            prog_Act_Perform = itemView.findViewById(R.id.prog_Act_Perform);
        }
    }

    public ActivityPerformAdapter (ArrayList<ActivityPerformItem> itemArrayList, Context context){
        this.itemArrayList = itemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_perform, parent, false);
        ActivityPerformAdapter.ViewHolder viewHolder = new ActivityPerformAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityPerformAdapter.ViewHolder holder, int position) {
        //TODO: Analyse
        ActivityPerformItem currentitem = itemArrayList.get(position);
        String category = currentitem.getCategory();
        Integer score = currentitem.getScore();

        holder.txt_Act_Perform.setText(category);
        holder.txt_Act_Score.setText("Score : " + score.toString());

        Integer nxt_level_requirement = 0;
        Integer level_progress = 0;
        Integer req = 1;

        for (Integer level = 1; level <= 30; level++){
            req = 2000 * level;
            nxt_level_requirement += req;
            if (score < nxt_level_requirement || level == 30){
                holder.txt_Act_Level.setText("Level : " + level.toString());
                level_progress = nxt_level_requirement - score;
                break;
            }
        }

        Integer percentage = ( (req - level_progress) * 100) / (nxt_level_requirement);
        holder.prog_Act_Perform.setMax(100);
        holder.prog_Act_Perform.setProgress(percentage);
        Log.i("percentage", percentage + "%, " + (req - level_progress) + " of " + req);
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }
}
