package com.example.newstg.obj;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Article {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") public int id;
    @ColumnInfo(name = "title") public String chnTitle;
    @ColumnInfo(name = "img") public String image;
    @ColumnInfo(name = "link") public String link;
    @ColumnInfo(name = "body") public String body;
    @ColumnInfo(name = "time") public long time;
    @ColumnInfo(name = "keywords") public List<String> keywords;
    public Article(
            int id,
            String chnTitle,
            String image,
            String link,
            String body,
            long time,
            List<String> keywords
    ) {
        this.id = id;
        this.chnTitle = chnTitle;
        this.image = image;
        this.link = link;
        this.body = body;
        this.time = time;
        this.keywords = keywords;
    }
}
