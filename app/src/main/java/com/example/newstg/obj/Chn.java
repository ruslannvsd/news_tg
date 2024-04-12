package com.example.newstg.obj;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "channels")
public class Chn {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") public int id;
    @ColumnInfo(name = "link") public String link;
    public Chn(
            int id,
            String link
    ) {
        this.id = id;
        this.link = link;
    }
}
