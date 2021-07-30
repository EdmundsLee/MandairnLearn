package com.example.mandairnlearn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityFragment extends Fragment {
    ArrayList<String> gamesArrayList, categoryArrayList;
    ArrayList<ActivityPerformItem> performanceArrayList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView txt_act_welcome;
    private RecyclerView view_act_games, view_act_perform;
    private RecyclerView.LayoutManager layout_manager;
    private ActivityGamesAdapter gamesAdapter;
    private ActivityPerformAdapter performAdapter;
    private ScoreItem scoreItem;

    public View view;
    public String list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activities, container, false);

        txt_act_welcome = view.findViewById(R.id.txt_Act_Welcome);
        databaseReference = firebaseDatabase.getInstance().getReference().child("Score");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String user = firebaseUser.getEmail();
        Pattern user_pattern = Pattern.compile("^.*(?=@)");
        Matcher user_matcher = user_pattern.matcher(user);
        if(user_matcher.find()) { txt_act_welcome.setText("Welcome, " + user_matcher.group(0)); }

        //TODO: List down the Available Activities / Quizzes / Games :D
        gamesArrayList = new ArrayList<String>();
        categoryArrayList = new ArrayList<String>();
        Pattern gamesPattern = Pattern.compile("(?<=\\:|\\,).[^\\,]*(?=\\,)");
        Pattern categoryPattern = Pattern.compile("^.*(?=\\:)");
        FileAssets fileAssets = new FileAssets();
        list = fileAssets.getOutput(
                getContext(),
                "games.txt",
                "^.*"
        );
        Matcher gamesMatcher = gamesPattern.matcher(list);
        if (gamesMatcher.find())    { gamesArrayList.add(gamesMatcher.group(0)); }
        Matcher categoryMatcher = categoryPattern.matcher(list);
        if (categoryMatcher.find())    { categoryArrayList.add(categoryMatcher.group(0)); }

        //TODO: List down the available Activities / Games
        buildViewActGames(view);

        //TODO: Reference Firebase Data to collect user scores
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("categoryArrayList", "size = " + categoryArrayList.size());
                performanceArrayList = new ArrayList<>();
                for (int i = 0; i < categoryArrayList.size(); i++){
                    Integer total_score = 0, score;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        scoreItem = dataSnapshot.getValue(ScoreItem.class);
                        if (String.valueOf(scoreItem.getUser()).equals(firebaseUser.getEmail()) &&
                                String.valueOf(scoreItem.getCategory()).equals(categoryArrayList.get(i))) {
                            score = Integer.valueOf(scoreItem.getScore());
                            total_score = total_score + score;
                        }
                        Log.i("getCategory", "\"" + String.valueOf(scoreItem.getCategory()) + "\"");
                    }
                    Log.i("performanceArrayList", "\"" + categoryArrayList.get(i) + "\"");
                    performanceArrayList.add(new ActivityPerformItem(categoryArrayList.get(i), total_score));
                    Log.i("performanceArrayList", performanceArrayList.get(i).getCategory() + " is " + performanceArrayList.get(i).getScore());
                }
                //TODO: Project findings to ActivityPerformAdapter
                buildViewActPerform(view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });
    }

    private void buildViewActPerform(View view) {
        view_act_perform = view.findViewById(R.id.view_Act_Perform);
        view_act_perform.setHasFixedSize(true);
        layout_manager = new LinearLayoutManager(this.getContext());
        view_act_perform.setLayoutManager(layout_manager);
        performAdapter = new ActivityPerformAdapter(performanceArrayList, view.getContext());
        view_act_perform.setAdapter(performAdapter);
        Log.i("buildViewActPerform", "Success");
        view_act_games.setClickable(true);
        view_act_games.setVisibility(View.VISIBLE);
    }

    private void buildViewActGames(View view) {
        view_act_games = view.findViewById(R.id.view_Act_Games);
        view_act_games.setHasFixedSize(true);
        layout_manager = new LinearLayoutManager(this.getContext());
        view_act_games.setLayoutManager(layout_manager);
        gamesAdapter = new ActivityGamesAdapter(gamesArrayList, view.getContext());
        view_act_games.setAdapter(gamesAdapter);
        view_act_games.setClickable(false);
        view_act_games.setVisibility(View.GONE);

        gamesAdapter.setListener(new ActivityGamesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String selected_game, current_user;
                Integer selected_total;
                Intent intent = new Intent(getActivity(), GameActivity.class);
                selected_game = gamesArrayList.get(position);
                current_user = firebaseUser.getEmail();
                for (int i = 0; i < performanceArrayList.size(); i++){
                    if (selected_game.equals(scoreItem.getGame()) &&
                            performanceArrayList.get(i).getCategory().equals(scoreItem.getCategory())) {
                        selected_total = performanceArrayList.get(i).getScore();
                        intent.putExtra("total", selected_total.toString());
                        break;
                    }
                }
                intent.putExtra("game", selected_game);
                intent.putExtra("user", current_user);
                startActivity(intent);
            }
        });
    }
}
