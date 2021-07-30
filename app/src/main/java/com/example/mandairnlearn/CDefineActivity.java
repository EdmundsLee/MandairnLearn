package com.example.mandairnlearn;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CDefineActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private SpeechRecognizer speech = null;

    private Intent speechIntent;
    private TextView txt_speak_score;
    private ImageButton btn_speak_play, btn_speak_rec;
    private ProgressBar prog_speak;
    private Toolbar toolbar;
    private GridView gridView; //Chinese Characters
    private ListView listView; //Definitions List

    public TextToSpeech textToSpeech;
    public String word, pronounce, def_string;
    public ArrayList<String> characters;
    public ArrayList<String> pinyin;
    public Boolean slowdown; //Toggles when Play button is hit multiple times
    public Boolean toggle;   //Toggles when Mic button is hit multiple times
    public String[] definitions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cdefine);

        //TODO: Initialize Activity Toolbar
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Dictionary");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });

        //TODO: Initialize Content
        if (getIntent().getExtras() != null) {

            //TODO: Assign Activity Elements
            txt_speak_score = findViewById(R.id.txt_speak_score);
            btn_speak_play = findViewById(R.id.btn_speak_play);
            btn_speak_rec = findViewById(R.id.btn_speak_rec);
            prog_speak = findViewById(R.id.prog_speak);
            btn_speak_play.setClickable(false);
            btn_speak_play.setEnabled(false);
            btn_speak_rec.setClickable(false);
            btn_speak_rec.setEnabled(false);
            slowdown = false;
            toggle = false;
            prog_speak.setVisibility(View.GONE);

            //TODO: Get data from DictFragment Searches
            word = getIntent().getStringExtra("word");
            pronounce = getIntent().getStringExtra("pinyin");
            def_string = getIntent().getStringExtra("definition");

            pronounce = pronounce.replace("[", "");
            pronounce = pronounce.replace("]", " ");

            //TODO: Separate String data elements into array
            Pattern char_pattern = Pattern.compile("[\\x{3400}-\\x{9fbb}]{1}");
            Pattern pinyin_pattern = Pattern.compile("[^\\s]*(?=[\\s])");
            Matcher char_matcher = char_pattern.matcher(word);
            Matcher pinyin_matcher = pinyin_pattern.matcher(pronounce);

            StringBuilder test = new StringBuilder();

            //TODO: Add individual character and pinyin to String Array
            characters = new ArrayList<String>();
            pinyin = new ArrayList<String>();

            int count = 0;
            while (char_matcher.find()){
                characters.add(char_matcher.group(count));
            }
            test.append("\n");
            count = 0;
            while (pinyin_matcher.find()){
                if (!pinyin_matcher.group(count).isEmpty()){
                    pinyin.add(pinyin_matcher.group(count));
                }
            }

            //TODO: Process word meaning
            definitions = def_string.split("\\x2f", -1);
            definitions = trimDefinitions(definitions, 0);
            definitions = trimDefinitions(definitions, definitions.length - 1);
            listView = findViewById(R.id.list_DictDef);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, R.layout.cdefine_definition, R.id.txt_DictDef_Define, definitions
            );
            listView.setAdapter(adapter);
        }

        //TODO: Project Characters to GridView
        gridView = findViewById(R.id.grid_DictDef_Char);
        CDefineAdapter dictDefAdapter = new CDefineAdapter(this, R.layout.cedefine_character, characters, pinyin);
        gridView.setAdapter(dictDefAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Transfer Data from CDefine to Character
                Intent intent = new Intent(CDefineActivity.this, CharacterActivity.class);
                String selected_char;
                selected_char = characters.get(position);
                intent.putExtra("char", selected_char);
                startActivity(intent);
            }
        });

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
                        btn_speak_play.setClickable(true);
                        btn_speak_play.setEnabled(true);
                    }
                }
                else { Log.e("TTS", "TTS initialization failed"); }
            }
        });

        //TODO: Activate Text-to-speech
        btn_speak_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        //TODO: Initialize Speech Recognition
        ActivityCompat.requestPermissions(CDefineActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                1);

        speech = SpeechRecognizer.createSpeechRecognizer(this);

        Log.i("SpeechRecognizer", "Available = " + SpeechRecognizer.isRecognitionAvailable(this));
        if (SpeechRecognizer.isRecognitionAvailable(this) == true){
            btn_speak_rec.setClickable(true);
            btn_speak_rec.setEnabled(true);
        }
        speech.setRecognitionListener(new listener());


        //TODO: Activate Speech Recognition
        btn_speak_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN");
                speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 128);
                if (toggle != true) {
                    prog_speak.setVisibility(View.VISIBLE);
                    prog_speak.setIndeterminate(true);
                    ActivityCompat.requestPermissions (CDefineActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_RECORD_PERMISSION);
                    toggle = true;
                } else {
                    prog_speak.setIndeterminate(false);
                    prog_speak.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                    toggle = false;
                }
            }
        });

    }

    //TODO: Project Characters to GridView
    public String[] trimDefinitions (String[] array, int position){
        String[] proxy = new String[array.length - 1];
        int j = 0;
        for (int i = 0; i < array.length; i++){
            if (i == position){ continue; }
            else {
                proxy[j] = array[i];
                j++;
            }
        }
        return proxy;
    }

    //TODO: Calibrate and Execute TTS
    private void speak() {
        float speed;
        if (slowdown == true){
            speed = 0.5f;
            slowdown = false;
        }
        else {
            speed = 1.0f;
            slowdown = true;
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    Log.i("SpeechRecognizer", "Permission Granted");
                    speech.startListening(speechIntent);
                } else {
                    Log.e("SpeechRecognizer", "Permission Denied");
                }
        }
    }

    //TODO: Speech Recognition Listener
    class listener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.i("SpeechRecognizer", "onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.i("SpeechRecognizer", "onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            prog_speak.setProgress((int) rmsdB);
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.i("SpeechRecognizer", "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Log.i("SpeechRecognizer", "onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
            Log.e("SpeechRecognizer", "error = " + error);
        }

        @Override
        public void onResults(Bundle results) {
            //TODO: Get Speech Compatibility to word (In percentage)
            Log.i("SpeechRecognizer", "onResults");

            // REM: "matches" lists the possible results in ascending order
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            // REM: "confidence" lists the percentage of accuracy between the speech and the results in the "matches" list
            float[] confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

            String text = "";
            for (int i = 0; i < matches.size(); i++){
                Log.i("SpeechRecognizer", matches.get(i) + " = " + confidence[i]);
            }
            for (int i = 0; i < matches.size(); i++){
                if (matches.get(i).equals(word)){
                    DecimalFormat df = new DecimalFormat("0.00");
                    float score = (float) confidence[i] * 100;
                    text = word + " : " + df.format(score) + "%";
                    break;
                }
                if (i == (matches.size() - 1)){
                    text = word + " : " + "0.00%";
                }
            }
            txt_speak_score.setText(text);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.i("SpeechRecognizer", "onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.i("SpeechRecognizer", "onEvent = " + eventType);
        }
    }
}