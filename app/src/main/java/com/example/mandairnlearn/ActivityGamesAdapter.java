package com.example.mandairnlearn;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivityGamesAdapter extends RecyclerView.Adapter<ActivityGamesAdapter.ViewHolder> {
    Context context;
    private ArrayList<String> gamesArrayList;
    private ActivityGamesAdapter.OnItemClickListener listener;

    public interface OnItemClickListener { void onItemClick (int position); }

    public void setListener(ActivityGamesAdapter.OnItemClickListener listener) { this.listener = listener; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_name;
        public ImageView img_icon;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_Game_Title);
            img_icon = itemView.findViewById(R.id.img_Game_Icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public ActivityGamesAdapter (ArrayList<String> gamesArrayList, Context context) {
        this.gamesArrayList = gamesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_games, parent, false);
        ActivityGamesAdapter.ViewHolder viewHolder = new ActivityGamesAdapter.ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentitem = gamesArrayList.get(position);
        String temp = currentitem.toLowerCase();
        temp = temp.replace(" ", "");
        String img_name = "ic_game_" + temp;
        int img_resid;
        holder.txt_name.setText(currentitem);
        try {
            img_resid = context.getResources().getIdentifier(
                    img_name, "drawable", context.getPackageName());
            if (img_resid == 0) {
                holder.img_icon.setImageResource(R.drawable.ic_game_o);
                temp = "o";
            }
            else {
                holder.img_icon.setImageResource(img_resid);}

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return gamesArrayList.size();
    }
}
