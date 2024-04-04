package com.example.newstg.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.newstg.obj.Word;

@Database(entities = { Word.class }, version = 1, exportSchema = false)
abstract public class WordDB extends RoomDatabase {
    public abstract WordDao wordDao();
    private static volatile WordDB INSTANCE;

    public static synchronized WordDB getDatabase(final Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(), WordDB.class, "word_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
