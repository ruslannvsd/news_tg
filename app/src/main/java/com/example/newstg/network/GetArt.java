package com.example.newstg.network;

import static com.example.newstg.consts.Cons.JS_TEXT;
import static com.example.newstg.consts.Cons.MESSAGE_DIV;
import static com.example.newstg.consts.Cons.TEXT_DIV;

import static java.lang.String.join;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.adap.ArtAd;
import com.example.newstg.adap.SumAd;
import com.example.newstg.consts.Cons;
import com.example.newstg.data.WordVM;
import com.example.newstg.databinding.ProgressBinding;
import com.example.newstg.obj.Article;
import com.example.newstg.obj.Word;
import com.example.newstg.utils.ArticleMaking;
import com.example.newstg.utils.Count;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetArt {
    Context ctx;
    PopupWindow window;
    LifecycleOwner owner;
    RecyclerView artRv;
    ArtAd artAd;
    RecyclerView sumRv;
    SumAd sumAd;
    WordVM wordVm;

    ProgressBinding bnd;
    AlertDialog dialog;

    public void getArt(
            Context ctx,
            LifecycleOwner owner,
            int hours,
            PopupWindow window,
            RecyclerView artRv,
            ArtAd artAd,
            RecyclerView sumRv,
            SumAd sumAd,
            WordVM wordVm
    ) {
        this.ctx = ctx;
        this.window = window;
        this.artRv = artRv;
        this.artAd = artAd;
        this.wordVm = wordVm;
        this.sumRv = sumRv;
        this.sumAd = sumAd;

        List<String> links = Cons.CHANNELS;

        List<Article> articles = new ArrayList<>();
        progress(links.size());
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        wordVm.getAll().observe(owner, keywords -> {
            Callable<Void> task = () -> {
                Handler handler = new Handler(Looper.getMainLooper());
                int progress = 0; // progress counter
                Document doc;
                for (String link : links) {
                    final int currentProgress = ++progress;
                    handler.post(() -> {
                        if (dialog != null && bnd != null) {
                            bnd.progressBar.setProgress(currentProgress);
                            bnd.progressText.setText(String.format("%d/%d", currentProgress, links.size()));
                        }
                    });
                    Log.i("Link", link);
                    try {
                        doc = Jsoup.connect(link).timeout(20 * 1000).get();
                        Elements messageSections = doc.select("div." + MESSAGE_DIV);
                        String chnTitle = doc.select("meta[property=og:title]").first().attr("content");
                        for (Element section : messageSections) {
                            Elements allTextDivs = section.select("div." + TEXT_DIV + JS_TEXT);
                            for (Element articleBody : allTextDivs) {
                                if (articleBody.parent() != null && !articleBody.parent().hasClass("tgme_widget_message_reply")) {
                                    for (Word keyword : keywords) {
                                        String word = keyword.getWord();
                                        String artBody = replaceBR(articleBody);
                                        String lower = artBody.toLowerCase();
                                        if (!word.contains("_")) {
                                            if (lower.contains(word.toLowerCase())) {
                                                Article art = new ArticleMaking().makeArticle(chnTitle, section, artBody, word, hours);
                                                if (art != null) {
                                                    articles.add(art);
                                                }
                                            }
                                        } else {
                                            String[] splitWord = word.split("_");
                                            if (lower.contains(splitWord[0].toLowerCase()) && lower.contains(splitWord[1].toLowerCase())) {
                                                Article art = new ArticleMaking().makeArticle(chnTitle, section, artBody, word, hours);
                                                if (art != null) {
                                                    articles.add(art);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    } catch (IOException e) {
                        String error = Objects.requireNonNull(e.getMessage());
                        Log.e("ERROR :", Objects.requireNonNull(e.getMessage()));
                        handler.post(() -> {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        });
                        Toast.makeText(ctx, error, Toast.LENGTH_LONG).show();
                        throw new RuntimeException(e);
                    }

                }
                if (articles.isEmpty()) {
                    handler.post(() -> {
                        Toast.makeText(ctx, "Nothing has been found", Toast.LENGTH_LONG).show();
                    });
                } else {
                    List<Article> finalList;
                    if (keywords.size() == 1) {
                        finalList = sorting(articles);
                    } else {
                        finalList = merging(articles);
                    }
                    handler.post(() -> {
                        wordVm.setArticles(finalList);
                        wordVm.setWords(sortingNum(new Count().results(keywords, finalList, ctx, wordVm)));
                        dialog.dismiss();
                    });
                }
                handler.post(() -> {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                });
                Toast.makeText(ctx, "Search completed.", Toast.LENGTH_SHORT).show();
                return null;
            };
            executorService.submit(task);
            window.dismiss();
            executorService.shutdown();
        });
    }
    @NonNull
    public static String replaceBR(@NonNull Element element) {
        String[] articleBodyStr = element.html().split("<br>");
        return Jsoup.parse(join("$$$$$", articleBodyStr)).text().replace("$$$$$", "\n");
    }


    public List<Article> sorting(@NonNull List<Article> artList) {
        return artList.stream().sorted(Comparator.comparingLong(article -> -article.time))
                .collect(Collectors.toList());
    }

    public List<Article> merging(@NonNull List<Article> articles) {
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

    public List<Word> sortingNum(List<Word> words) {
        return words.stream()
                .filter(word -> word.getNum() != 0)
                .sorted(Comparator.comparingInt(Word::getNum).reversed())
                .collect(Collectors.toList());
    }

    public void progress(int size) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        bnd = ProgressBinding.inflate(LayoutInflater.from(ctx));
        builder.setView(bnd.getRoot());
        dialog = builder.create();
        bnd.progressBar.setMax(size);
        bnd.progressText.setText(String.format("0/%d", size));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}

