package com.example.newstg.obj;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Word {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") private int id;
    @ColumnInfo(name = "word") private String word;
    @ColumnInfo(name = "imp") private boolean imp;
    @ColumnInfo(name = "num") private int num;
    public Word(int id, String word, boolean imp, int num) {
        this.id = id;
        this.word = word;
        this.imp = imp;
        this.num = num;
    }
    // setters
    public void setId(int id) { this.id = id; }
    public void setWord(String word) { this.word = word; }
    public void setImp(boolean imp) { this.imp = imp; }
    public void setNum(int num) { this.num = num; }

    // getters
    public int getId() { return id; }
    public String getWord() { return word; }
    public boolean getImp() { return imp; }
    public int getNum() { return num; }
}