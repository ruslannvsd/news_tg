package com.example.newstg.network;

import static com.example.newstg.consts.Cons.ART_META;
import static com.example.newstg.consts.Cons.DATETIME;
import static com.example.newstg.consts.Cons.D_TIME;
import static com.example.newstg.consts.Cons.JS_TEXT;
import static com.example.newstg.consts.Cons.MESSAGE_DIV;
import static com.example.newstg.consts.Cons.MESS_REPLY;
import static com.example.newstg.consts.Cons.TEXT_DIV;
import static com.example.newstg.network.FetchUtils.merging;
import static com.example.newstg.network.FetchUtils.replaceBR;

import com.example.newstg.consts.Cons;
import com.example.newstg.obj.Article;
import com.example.newstg.obj.Chn;
import com.example.newstg.obj.Word;
import com.example.newstg.utils.ArticleMaking;
import com.example.newstg.utils.TimeFuncs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetArtOffline {
    int interval;
    public List<Article> getArt(List<Word> keywords, List<Chn> channels, int interval) {
        this.interval = interval;
        List<Article> articles = new ArrayList<>();
        for (Chn chn : channels) {
            try {
                articles.addAll(gettingArticles(chn.link, interval, keywords));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return merging(articles);
    }

    private List<Article> gettingArticles(String link, int hours, List<Word> keywords) throws IOException {
        Document doc;
        List<Article> articles = new ArrayList<>();

        doc = Jsoup.connect(link).timeout(20 * 1000).get();
        if (doc.text().contains("you can contact")) {
            return articles;
        }
        Elements messageSections = doc.select("div." + MESSAGE_DIV);
        String chnTitle = doc.select("meta[property=og:title]").first().attr("content");
        List<Element> reversibleList = new ArrayList<>(messageSections);
        Collections.reverse(reversibleList);
        for (Element section : reversibleList) {
            Elements allTextDivs = section.select("div." + TEXT_DIV + JS_TEXT);
            boolean hasVideo = !section.select(Cons.VIDEO).isEmpty();
            for (Element articleBody : allTextDivs) {
                if (articleBody.parent() != null && !articleBody.parent().hasClass(MESS_REPLY)) {
                    long now = System.currentTimeMillis();
                    long hoursMilli = (long) hours * 60 * 60 * 1000;
                    long threshold = now - hoursMilli;
                    String articleTime = section.select("span." + ART_META + DATETIME).attr(D_TIME);
                    long millis = TimeFuncs.convertToMillis(articleTime);
                    if (millis >= threshold) {
                        for (Word keyword : keywords) {
                            String word = keyword.getWord();
                            String artBody = replaceBR(articleBody);
                            String lower = artBody.toLowerCase();
                            if (hasVideo) {
                                artBody = "[VIDEO]\n\n" + artBody;
                            }
                            if (!word.contains("_")) {
                                if (lower.contains(word.toLowerCase())) {
                                    Article art = new ArticleMaking().makeArticle(chnTitle, section, artBody, word, millis);
                                    if (art != null) {
                                        articles.add(art);
                                    }
                                }
                            } else {
                                String[] splitWord = word.split("_");
                                if (lower.contains(splitWord[0].toLowerCase()) && lower.contains(splitWord[1].toLowerCase())) {
                                    Article art = new ArticleMaking().makeArticle(chnTitle, section, artBody, word, millis);
                                    if (art != null) {
                                        articles.add(art);
                                    }
                                }
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return articles;
    }
}
