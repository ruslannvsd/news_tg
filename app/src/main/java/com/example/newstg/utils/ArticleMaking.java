package com.example.newstg.utils;

import static com.example.newstg.consts.Cons.ART_META;
import static com.example.newstg.consts.Cons.FORWARDED;
import static com.example.newstg.consts.Cons.LINK;
import static com.example.newstg.consts.Cons.SECTION;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.example.newstg.obj.Article;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class ArticleMaking {
    String sourceTitle = null;
    String sourceLink = null;
    @Nullable
    public Article makeArticle(
            String chnTitle,
            Element el,
            int color,
            String body,
            String keyW,
            long millis
    ) {
        String imgLink = WordFuncs.getLink(el);
        Element linkElement = el.select("span." + ART_META + " a." + SECTION).first();
        String artLink = linkElement.attr(LINK);
        Element forwarded = el.select(FORWARDED).first();
        if (forwarded != null) {
            Pair<String, String> forwardedArt = forwardArt(forwarded);
            chnTitle = appending(chnTitle, forwardedArt.first);
            artLink = appending(artLink, forwardedArt.second);
        }
        List<String> keywords = new ArrayList<>();
        keywords.add(keyW);
        sourceTitle = null;
        sourceLink = null;
        return new Article(0, chnTitle, imgLink, artLink, color, body, millis, keywords);
    }

    private Pair<String, String> forwardArt(Element forwarded) {
        if (forwarded != null) {
            sourceTitle = forwarded.text();
            sourceLink = forwarded.attr("href");
        }
        return new Pair<>(sourceTitle, sourceLink);
    }
    private String appending(String original, String toAppend) {
        if (toAppend != null && !toAppend.isEmpty()) {
            return original.isEmpty() ? toAppend : original + "|" + toAppend;
        }
        return original;
    }
}
