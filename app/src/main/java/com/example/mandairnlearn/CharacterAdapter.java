package com.example.mandairnlearn;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharacterAdapter extends ArrayAdapter {
    private static ArrayList<String> elements = new ArrayList<String>();
    private static int c_type;
    private Context context;

    public CharacterAdapter(@NonNull Context context, int resource, ArrayList<String> elements, int c_type) {
        super(context, resource);
        this.elements = elements;
        this.context = context;
        this.c_type = c_type;
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.character_composition, null);

        TextView txt_char = (TextView) view.findViewById(R.id.txt_Char_Element);
        ImageView img_colour = (ImageView) view.findViewById(R.id.img_Char_Color);
        TextView txt_meaning = (TextView) view.findViewById(R.id.txt_Char_Desc); // This really sucks D-8>

        //TODO: Set Component
        txt_char.setText(elements.get(position));

        //TODO: Set Colour Identification based on position
        String colorString = "#AAAAAA";
        switch (position) {
            case 0:     colorString = "#AA5555"; break;
            case 1:     colorString = "#55AA55"; break;
            case 2:     colorString = "#5555AA"; break;
            case 3:     colorString = "#AAAA55"; break;
            case 4:     colorString = "#AA55AA"; break;
            default:    colorString = "#555555"; break;
        }
        img_colour.setBackgroundColor(Color.parseColor(colorString));

        //TODO: Process Component Meaning
        FileAssets fileAssets = new FileAssets();
        String temp_desc = "";
        switch (c_type){
            case 1:
                temp_desc = fileAssets.getOutput(context,"radical_mean.txt",
                        "(?<=" + elements.get(position) + "\\x3A).*");
                temp_desc = temp_desc.replace("\n", "");
                if (temp_desc != ""){
                    Pattern pattern = Pattern.compile("(?<=" + elements.get(position) + "\\x3A).*");
                    Matcher matcher = pattern.matcher(temp_desc);
                    if (matcher.find()){
                        String desc = "The Radical component of the character, contains the meaning of \"" +
                                matcher.group(0) + "\", therefore indicating connections with the component.";
                        txt_meaning.setText(desc);
                    }
                }
                else {
                    temp_desc = fileAssets.getOutput(context,"cedict_ts.txt",
                            "\\x20" + elements.get(position) + "\\x20\\x5B.*?");
                    temp_desc = temp_desc.replace("\n", "");
                    Pattern pattern = Pattern.compile(
                            "(?<=\\x20" + elements.get(position) + "\\x20\\x5B).[^\\x20]*(?=\\x5D\\x20)"
                    );
                    Matcher matcher = pattern.matcher(temp_desc);
                    if(matcher.find()){
                        String desc = "The Phonetic component of the character, with the pronunciation \"" +
                                matcher.group(0) + "\", therefore is pronounced similar or related to the component.";
                        txt_meaning.setText(desc);
                    }
                }
                break;

            case 2:
                temp_desc = fileAssets.getOutput(context,"pictogram.txt",
                        elements.get(position) + "\\x3A.*");
                temp_desc = temp_desc.replace("\n", "");
                Pattern pattern = Pattern.compile("(?<=" + elements.get(position) + "\\x3A).*");
                Matcher matcher = pattern.matcher(temp_desc);
                if (matcher.find()){
                    txt_meaning.setText(matcher.group(0));
                }
                break;

            default:
                String desc = "This application cannot analyse this character. We are sorry for this inconvenience.";
                txt_meaning.setText(desc);
                break;
        }

        return view;
    }
}
