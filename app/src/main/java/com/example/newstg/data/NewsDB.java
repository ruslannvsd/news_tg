package com.example.newstg.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.newstg.data.channels.ChnDao;
import com.example.newstg.data.keywords.WordDao;
import com.example.newstg.obj.Chn;
import com.example.newstg.obj.Word;

@Database(entities = { Word.class, Chn.class }, version = 1, exportSchema = false)
abstract public class NewsDB extends RoomDatabase {
    public abstract WordDao wordDao();
    public abstract ChnDao chnDao();
    private static volatile NewsDB INSTANCE;

    public static synchronized NewsDB getDatabase(final Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(), NewsDB.class, "word_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
