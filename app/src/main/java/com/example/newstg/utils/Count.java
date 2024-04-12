package com.example.newstg.utils;

import android.content.Context;

import com.example.newstg.R;
import com.example.newstg.consts.Cons;
import com.example.newstg.data.NewsVM;
import com.example.newstg.obj.Article;
import com.example.newstg.obj.Word;

import java.util.List;

public class Count {
    public List<Word> results(List<Word> keywords, List<Article> articles, Context ctx, NewsVM wordVm) {
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
        Word allWord = new Word(0, Cons.ALL, ctx.getColor(R.color.cloud), total, true);
        keywords.add(0, allWord);
        return keywords;
    }
}
