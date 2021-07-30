package com.example.mandairnlearn;

public class DictItem {
    private String word;
    private String pronounce;
    private String definition;

    public DictItem (String word, String pronounce, String definition) {
        this.word = word;
        this.pronounce = pronounce;
        this.definition = definition;
    }

    public String getWord() {
        return word;
    }

    public String getPronounce() {
        return pronounce;
    }

    public String getDefinition() {
        return definition;
    }
}
