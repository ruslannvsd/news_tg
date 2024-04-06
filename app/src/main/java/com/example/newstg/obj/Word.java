package com.example.newstg.obj;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Word {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") private int id;
    @ColumnInfo(name = "word") private String word;
    @ColumnInfo(name = "color") private int color;
    @ColumnInfo(name = "num") private int num;
    public Word(int id, String word, int color, int num) {
        this.id = id;
        this.word = word;
        this.color = color;
        this.num = num;
    }
    // setters
    public void setId(int id) { this.id = id; }
    public void setWord(String word) { this.word = word; }
    public void setColor(int color) { this.color = color; }
    public void setNum(int num) { this.num = num; }

    // getters
    public int getId() { return id; }
    public String getWord() { return word; }
    public int getColor() { return color; }
    public int getNum() { return num; }
}