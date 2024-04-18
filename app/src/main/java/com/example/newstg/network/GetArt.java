package com.example.newstg.network;

import static com.example.newstg.consts.Cons.ART_META;
import static com.example.newstg.consts.Cons.DATETIME;
import static com.example.newstg.consts.Cons.D_TIME;
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
import androidx.lifecycle.Observer;
import androidx.core.util.Pair;
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
import com.example.newstg.utils.TimeFuncs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
    Handler handler;

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

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        Observer<Pair<List<Word>, List<Chn>>> observer = new Observer<Pair<List<Word>, List<Chn>>>() {
            @Override
            public void onChanged(Pair<List<Word>, List<Chn>> lists) {
                List<Word> keywords = lists.first;
                List<Chn> channels = lists.second;
                if (channels != null) {
                    progress(channels.size());
                    Toast.makeText(ctx, String.valueOf(channels.size()), Toast.LENGTH_SHORT).show();
                    Callable<Void> task = () -> {
                        List<Article> articles = new ArrayList<>();
                        int progress = 0;
                        for (Chn chn : channels) {
                            String link = chn.link;
                            final int currentProgress = ++progress;
                            handler.post(() -> {
                                String[] parts = link.split("/");
                                String title = parts[parts.length - 1];
                                if (dialog != null && bnd != null) {
                                    bnd.progressBar.setProgress(currentProgress);
                                    String text = String.format(Locale.UK, "%d/%d\n%s", currentProgress, channels.size(), title);
                                    bnd.progressText.setText(text);
                                }
                            });
                            articles.addAll(gettingArticles(link, hours, keywords));
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
                                wordVm.setWords(sortingNum(new Count().results(keywords, finalList, ctx)));
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
                    wordVm.lists().removeObserver(this);
                }
            }
        };
        wordVm.lists().observe(owner, observer);
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
        bnd.progressText.setText(String.format(Locale.UK, "0/%d", size));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private Document fetchDoc(String url) throws IOException {
        return Jsoup.connect(url).timeout(20 * 1000).get(); // 20 seconds timeout
    }

    private List<Article> gettingArticles(String link, int hours, List<Word> keywords) {
        Document doc;
        List<Article> articles = new ArrayList<>();
        try {
            doc = fetchDoc(link);

            if (doc.text().contains("you can contact")) {
                Toast.makeText(ctx, link, Toast.LENGTH_LONG).show();
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
        return articles;
    }
}
