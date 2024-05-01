package com.example.newstg.obj;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "articles")
public class Article {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") public int id;
    @ColumnInfo(name = "title") public String chnTitle;
    @ColumnInfo(name = "img") public String image;
    @ColumnInfo(name = "link") public String link;
    @ColumnInfo(name = "color") public int color;
    @ColumnInfo(name = "body") public String body;
    @ColumnInfo(name = "time") public long time;
    @ColumnInfo(name = "keywords") public List<String> keywords;
    public Article(
            int id,
            String chnTitle,
            String image,
            String link,
            int color,
            String body,
            long time,
            List<String> keywords
    ) {
        this.id = id;
        this.chnTitle = chnTitle;
        this.image = image;
        this.link = link;
        this.color = color;
        this.body = body;
        this.time = time;
        this.keywords = keywords;
    }
}
