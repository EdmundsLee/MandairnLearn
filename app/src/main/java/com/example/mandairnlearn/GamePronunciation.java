package com.example.mandairnlearn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GamePronunciation extends AppCompatActivity implements GamePronunciationDialog.DialogListener{
    private Toolbar toolbar;
    private Button button_a, button_b, button_c;
    private ImageButton button_speak, button_pause;
    private TextView txt_round, txt_timer, txt_pinyin;

    public TextToSpeech textToSpeech;
    public CountDownTimer countDownTimer;
    public ArrayList<String> vocab_list;
    public ArrayList<Integer> score_list;
    public List<List<String>> selection_list;
    public ArrayList<Integer> correct_list;
    public Integer current_score, level, progress;
    public Integer this_score = 0;
    public Long start_time, end_time;
    public int round = 6, select = 3;
    public int current;
    public long millis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_pronunciation);

        //TODO: Initialize Activity Toolbar
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pronunciation");

        //TODO: Initialize Activity Elements
        button_a = findViewById(R.id.btn_PronGame_AnswerA);
        button_b = findViewById(R.id.btn_PronGame_AnswerB);
        button_c = findViewById(R.id.btn_PronGame_AnswerC);
        button_speak = findViewById(R.id.btn_PronGame_Speak);
        button_pause = findViewById(R.id.btn_PronGame_Pause);
        txt_round = findViewById(R.id.txt_PronGame_Round);
        txt_timer = findViewById(R.id.txt_PronGame_Time);
        txt_pinyin = findViewById(R.id.txt_PronGame_Pinyin);
        button_speak.setClickable(false);
        button_speak.setEnabled(false);

        //TODO: Get data from GameActivity
        if (getIntent().getExtras() != null){
            current_score = Integer.valueOf(getIntent().getStringExtra("score"));
                    level = Integer.valueOf(getIntent().getStringExtra("level"));
                 progress = Integer.valueOf(getIntent().getStringExtra("progress"));
        }

        //TODO: Initialize Text-to-speech
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.CHINESE);
                    if (result == TextToSpeech.LANG_MISSING_DATA)
                    { Log.e("TTS", "Data is Missing"); }
                    else if (result == TextToSpeech.LANG_NOT_SUPPORTED)
                    { Log.e("TTS", "Language not Supported"); }
                    else {
                        //TODO: Code if tts initialization is successful
                        button_speak.setClickable(true);
                        button_speak.setEnabled(true);
                    }
                }
                else { Log.e("TTS", "TTS initialization failed"); }
            }
        });

        //TODO: Get vocabulary list from game_vocabulary.txt
        String vocab_string = "";
        FileAssets fileAssets = new FileAssets();
        //REM: Different sets of vocabulary by difficulty according to current level
        vocab_string = fileAssets.getOutput( this, "game_vocabulary.txt", "^" + level + "\\x3A.*$" );
        vocab_list = new ArrayList<String>();
        Pattern vocab_pattern = Pattern.compile("[\\x{3400}-\\x{9fbb}]*(?=\\x20)");
        Matcher vocab_matcher = vocab_pattern.matcher(vocab_string);
        while (vocab_matcher.find()){
            if (!vocab_matcher.group(0).isEmpty()){
                vocab_list.add(vocab_matcher.group(0));
            }
        }
        Log.i("vocab_list", "size = " + vocab_list.size());

        //TODO: Get correct answers
        Set<Integer> correct_set = new HashSet<>();
        while (correct_set.size() < round){
            correct_set.add((int) Math.round(Math.random() * (vocab_list.size() - 1) ));
        }
        correct_list = new ArrayList<>(correct_set);

        selection_list = new ArrayList<List<String>>();
        //TODO: Generate selection list
        for (int r = 0; r < round; r++){
            ArrayList<String> temp_list = new ArrayList<String>();

            //TODO: Get alternative answers
            int ind_select = 0;
            String[] alt_list = new String[select - 1];
            while (ind_select < (select - 1)){
                int alt_temp = (int) Math.round(Math.random() * (vocab_list.size() - 1));
                alt_list[ind_select] = vocab_list.get(alt_temp);
                if (!alt_list[ind_select].equals(vocab_list.get(correct_list.get(r))) &&
                        !(ind_select > 0 && alt_list[ind_select].equals(alt_list[ind_select - 1]))){
                    temp_list.add(alt_list[ind_select]);
                    ind_select++;
                }
            }
            temp_list.add(vocab_list.get(correct_list.get(r)));
            Log.i("answer_list", "list_"+r+"="+vocab_list.get(correct_list.get(r))+"|"+alt_list[0] +","+alt_list[1]);

            //TODO: Assign values for each element
            ArrayList<String> single_selection = new ArrayList<String>();
            ArrayList<Integer> answer_list = new ArrayList<>();
            for (int i = 0; i < temp_list.size(); i++) { answer_list.add(i); }
            int[] temp_a = new int[temp_list.size()];
            for (int i = 0; i < temp_list.size(); i++) {
                temp_a[i] = answer_list.remove((int) (Math.random() * answer_list.size() ));
                single_selection.add(temp_list.get(temp_a[i]));
            }

            selection_list.add(single_selection);
        }
        Log.i("selection_list", "totalup = " + selection_list);

        //TODO: Initialize Game-play
        current = 0;
        score_list = new ArrayList<>();
        genSingleRound(current, vocab_list.get(correct_list.get(current)));

        //TODO: Activate Text-to-speech
        button_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(vocab_list.get(correct_list.get(current)));
            }
        });

        //TODO: Check for answers
        button_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSelect(vocab_list.get(correct_list.get(current)), button_a.getText().toString(), selection_list.get(current));
            }
        });
        button_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSelect(vocab_list.get(correct_list.get(current)), button_b.getText().toString(), selection_list.get(current));
            }
        });
        button_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSelect(vocab_list.get(correct_list.get(current)), button_c.getText().toString(), selection_list.get(current));
            }
        });

        //TODO When pauses
        button_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pause();
            }
        });
    }

    //TODO When pauses
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Pause();
    }

    //TODO: Assign Values
    public void genSingleRound (int c, String word) {
        button_a.setText(selection_list.get(c).get(0));
        button_b.setText(selection_list.get(c).get(1));
        button_c.setText(selection_list.get(c).get(2));
        String txt_round_temp = (c+1) + "/" + selection_list.size();
        txt_round.setText(txt_round_temp);

        //TODO: Get pinyin from cedict_ts.txt
        FileAssets fileAssets = new FileAssets();
        String cedict_string = "";
        cedict_string = fileAssets.getOutput( this, "cedict_ts.txt", "^([^\\x20]*\\x20" + word + "\\x20).*$" );
        Pattern pronounce_pattern = Pattern.compile("(?<=\\x20)\\x5b[a-z0-9\\x20]*\\x5d(?=\\x20)");
        Log.i("cedict_string", cedict_string);
        Matcher pronounce_matcher = pronounce_pattern.matcher(cedict_string);
        if (pronounce_matcher.find()){
            String pinyin = pronounce_matcher.group(0);
            pinyin = pinyin.toLowerCase();
            txt_pinyin.setText(pinyin);
        }

        //TODO: Start countdown timer
        start_time = System.currentTimeMillis();
        countDownTimer = new CountDownTimer(91000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                millis = millisUntilFinished;
                long rawtime = millisUntilFinished / 1000;
                int min = (int) rawtime / 60;
                int s = (int) rawtime - (min * 60);
                String sec;
                if (s < 10) { sec = "0" + s; }
                else { sec = String.valueOf(s); }
                txt_timer.setText(min + ":" + sec);
            }
            @Override
            public void onFinish() {
                processSelect("null", "timeout", new ArrayList<>() );
            }
        }.start();

        speak(vocab_list.get(correct_list.get(current)));
    }

    //TODO: Answer Checking and Redirecting Function
    public void processSelect (String correct, String select, List<String> single_list) {
        Boolean is_correct = false;

        //TODO: Calculate time
        end_time = System.currentTimeMillis();
        countDownTimer.cancel();
        Long duration = ( (end_time - start_time) / 1000 );
        Log.i("GamePronunciation", "Duration = " + duration);

        if (current < round){
            Integer single_score = 0;
            if (select.equals("timeout")){
                Log.i("GamePronunciation", "Time-out");
            }
            else if (correct.equals(select)){
                Log.i("GamePronunciation", "Correct");
                single_score = 40;
                Integer duration_int = (int) (long) duration;
                if (duration_int < 60) { single_score += (60 - duration_int); }
                is_correct = true;
            }
            else {
                Log.i("GamePronunciation", "Wrong");
            }
            this_score += single_score;
            score_list.add(single_score);
            Log.i("GamePronunciation", "Score = " + single_score);
        }

        //TODO: Open GamePronunciation Dialog
        openDialog(correct, single_list, is_correct);
    }

    //TODO: Calibrate and Execute TTS
    private void speak(String word) {
        float speed = 1.0f;
        textToSpeech.setSpeechRate(speed);
        textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, "pronunciation");
    }
    @Override
    public void onDestroy() {
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    //TODO: Open GamePronunciation Dialog
    public void openDialog (String answ, List<String> list, Boolean corr) {
        GamePronunciationDialog dialog = new GamePronunciationDialog(answ, list, corr);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    //TODO: Pause dialog
    public void Pause () {
        //TODO: Pause Time
        long pause_start = System.currentTimeMillis();
        countDownTimer.cancel();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Paused");
        builder.setCancelable(false);
        builder.setMessage("Press \"Continue\" to resume the activity.\nPress \"Exit\" to return to the Game Menu.");
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: Resume Time
                long pause_end = System.currentTimeMillis();
                start_time = start_time + (pause_end - pause_start);
                long resume_millis = millis;
                countDownTimer = new CountDownTimer(resume_millis, 1000){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        millis = millisUntilFinished;
                        long rawtime = millisUntilFinished / 1000;
                        int min = (int) rawtime / 60;
                        int s = (int) rawtime - (min * 60);
                        String sec;
                        if (s < 10) { sec = "0" + s; }
                        else { sec = String.valueOf(s); }
                        txt_timer.setText(min + ":" + sec);
                    }
                    @Override
                    public void onFinish() {
                        processSelect("null", "timeout", new ArrayList<>() );
                    }
                }.start();
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //TODO: Resume game if Pop-up Dialog is closed
    @Override
    public void Resume(Boolean is_resume) {
        if (is_resume){
            //TODO: Direct to conclusion if game ends, next round if else (After Dialog)
            if (current == (round - 1)){
                float bonus = (1.0f + ( 4.0f * ( (float) (level-1) / 29.0f )));
                float total = (float) this_score * bonus;
                Integer total_int = Integer.valueOf(Math.round(total));
                Log.i("GamePronunciation", "Bonus = " + bonus);
                Log.i("GamePronunciation", "Total Score = " + total_int);
                Log.i("GamePronunciation", "Score Timeline = " + score_list.toString());
                Log.i("GamePronunciation", "Round ends, redirecting ... ");

                //TODO: Redirect to GameEnd
                Intent intent = new Intent(GamePronunciation.this, GameEnd.class);
                intent.putExtra("current_score", current_score);
                intent.putExtra("current_level", level);
                intent.putExtra("current_progress", progress);
                intent.putExtra("bonus", String.valueOf(bonus));
                intent.putExtra("gain", total_int.toString());
                intent.putExtra("list", score_list.toString());
                intent.putExtra("game", "Pronunciation");
                startActivity(intent);
                finish();
            }
            else {
                current++;
                genSingleRound(current, vocab_list.get(correct_list.get(current)));
            }
        }
    }
}
