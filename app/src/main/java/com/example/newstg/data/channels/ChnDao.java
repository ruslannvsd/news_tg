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
    @Query("select exists(select 1 from channels where link = :link limit 1)")
    LiveData<Boolean> linkExist(String link);

    @Insert
    void insChn(Chn chn);

    @Update
    void updChn(Chn chn);

    @Delete
    void delChn(Chn chn);

    @Query("select * from channels")
    LiveData<List<Chn>> getAll();
}