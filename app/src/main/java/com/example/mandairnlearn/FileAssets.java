package com.example.mandairnlearn;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class FileAssets {
    public FileAssets () {}
    public String getOutput(Context context, String filename, String regex) {
        String text; // String for storing each line of the target file.
        Pattern pattern = Pattern.compile(regex);
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream;
        //TODO: Add findings into the StringBuilder
        try {
            inputStream = context.getAssets().open(filename); // Access to the assets folder
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((text = bufferedReader.readLine()) != null) {
                if (pattern.matcher(text).find()){
                    stringBuilder.append(text);
                    stringBuilder.append("\n");
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
