package com.example.newstg.utils;

import static java.lang.Integer.compare;

import android.content.Context;

import com.example.newstg.consts.Cons;
import com.example.newstg.data.WordVM;
import com.example.newstg.obj.Article;
import com.example.newstg.obj.Word;

import java.util.List;

public class Count {
    public List<Word> results(List<Word> keywords, List<Article> articles, Context ctx, WordVM wordVm) {
        int total = 0;
        int count = 0;
        for (Word wd : keywords) {
            for (Article article : articles) {
                if (article.keywords.contains(wd.getWord())) {
                    count++;
                }
            }
            wd.setNum(count);
            total += count;
            count = 0;
        }
        Word allWord = new Word(0, Cons.ALL, true, total);
        keywords.add(0, allWord);
        return keywords;
        // keywords.add(new Keyword(Cons.ALL, total));
        // Toast.makeText(ctx, "", Toast.LENGTH_SHORT).show();
        // keywords.sort((k1, k2) -> compare(k2.getNum(), k1.getNum()));
        // Log.i("Custom list : ", Integer.toString(keywords.get(0).getNum()));
    }
}
