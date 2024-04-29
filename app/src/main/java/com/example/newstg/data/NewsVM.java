package com.example.newstg.data;

import android.app.Application;
import androidx.core.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.newstg.obj.Article;
import com.example.newstg.obj.Chn;
import com.example.newstg.obj.Word;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class NewsVM extends AndroidViewModel {
    private NewsRep newsRep;
    private final LiveData<List<Word>> words;
    private final LiveData<List<Word>> onlyTrue;
    private final LiveData<List<Chn>> channels;
    private final LiveData<List<Article>> articles;

    private final MutableLiveData<List<Article>> articlesLD = new MutableLiveData<>();
    private final MutableLiveData<List<Word>> wordsLD = new MutableLiveData<>();

    private final MediatorLiveData<Pair<List<Word>, List<Chn>>> wordsAndChannels = new MediatorLiveData<>();

    public NewsVM(@NonNull Application app) {
        super(app);
        newsRep = new NewsRep(app);
        words = newsRep.getAll();
        onlyTrue = newsRep.getTrue();
        channels = newsRep.getChannels();
        articles = newsRep.getOffArt();

        wordsAndChannels.addSource(onlyTrue, wordsList -> combine(wordsList, channels.getValue()));
        wordsAndChannels.addSource(channels, channelsList -> combine(onlyTrue.getValue(), channelsList));
    }

    private void combine(List<Word> wordsList, List<Chn> channelsList) {
        wordsAndChannels.setValue(Pair.create(wordsList, channelsList));
    }

    public LiveData<Pair<List<Word>, List<Chn>>> lists() { return wordsAndChannels; }

    public LiveData<List<Word>> getAll() { return words; }

    public void insWd(Word wd) {
        CompletableFuture.runAsync(() -> newsRep.insWord(wd), Executors.newSingleThreadExecutor());
    }
    public void updWd(Word wd) {
        CompletableFuture.runAsync(() -> newsRep.updWord(wd), Executors.newSingleThreadExecutor());
    }
    public void delWd(Word wd) {
        CompletableFuture.runAsync(() -> newsRep.delWord(wd), Executors.newSingleThreadExecutor());
    }
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

    // channels functions
    public LiveData<List<Chn>> getChannels() {
        return channels;
    }

    public void insChn(Chn chn) {
        CompletableFuture.runAsync(() -> newsRep.insChn(chn), Executors.newSingleThreadExecutor());
    }
    public void updChn(Chn chn) {
        CompletableFuture.runAsync(() -> newsRep.updChn(chn), Executors.newSingleThreadExecutor());
    }
    public void delChn(Chn chn) {
        CompletableFuture.runAsync(() -> newsRep.delChn(chn), Executors.newSingleThreadExecutor());
    }

    public void delChn(String chn) {
        CompletableFuture.runAsync(() -> newsRep.delByUrl(chn), Executors.newSingleThreadExecutor());
    }

    public LiveData<List<Article>> offArticles() { return articles; }
    public void clearArticles() {
        CompletableFuture.runAsync(() -> newsRep.delOffArt(), Executors.newSingleThreadExecutor());
    }
}