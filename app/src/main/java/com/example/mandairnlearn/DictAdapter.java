package com.example.mandairnlearn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DictAdapter extends RecyclerView.Adapter<DictAdapter.ViewHolder> {
    private ArrayList<DictItem> dictItems;
    private OnItemClickListener listener;
    private Boolean is_ec;

    public interface OnItemClickListener {
        void onItemClick (int position);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_word, txt_pronounce;
        public LinearLayout layout_dict;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            txt_word = itemView.findViewById(R.id.txt_dict_word);
            txt_pronounce = itemView.findViewById(R.id.txt_dict_pronounce);
            layout_dict = itemView.findViewById(R.id.layout_dict_ce);

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

    public DictAdapter (ArrayList<DictItem> dictItems, Boolean is_ec) {
        this.dictItems = dictItems;
        this.is_ec = is_ec;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dict_ce, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DictItem dictItem_current = dictItems.get(position);

        holder.txt_word.setText(dictItem_current.getWord());
        String text = dictItem_current.getDefinition();
        text = text.replaceAll("\\x2F", " • ");
        holder.txt_pronounce.setText(text);

        //TODO: Increase the height of each of the cards to fit more information for definitions
        ViewGroup.LayoutParams params = holder.layout_dict.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = 240; //TODO: It is not in dp?
        holder.layout_dict.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return dictItems.size();
    }
}
