package com.example.newstg.data.articles;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.newstg.obj.Article;

import java.util.List;

@Dao
public interface ArtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Article> articles);

    @Query("SELECT * FROM articles")
    LiveData<List<Article>> getArticles();

    @Query("DELETE FROM articles")
    void deleteAll();
}
