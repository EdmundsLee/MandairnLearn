package com.example.mandairnlearn;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameEnd extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView view_score;
    private Button btn_return;
    private TextView txt_answered, txt_score, txt_bonus, txt_message, txt_title;
    private ImageView img_title;
    private static DecimalFormat df = new DecimalFormat("0.00");

    private ScoreItem scoreItem;
    private GameEndAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public String game;
    public Integer score;
    public Float bonus;
    ArrayList<String> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameend);

        //TODO: Initialize Activity Toolbar
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Summary");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });

        //TODO: Initialize Activity Elements
        btn_return   = findViewById(R.id.btn_GameEnd_Back);
        txt_answered = findViewById(R.id.txt_GameEnd_Corrects);
        txt_bonus    = findViewById(R.id.txt_GameEnd_LvlBonus);
        txt_score    = findViewById(R.id.txt_GameEnd_Scored);
        txt_message  = findViewById(R.id.txt_GameEnd_Message);
        txt_title    = findViewById(R.id.txt_GameEnd_Title);
        img_title    = findViewById(R.id.img_GameEnd_Icon);

        databaseReference = firebaseDatabase.getInstance().getReference("Score");

        //TODO: Get data from Games
        if (getIntent().getExtras() != null){
            String temp = getIntent().getStringExtra("bonus");
            bonus = Float.valueOf(temp);
            score = Integer.valueOf(getIntent().getStringExtra("gain"));
            game =  getIntent().getStringExtra("game");

            String temp_list;
            temp_list =  getIntent().getStringExtra("list");
            Log.i("GameEnd", "temp_list = " + temp_list);

            //TODO: String temp_list -> ArrayList<String> list
            temp_list = temp_list.replace("[", "");
            temp_list = temp_list.replace("]", "");
            list = new ArrayList<String>(Arrays.asList(temp_list.split(", ")));

            //TODO: set img_title
            String ictemp = game.toLowerCase();
            ictemp = ictemp.replace(" ", "");
            String img_name = "ic_game_" + ictemp;
            int img_resid;
            try {
                img_resid = getResources().getIdentifier(img_name, "drawable", getPackageName());
                if (img_resid == 0) {
                    img_title.setImageResource(R.drawable.ic_game_o);
                }
                else {
                    img_title.setImageResource(img_resid);}
            } catch (Exception e) {
                e.printStackTrace();
            }

            int ans_correct = 0;
            for (int i = 0; i < list.size(); i++){
                if (!(list.get(i)).equals("0")) ans_correct++;
            }

            //TODO: Set Simple Layout Elements
            txt_title.setText(game);
            txt_score.setText(score + " Points");
            txt_bonus.setText("x" + df.format(bonus));
            txt_answered.setText(ans_correct + "/" + list.size());
            int quarter = (list.size() / 4), half = (list.size() / 2);
                 if (ans_correct <= quarter)
                txt_message.setText("Oh no.. Try Again!");
            else if (ans_correct <= half)
                txt_message.setText("Nice Job!");
            else if (ans_correct <= (quarter + half))
                txt_message.setText("Well Done!");
            else
                txt_message.setText("Excellent!");
        }

        //TODO: Display Score List for the Activity
        buildViewScore();

        //TODO: Get Scores from Firebase
        scoreItem = new ScoreItem();

        //TODO: Save Scores into Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        scoreItem.setScore(score.toString());
        scoreItem.setUser(firebaseUser.getEmail());
        scoreItem.setGame(game);
        String game_list, category;
        FileAssets fileAssets = new FileAssets();
        game_list = fileAssets.getOutput(this, "games.txt", "^.*" + game + ".*$" );
        Pattern categoryPattern = Pattern.compile("^.*(?=\\:)");
        Matcher categoryMatcher = categoryPattern.matcher(game_list);
        if (categoryMatcher.find()){
            category = categoryMatcher.group(0);
            scoreItem.setCategory(category);
        }
        String id = databaseReference.push().getKey();
        databaseReference.child(id).setValue(scoreItem);

        //TODO: Button to return to Game Menu
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void buildViewScore(){
        view_score = findViewById(R.id.view_Act_GameEnd);
        view_score.setHasFixedSize(true);
        view_score.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new GameEndAdapter(list, this);
        view_score.setAdapter(adapter);
    }
}
