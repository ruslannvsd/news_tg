package com.example.newstg.network;

import static java.lang.String.join;

import androidx.annotation.NonNull;

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
        return sorting(merged);
    }

    public static List<Word> sortingNum(List<Word> words) {
        return words.stream()
                .filter(word -> word.getNum() != 0)
                .sorted(Comparator.comparingInt(Word::getNum).reversed())
                .collect(Collectors.toList());
    }
}
