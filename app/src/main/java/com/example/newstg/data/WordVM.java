package com.example.newstg.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.newstg.obj.Article;
import com.example.newstg.obj.Word;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class WordVM extends AndroidViewModel {
    WordRep wordRep;
    private final LiveData<List<Word>> words;
    private final MutableLiveData<List<Article>> articlesLD = new MutableLiveData<>();
    private final MutableLiveData<List<Word>> wordsLD = new MutableLiveData<>();

    public WordVM(@NonNull Application app) {
        super(app);
        wordRep = new WordRep(app);
        words = wordRep.getAll();
    }

    public LiveData<List<Word>> getAll() { return words; }

    public void insWd(Word wd) {
        CompletableFuture.runAsync(() -> wordRep.insWord(wd), Executors.newSingleThreadExecutor());
    }
    public void updWd(Word wd) {
        CompletableFuture.runAsync(() -> wordRep.updWord(wd), Executors.newSingleThreadExecutor());
    }
    public void delWd(Word wd) {
        CompletableFuture.runAsync(() -> wordRep.delWord(wd), Executors.newSingleThreadExecutor());
    }
    public LiveData<Boolean> exist(String word) {
        return wordRep.exist(word); }
    public LiveData<List<Article>> getArticles() {
        return articlesLD;
    }
    public void setArticles(List<Article> articles) {
        articlesLD.setValue(articles);
    }
    public LiveData<List<Word>> getResults() {
        return wordsLD;
    }
    public void setWords(List<Word> words) {
        wordsLD.setValue(words);
    }
}