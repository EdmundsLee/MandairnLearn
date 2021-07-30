package com.example.mandairnlearn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.List;
import java.util.Locale;

public class GamePronunciationDialog extends AppCompatDialogFragment {

    private TextView txt_title, txt_correct, txt_alt1, txt_alt2;
    private ImageButton btn_correct, btn_alt1, btn_alt2;
    private ImageView img_title;
    private DialogListener listener;

    public TextToSpeech textToSpeech;
    public String answ;
    public List<String> list;
    public Boolean corr;

    GamePronunciationDialog (String answ, List<String> list, Boolean corr) {
        this.answ = answ;
        this.list = list;
        this.corr = corr;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.game_pronunciation_popup, null);

        builder.setView(view).setPositiveButton("continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Boolean is_resume = true;
                listener.Resume(is_resume);
            }
        });

        //TODO: Initialize Layout Elements
        txt_title = view.findViewById(R.id.txt_PronGame_Boolean);
        txt_correct = view.findViewById(R.id.txt_PronGame_Correct);
        txt_alt1 = view.findViewById(R.id.txt_PronGame_IncorrectA);
        txt_alt2 = view.findViewById(R.id.txt_PronGame_IncorrectB);
        btn_correct = view.findViewById(R.id.btn_PronGame_Correct);
        btn_alt1 = view.findViewById(R.id.btn_PronGame_IncorrectA);
        btn_alt2 = view.findViewById(R.id.btn_PronGame_IncorrectB);
        img_title = view.findViewById(R.id.img_PronGame_Image);

        btn_correct.setClickable(false);
        btn_correct.setEnabled(false);
        btn_alt1.setClickable(false);
        btn_alt1.setEnabled(false);
        btn_alt2.setClickable(false);
        btn_alt2.setEnabled(false);

        //TODO: Change title if correct, defaults to incorrect
        if (corr){
            txt_title.setText("Correct");
            img_title.setImageResource(R.drawable.ic_correct);
        }

        //TODO: Set Text Content
        txt_correct.setText(answ);
        boolean temp = false;
        for (int i = 0; i < list.size(); i++){
            if (!(list.get(i)).equals(answ)){
                if (temp){ txt_alt1.setText(list.get(i)); }
                else     { txt_alt2.setText(list.get(i)); temp = true; }
            }
        }

        //TODO: Initialize Text-to-speech
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
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
                        btn_correct.setClickable(true);
                        btn_correct.setEnabled(true);
                        btn_alt1.setClickable(true);
                        btn_alt1.setEnabled(true);
                        btn_alt2.setClickable(true);
                        btn_alt2.setEnabled(true);
                    }
                }
                else { Log.e("TTS", "TTS initialization failed"); }
            }
        });
        btn_correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(txt_correct.getText().toString());
            }
        });
        btn_alt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(txt_alt1.getText().toString());
            }
        });
        btn_alt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(txt_alt2.getText().toString());
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    //TODO: Interface
    public interface DialogListener{
        void Resume(Boolean is_resume);
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
}
