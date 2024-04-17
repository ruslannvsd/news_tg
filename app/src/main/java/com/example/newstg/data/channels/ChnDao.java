package com.example.newstg.data.channels;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.newstg.obj.Chn;

import java.util.List;

@Dao
public interface ChnDao {
    @Insert
    void insChn(Chn chn);

    @Update
    void updChn(Chn chn);

    @Delete
    void delChn(Chn chn);

    @Query("select * from channels")
    LiveData<List<Chn>> getAll();

    @Query("DELETE FROM channels WHERE link = :url")
    void deleteByUrl(String url);
}