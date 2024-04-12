package com.example.newstg.data.keywords;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.newstg.obj.Word;

import java.util.List;


@Dao
public interface WordDao {
    @Query("select exists(select 1 from keywords where word = :wd limit 1)")
    LiveData<Boolean> exist(String wd);

    @Insert
    void insWord(Word wd);

    @Update
    void updWord(Word wd);

    @Delete
    void delWord(Word wd);

    @Query("select * from keywords")
    LiveData<List<Word>> getAll();

    @Query("select * from keywords where status = 1")
    LiveData<List<Word>> getTrue();
}