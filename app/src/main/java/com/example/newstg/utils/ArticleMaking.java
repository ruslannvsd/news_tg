package com.example.newstg.utils;

import static com.example.newstg.consts.Cons.ART_META;
import static com.example.newstg.consts.Cons.LINK;
import static com.example.newstg.consts.Cons.SECTION;

import androidx.annotation.Nullable;

import com.example.newstg.obj.Article;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class ArticleMaking {
    @Nullable
    public Article makeArticle(String chnTitle, Element el, int color, String body, String keyW, long millis) {
        String imgLink = WordFuncs.getLink(el);
        Element linkElement = el.select("span." + ART_META + " a." + SECTION).first();
        String artLink = linkElement.attr(LINK);
        List<String> keywords = new ArrayList<>();
        keywords.add(keyW);
        return new Article(0, chnTitle, imgLink, artLink, color, body, millis, keywords);
    }
}
