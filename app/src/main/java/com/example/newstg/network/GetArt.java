package com.example.newstg.network;

import static com.example.newstg.consts.Cons.JS_TEXT;
import static com.example.newstg.consts.Cons.MESSAGE_DIV;
import static com.example.newstg.consts.Cons.MESS_REPLY;
import static com.example.newstg.consts.Cons.TEXT_DIV;

import static java.lang.String.join;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.adap.ArtAd;
import com.example.newstg.adap.SumAd;
import com.example.newstg.consts.Cons;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.ProgressBinding;
import com.example.newstg.obj.Article;
import com.example.newstg.obj.Chn;
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
    NewsVM wordVm;
    TextView unique;

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
            NewsVM wordVm,
            TextView unique
    ) {
        this.ctx = ctx;
        this.window = window;
        this.artRv = artRv;
        this.artAd = artAd;
        this.wordVm = wordVm;
        this.sumRv = sumRv;
        this.sumAd = sumAd;
        this.unique = unique;
        this.owner = owner;


        List<Article> articles = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        wordVm.lists().observe(owner, lists -> {
            List<Word> keywords = lists.first;
            List<Chn> channels = lists.second;
            if (channels != null) {
                progress(channels.size());
                Toast.makeText(ctx, String.valueOf(channels.size()), Toast.LENGTH_SHORT).show();
                Callable<Void> task = () -> {
                    int progress = 0;
                    Document doc;
                    for (Chn chn : channels) {
                        String link = chn.link;
                        final int currentProgress = ++progress;
                        handler.post(() -> {
                            if (dialog != null && bnd != null) {
                                bnd.progressBar.setProgress(currentProgress);
                                String text = String.format("%d/%d", currentProgress, channels.size());
                                bnd.progressText.setText(text);
                            }
                        });
                        Log.i("Link", link);
                        try {
                            doc = Jsoup.connect(link).timeout(20 * 2000).get();

                            if (doc.text().contains("you can contact")) {
                                Toast.makeText(ctx, link, Toast.LENGTH_LONG).show();
                            }

                            Elements messageSections = doc.select("div." + MESSAGE_DIV);
                            String chnTitle = doc.select("meta[property=og:title]").first().attr("content");
                            for (Element section : messageSections) {
                                Elements allTextDivs = section.select("div." + TEXT_DIV + JS_TEXT);
                                boolean hasVideo = !section.select(Cons.VIDEO).isEmpty();
                                for (Element articleBody : allTextDivs) {
                                    if (articleBody.parent() != null && !articleBody.parent().hasClass(MESS_REPLY)) {
                                        for (Word keyword : keywords) {
                                            String word = keyword.getWord();
                                            String artBody = replaceBR(articleBody);
                                            String lower = artBody.toLowerCase();
                                            if (hasVideo) {
                                                artBody = "[VIDEO]\n\n" + artBody;
                                            }
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
                            String errorText =
                                    error.substring(0, 1).toUpperCase() + error.substring(1)
                                            + ". Restart the process.";
                            Log.e("ERROR :", Objects.requireNonNull(e.getMessage()));
                            handler.post(() -> {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            });
                            unique.setVisibility(View.VISIBLE);
                            unique.setText(errorText);
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
                // executorService.shutdown();
            }});
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

