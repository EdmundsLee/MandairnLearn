package com.example.mandairnlearn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView view_topscores;
    private ScoreItem scoreItem;
    private GameAdapter gameAdapter;
    private TextView txt_Game_Desc, txt_Game_Title;
    private ImageView img_Game_Icon;
    private Button btn_Game_Play;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public ArrayList<Integer> scoreList;
    public String game, user, desc;
    public Integer score, level, goal = 0, progress = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //TODO: Initialize Activity Toolbar
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Activity");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });

        //TODO: Initialize Activity Elements
        view_topscores = findViewById(R.id.view_Act_Games);
        view_topscores.setHasFixedSize(true);
        view_topscores.setLayoutManager(new GridLayoutManager(this, 2));
        databaseReference = firebaseDatabase.getInstance().getReference("Score");
        txt_Game_Desc = findViewById(R.id.txt_Game_Desc);
        txt_Game_Title = findViewById(R.id.txt_Game_Title);
        img_Game_Icon = findViewById(R.id.img_Game_Icon);
        btn_Game_Play = findViewById(R.id.btn_Game_Play);

        //TODO: Get data from ActivityFragment
        if (getIntent().getExtras() != null){
            game = getIntent().getStringExtra("game");
            user = getIntent().getStringExtra("user");
            String temp;
            temp = getIntent().getStringExtra("total");
            score = Integer.valueOf(temp);
            for (level = 1; level <= 30; level++){
                Integer req = 2000 * level;
                goal += req;
                if (score < goal || level == 30){
                    progress = goal - score;
                    Log.i("score_total", level + ", " + score + ", " + goal + ", " + progress);
                    break;
                }
            }
        }

        //TODO: Set Game Title
        txt_Game_Title.setText(game);
        String temp = game.toLowerCase();
        temp = temp.replace(" ", "");
        String img_name = "ic_game_" + temp;
        int img_resid;
        try {
            img_resid = getResources().getIdentifier(img_name, "drawable", getPackageName());
            if (img_resid == 0) {
                img_Game_Icon.setImageResource(R.drawable.ic_game_o);
            }
            else {
                img_Game_Icon.setImageResource(img_resid);}
        } catch (Exception e) {
            e.printStackTrace();
        }

        //TODO: Set Game Description
        Pattern pattern = Pattern.compile("(?<=" + game + ":).*$");
        FileAssets fileAssets = new FileAssets();
        desc = fileAssets.getOutput(
                getBaseContext(),
                "games-desc.txt",
                "(?<=" + game + ":).*$"
        );
        Matcher matcher = pattern.matcher(desc);
        if (matcher.find()) { txt_Game_Desc.setText(matcher.group(0)); }

        //TODO: Set Play Button Action (aka the most dreaded one)
        btn_Game_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (game) {
                    case "Pronunciation":
                        intent = new Intent(GameActivity.this, GamePronunciation.class);
                        break;
                    case "Vocabulary":
                        intent = new Intent(GameActivity.this, GameVocabulary.class);
                        break;
                    default:
                        intent = new Intent(GameActivity.this, GameActivity.class);
                        break;
                }
                intent.putExtra("score", score.toString());
                intent.putExtra("level", level.toString());
                intent.putExtra("progress", progress.toString());
                startActivity(intent);
            }
        });

        //TODO: Set High Score Values
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scoreList = new ArrayList<Integer>();
                Log.i("addValueEventListener", "initiate");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    scoreItem = dataSnapshot.getValue(ScoreItem.class);
                    if (String.valueOf(scoreItem.getUser()).equals(user) &&
                            String.valueOf(scoreItem.getGame()).equals(game)) {
                        scoreList.add(Integer.valueOf(scoreItem.getScore()));
                    }
                }
                //TODO: Sorts scoreList for ranking
                Collections.sort(scoreList);
                Collections.reverse(scoreList);
                gameAdapter = new GameAdapter(getBaseContext(), scoreList);
                view_topscores.setAdapter(gameAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });
    }
}
