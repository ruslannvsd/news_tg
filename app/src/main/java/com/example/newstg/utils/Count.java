package com.example.newstg.utils;

import android.widget.TextView;

import com.example.newstg.obj.Article;
import com.example.newstg.obj.Word;

import java.util.List;

public class Count {
    public List<Word> results(List<Word> keywords, List<Article> articles) {
        int count = 0;
        for (Word wd : keywords) {
            for (Article article : articles) {
                if (article.keywords.contains(wd.getWord())) {
                    count++;
                }
            }
            wd.setNum(count);
            count = 0;
        }
        return keywords;
    }
    public void articleAmountText(TextView unique, int amount) {
        String articleText = amount + " " + (amount == 1 ? "ARTICLE" : "ARTICLES");
        unique.setText(articleText);
    }
}
