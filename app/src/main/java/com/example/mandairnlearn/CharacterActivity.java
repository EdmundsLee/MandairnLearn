package com.example.mandairnlearn;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharacterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    private TextView txt_char_glyph, txt_char_pron, txt_char_class;
    private ImageView img_char_comp;

    public String decomp_string;
    public String character, structure;
    public int c_type;
    public ArrayList<String> components;

    /*
        TODO: "c_type" refers to the classification of the character:
        TODO: 0 = default, ones that I have no idea how to categorize at the moment
        TODO: 1 = phono-semantic characters
        TODO: 2 = pictographic characters
    */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        //TODO: Initialize Activity Toolbar
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Character");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });

        //TODO: Initialize Layout Elements
        listView = findViewById(R.id.list_CharComp);
        txt_char_glyph = findViewById(R.id.txt_Char_Glyph);
        txt_char_pron = findViewById(R.id.txt_Char_Pron);
        txt_char_class = findViewById(R.id.txt_Char_Class);
        img_char_comp = findViewById(R.id.img_Char_Comp);


        //TODO: Get Character from CDefineActivity
        if (getIntent().getExtras() != null) {
            character = getIntent().getStringExtra("char");
        }

        txt_char_glyph.setText(character);

        //TODO: Get Composition Data
        FileAssets fileAssets = new FileAssets();
        decomp_string = fileAssets.getOutput(
                CharacterActivity.this,
                "cjk-decomp.txt",
                "^" + character + ".*?"
        );
        decomp_string = decomp_string.replace("\n", "");

        Pattern structure_pattern = Pattern.compile("\\:[a-z|0-9]*\\(");
        Matcher structure_matcher = structure_pattern.matcher(decomp_string);

        if (structure_matcher.find()){
            structure = structure_matcher.group(0);
            structure = structure.replace(":", "");
            structure = structure.replace("(", "");
        }

        //TODO: Set composition image
        String img_name = "ic_" + structure;
        int img_resid;
        try {
            img_resid = getResources().getIdentifier(
                    img_name, "drawable", getPackageName());
            if (img_resid == 0) {
                img_char_comp.setImageResource(R.drawable.ic_o);
                structure = "o";
            }
            else {img_char_comp.setImageResource(img_resid);}
        }catch (Exception e) {
            e.printStackTrace();
        }

        //TODO: List Pronunciations
        String pron;
        StringBuilder pron_output = new StringBuilder();
        pron = fileAssets.getOutput(
                getBaseContext(),
                "cedict_ts.txt",
                "^.*(?<= " + character + " ).*$"
        );
        Pattern pron_pattern = Pattern.compile("(?<= \\x5B).*(?=\\x5D )");
        Matcher pron_matcher = pron_pattern.matcher(pron);
        while (pron_matcher.find()) {
            pron_output.append(pron_matcher.group(0) + " | ");
        }
        txt_char_pron.setText(pron_output);

        //TODO: Split Composition list to Array
        Pattern components_pattern = Pattern.compile(
                "(?<=[\\x28|\\x2c]).[^\\x28|\\x29|\\x2c]*(?=[\\x29|\\x2c])"
        );
        Matcher components_matcher = components_pattern.matcher(decomp_string);
        components = new ArrayList<String>();
        while (components_matcher.find()){
            String found_component = components_matcher.group(0);
            Pattern is_unknown_pattern = Pattern.compile("[0-9]+");
            Matcher is_unknown_matcher = is_unknown_pattern.matcher(found_component);
            if (is_unknown_matcher.find() || structure == "o"){
                Log.i("CharacterActivity", "Composition is unknown");
                components.clear();
                components.add(character);
                c_type = 0;
                break;
            }
            else {
                Log.i("CharacterActivity", "Composition is known");
                components.add(found_component);
                c_type = 1;
            }
        }

        c_type = isPictogram (character, c_type);

        //TODO: Initialize txt_char_class
        String type_string;
        switch (c_type){
            case 1:
                type_string = "Phono-semantic";
                break;
            case 2:
                type_string = "Pictographic";
                break;
            default:
                type_string = "Other";
                break;
        }
        txt_char_class.setText(type_string);

        //TODO: Initiate characterItemList with CharacterAdapter
        CharacterAdapter characterAdapter = new CharacterAdapter(this, R.layout.character_composition, components, c_type);
        listView.setAdapter(characterAdapter);
    }

    public int isPictogram (String character, int sp_in){
        int sp = sp_in;
        String line;
        String filename = "pictogram.txt";

        Pattern pattern = Pattern.compile("^" + character + ".*?");
        InputStream inputStream;
        try {
            inputStream = this.getAssets().open(filename);
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((line = bufferedReader.readLine()) != null) {
                if (pattern.matcher(line).find()){
                    sp = 2;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sp;
    }

}
