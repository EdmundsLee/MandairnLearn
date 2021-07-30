package com.example.mandairnlearn;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DictFragment extends Fragment {

    ArrayList<DictItem> dictItems;

    private RecyclerView recycler;
    private DictAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    EditText edit_search;
    ImageButton btn_search;
    Button btn_toggle;
    TextView txt_search;
    ProgressBar progressBar;
    public View view;
    public Boolean is_ec = false; //TODO: is english-chinese, if true the app accepts english input instead

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dict, container, false);
        edit_search = view.findViewById(R.id.edit_Dict_Search);
        btn_search = view.findViewById(R.id.btn_Dict_Search);
        btn_toggle = view.findViewById(R.id.btn_Dict_Toggle);
        txt_search = view.findViewById(R.id.txt_Dict_Search);
        progressBar = view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        btn_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_ec == true){
                    btn_toggle.setText("C-E");
                    is_ec = false;
                }
                else {
                    btn_toggle.setText("E-C");
                    is_ec = true;
                }
                txt_search.setText("You've searched: \"\".");
                edit_search.getText().clear();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String word, pronounce, definition;
                String unicode;
                String input = edit_search.getText().toString();
                Boolean is_error;
                dictItems = new ArrayList<>();

                //TODO: EditText edit_search condition check, returns function if met (as Errors)
                if (TextUtils.isEmpty(input.trim())){
                    edit_search.setError("This field cannot be left empty");
                    edit_search.requestFocus();
                    is_error = true;
                    return;
                }
                if (input.matches("^.*[^\\x{3400}-\\x{9fbb}].*?") && !is_ec){
                    edit_search.setError("Only Chinese characters are allowed");
                    edit_search.requestFocus();
                    is_error = true;
                    return;
                }
                if (input.matches("[\\x{3400}-\\x{9fbb}]{5,}+") && !is_ec){
                    edit_search.setError("Only a maximum of 4 characters are allowed");
                    edit_search.requestFocus();
                    is_error = true;
                    return;
                }
                if (input.matches("^.*[^a-zA-Z\\x20\\x27].*?") && is_ec){
                    edit_search.setError("Only English words are allowed");
                    edit_search.requestFocus();
                    is_error = true;
                    return;
                }
                if (input.matches("[a-zA-Z\\x20\\x27]{129,}+") && is_ec){
                    edit_search.setError("Only a maximum of 128 characters are allowed");
                    edit_search.requestFocus();
                    is_error = true;
                    return;
                }

                //TODO: regex for extracting chinese word from individual string
                Pattern word_pattern = Pattern.compile("\\x20[\\x{3400}-\\x{9fbb}]+\\x20");
                Pattern pronounce_pattern = Pattern.compile("\\x20\\x5b[a-z0-9\\x20]*\\x5d\\x20");
                Pattern definition_pattern = Pattern.compile("\\x2f.*$");

                //TODO: get user input from "cedict_ts.txt", separate searches into array
                FileAssets fileAssets = new FileAssets();
                if (is_ec) {
                    unicode = fileAssets.getOutput(
                            getContext(),
                            "cedict_ts.txt",
                            "^.*(?<=\\x2F)" + input + "(?=\\x2F).*$"
                    );
                }
                else {
                    unicode = fileAssets.getOutput(
                            getContext(),
                            "cedict_ts.txt",
                            "^([^\\x20]*\\x20" + input + ").*$"
                    );
                }
                String[] unicode_array = unicode.split("\\n", -1);

                //TODO: extract chinese word from string array, append into ArrayList dictItems
                for (int i = 0; i < unicode_array.length; i++){
                    //TODO: assign Matchers with regex
                    Matcher word_matcher = word_pattern.matcher(unicode_array[i]);
                    Matcher pronounce_matcher = pronounce_pattern.matcher(unicode_array[i]);
                    Matcher definition_matcher = definition_pattern.matcher(unicode_array[i]);

                    //TODO: Implement Matcher findings into dictItems
                    if (pronounce_matcher.find() && word_matcher.find() && definition_matcher.find()){
                        pronounce = pronounce_matcher.group(0);
                        word = word_matcher.group(0);
                        definition = definition_matcher.group(0);
                        word = word.replaceAll("\\s+", "");

                        //TODO: Limit word search to 4 characters or less
                        Pattern word_limit_pattern = Pattern.compile("^[\\x{3400}-\\x{9fbb}]{5,}+.?");
                        Matcher word_limit_matcher = word_limit_pattern.matcher(word);
                        if (!word_limit_matcher.find()){
                            dictItems.add(new DictItem(word, pronounce, definition));
                        }
                    }
                }

                //TODO: Warnings, does not return the function
                if (dictItems.size() > 50){
                    edit_search.setError("The list is too large. Define your search to find your result faster.");
                    edit_search.requestFocus();
                }
                if (dictItems.size() <= 0){
                    edit_search.setError("Result(s) not found.");
                    edit_search.requestFocus();
                }

                //TODO: finalize recycler association, execute
                buildRecyclerView(view);
                txt_search.setText("You've searched: \"" + input + "\".");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void buildRecyclerView (View v) {
        recycler = v.findViewById(R.id.view_Dict_Search);
        recycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        recycler.setLayoutManager(layoutManager);
        adapter = new DictAdapter(dictItems, is_ec);
        recycler.setAdapter(adapter);

        adapter.setListener(new DictAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String selected_word, selected_pinyin, selected_definition;
                selected_word = dictItems.get(position).getWord();
                selected_pinyin = dictItems.get(position).getPronounce();
                selected_definition = dictItems.get(position).getDefinition();
                Intent intent = new Intent(getActivity(), CDefineActivity.class);
                intent.putExtra("word", selected_word);
                intent.putExtra("pinyin", selected_pinyin);
                intent.putExtra("definition", selected_definition);
                startActivity(intent);
            }
        });
    }
}