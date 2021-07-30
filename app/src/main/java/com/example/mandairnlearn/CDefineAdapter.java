package com.example.mandairnlearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CDefineAdapter extends ArrayAdapter {

    private ArrayList<String> characters = new ArrayList<String>();
    private ArrayList<String> pinyin     = new ArrayList<String>();

    public CDefineAdapter(@NonNull Context context, int resource, ArrayList<String> characters, ArrayList<String> pinyin) {
        super(context, resource);
        this.characters = characters;
        this.pinyin = pinyin;
    }

    @Override
    public int getCount() {
        return characters.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.cedefine_character, null);

        TextView txt_char = view.findViewById(R.id.txt_DictWord_Char);
        TextView txt_pinyin = view.findViewById(R.id.txt_DictWord_Pinyin);

        txt_char.setText(characters.get(position));
        txt_pinyin.setText(pinyin.get(position));

        return view;
    }
}
