package com.example.newstg.data;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.newstg.data.articles.ArtDao;
import com.example.newstg.data.channels.ChnDao;
import com.example.newstg.data.keywords.WordDao;
import com.example.newstg.obj.Article;
import com.example.newstg.obj.Chn;
import com.example.newstg.obj.Word;
import java.util.List;

public class NewsRep {
    private final WordDao wordDao;
    private final ChnDao chnDao;
    private final ArtDao artDao;

    public NewsRep(Application app) {
        NewsDB db = NewsDB.getDatabase(app);
        wordDao = db.wordDao();
        chnDao = db.chnDao();
        artDao = db.artDao();
    }

    // keywords functions
    public LiveData<List<Word>> getAll() { return wordDao.getAll(); }
    public LiveData<List<Word>> getTrue() { return wordDao.getTrue(); }
    void insWord(Word wd) { wordDao.insWord(wd); }
    public void updWord(Word wd) { wordDao.updWord(wd); }
    public void delWord(Word wd) { wordDao.delWord(wd); }

    // channels functions
    public LiveData<List<Chn>> getChannels() { return chnDao.getAll(); }
    void insChn(Chn chn) { chnDao.insChn(chn); }
    public void updChn(Chn chn) { chnDao.updChn(chn); }
    public void delChn(Chn chn) { chnDao.delChn(chn); }

    public void delByUrl(String chn) { chnDao.deleteByUrl(chn); }

    public LiveData<List<Article>> getOffArt() { return artDao.getArticles(); }
    public void delOffArt() { artDao.deleteAll(); }

}
