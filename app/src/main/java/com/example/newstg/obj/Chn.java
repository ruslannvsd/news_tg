package com.example.newstg.obj;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "channels")
public class Chn {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") public int id;
    @ColumnInfo(name = "link") public String link;
    @ColumnInfo(name = "name") public String name;
    @ColumnInfo(name = "category") public int category;
    public Chn(
            int id,
            String link,
            String name,
            int category
    ) {
        this.id = id;
        this.link = link;
        this.name = name;
        this.category = category;
    }
}
