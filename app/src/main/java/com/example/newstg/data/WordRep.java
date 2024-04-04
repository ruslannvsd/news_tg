package com.example.newstg.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.newstg.obj.Word;
import java.util.List;

public class WordRep {
    private final WordDao wordDao;

    public WordRep(Application app) {
        WordDB db = WordDB.getDatabase(app);
        wordDao = db.wordDao();
    }

    public LiveData<List<Word>> getAll() { return wordDao.getAll(); }

    void insWord(Word wd) { wordDao.insWord(wd); }
    public void updWord(Word wd) { wordDao.updWord(wd); }
    public void delWord(Word wd) { wordDao.delWord(wd); }
    public LiveData<Boolean> exist(String wd) {
        return wordDao.exist(wd);
    }
}
