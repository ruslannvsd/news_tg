package com.example.newstg.network;

import static java.lang.String.join;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.newstg.R;
import com.example.newstg.consts.Cons;
import com.example.newstg.obj.Article;
import com.example.newstg.obj.Word;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FetchUtils {
    public static int total = 0;
    @NonNull
    public static String replaceBR(@NonNull Element element) {
        String[] articleBodyStr = element.html().split("<br>");
        return Jsoup.parse(join("$$$$$", articleBodyStr)).text().replace("$$$$$", "\n");
    }

    public static List<Article> sorting(@NonNull List<Article> artList) {
        return artList.stream().sorted(Comparator.comparingLong(article -> -article.time))
                .collect(Collectors.toList());
    }

    public static List<Article> merging(@NonNull List<Article> articles) {
        List<Article> merged = new ArrayList<>(articles.stream()
                .collect(Collectors.toMap(
                        article -> article.link,
                        Function.identity(),
                        (article1, article2) -> {
                            article1.keywords.addAll(article2.keywords);
                            article1.keywords = new ArrayList<>(new HashSet<>(article1.keywords));
                            return article1;
                        }))
                .values());
        List<Article> sorted = sorting(merged);
        total = sorted.size();
        return sorted;
    }

    public static List<Word> sortingNum(List<Word> words, Context ctx) {
        List<Word> keywords = words.stream()
                .filter(word -> word.getNum() != 0)
                .sorted(Comparator.comparingInt(Word::getNum).reversed())
                .collect(Collectors.toList());
        List<Word> coloredKw = cardColoring(keywords, ctx);
        Word allWord = new Word(ctx.getColor(R.color.green), Cons.ALL, ctx.getColor(R.color.black), total, true);
        coloredKw.add(0, allWord);
        return coloredKw;
    }
    private static int cardBgColor(int quantity, Context ctx, int largest, int smallest) {
        if (smallest == largest) {
            return ctx.getColor(R.color.two);
        }
        float range = (largest - smallest) / 5.0f;
        if (quantity > largest) {
            return ctx.getColor(R.color.green);
        } else if (quantity <= smallest + range) {
            return ctx.getColor(R.color.five);
        } else if (quantity <= smallest + 2 * range) {
            return ctx.getColor(R.color.four);
        } else if (quantity <= smallest + 3 * range) {
            return ctx.getColor(R.color.three);
        } else {
            return ctx.getColor(R.color.two);
        }
    }
    private static List<Word> cardColoring(List<Word> keywords, Context ctx) {
        if (keywords.size() > 1) {
            int largest = keywords.get(0).getNum();
            int smallest = keywords.get(keywords.size()-1).getNum();
            for (Word kw : keywords) {
                int color = cardBgColor(kw.getNum(), ctx, largest, smallest);
                kw.setId(color);
            }
        }
        return keywords;
    }
}
