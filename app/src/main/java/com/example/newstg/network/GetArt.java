package com.example.newstg.network;

import static com.example.newstg.consts.Cons.ART_META;
import static com.example.newstg.consts.Cons.CONTENT;
import static com.example.newstg.consts.Cons.DATETIME;
import static com.example.newstg.consts.Cons.DOC;
import static com.example.newstg.consts.Cons.D_TIME;
import static com.example.newstg.consts.Cons.JS_TEXT;
import static com.example.newstg.consts.Cons.MESSAGE_DIV;
import static com.example.newstg.consts.Cons.MESS_REPLY;
import static com.example.newstg.consts.Cons.TEXT_DIV;
import static com.example.newstg.network.FetchUtils.merging;
import static com.example.newstg.network.FetchUtils.replaceBR;
import static com.example.newstg.network.FetchUtils.sorting;
import static com.example.newstg.network.FetchUtils.sortingNum;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.adap.ArtAd;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetArt {
    Context ctx;
    PopupWindow window;
    LifecycleOwner owner;
    RecyclerView artRv;
    ArtAd artAd;
    RecyclerView sumRv;
    NewsVM newsVm;
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
            NewsVM newsVm,
            TextView unique,
            @Nullable String[] words
    ) {
        this.ctx = ctx;
        this.window = window;
        this.artRv = artRv;
        this.artAd = artAd;
        this.newsVm = newsVm;
        this.sumRv = sumRv;
        this.unique = unique;
        this.owner = owner;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        Observer<Pair<List<Word>, List<Chn>>> observer = new Observer<Pair<List<Word>, List<Chn>>>() {
            @Override
            public void onChanged(Pair<List<Word>, List<Chn>> lists) {
                try {
                    List<Word> keywords = lists.first;
                    List<Chn> channels = lists.second;
                    List<Word> newKws = new ArrayList<>();
                    if (words != null) {
                        for (String wd : words) {
                            boolean matchFound = false;
                            for (Word kw : keywords) {
                                if (kw.getWord().equals(wd)) {
                                    newKws.add(kw);
                                    matchFound = true;
                                    break;
                                }
                            }
                            if (!matchFound) {
                                Word newWd = new Word(0, wd, ContextCompat.getColor(ctx, R.color.white), 0, false);
                                newKws.add(newWd);
                                Log.d("Word", newWd.getWord());
                            }
                        }
                    }

                    if (channels != null) {
                        Collections.shuffle(channels);
                        progress(channels.size());
                        Toast.makeText(ctx, String.valueOf(channels.size()), Toast.LENGTH_SHORT).show();
                        Callable<Void> task = () -> {
                            List<Article> articles = new ArrayList<>();
                            int progress = 0;
                            for (Chn chn : channels) {
                                String link = chn.link;
                                Log.i("Link", link);
                                final int currentProgress = ++progress;
                                handler.post(() -> {
                                    if (dialog != null && bnd != null) {
                                        bnd.progressBar.setProgress(currentProgress);
                                        String text = String.format(Locale.UK, "%d/%d\n%s", currentProgress, channels.size(), chn.name);
                                        bnd.progressText.setText(text);
                                    }
                                });
                                if (words == null) {
                                    articles.addAll(gettingArticles(link, hours, chn.category, keywords));
                                } else {
                                    articles.addAll(gettingArticles(link, hours, chn.category, newKws));
                                }
                            }
                            if (articles.isEmpty()) {
                                handler.post(() -> Toast.makeText(ctx, "Nothing has been found", Toast.LENGTH_LONG).show());
                            } else {
                                List<Article> finalList;
                                if (keywords.size() == 1) {
                                    finalList = sorting(articles);
                                } else {
                                    finalList = merging(articles);
                                }
                                handler.post(() -> {
                                    newsVm.clearArticles();
                                    newsVm.setArticles(finalList);

                                    if (words == null) {
                                        newsVm.setWords(sortingNum(new Count().results(keywords, finalList), ctx));
                                    } else {
                                        for (Word kw : newKws) {
                                            Log.i("New Kw", kw.getWord());
                                        }
                                        newsVm.setWords(sortingNum(new Count().results(newKws, finalList), ctx));
                                    }
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
                        newsVm.lists().removeObserver(this);
                    }
                } catch (Exception e) {
                    newsVm.lists().removeObserver(this);
                    handler.post(() -> {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e("ObserverError", Objects.requireNonNull(e.getMessage()));
                        Toast.makeText(ctx, "Error occurred", Toast.LENGTH_LONG).show();
                    });
                }
            }
        };
        newsVm.lists().observe(owner, observer);
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
        return Jsoup.connect(url).timeout(20 * 2000).get();
    }

    private List<Article> gettingArticles(String link, int hours, int color, List<Word> keywords) {
        Document doc;
        List<Article> articles = new ArrayList<>();
        try {
            doc = fetchDoc(link);
            if (doc.text().contains("you can contact")) {
                Toast.makeText(ctx, "NOT ACTIVE : " + link, Toast.LENGTH_LONG).show();
                return articles;
            }

            Elements messageSections = doc.select("div." + MESSAGE_DIV);
            String chnTitle = doc.select(DOC).first().attr(CONTENT);

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
                                if (hasVideo) {
                                    artBody = "[VIDEO]\n\n" + artBody;
                                }
                                boolean allPartsMatch = false;
                                if (!word.contains("/")) {
                                    allPartsMatch = wordTreating(word, artBody);
                                } else {
                                    String[] slashedParts = word.split("/");
                                    for (String part : slashedParts) {
                                        allPartsMatch = wordTreating(part, artBody);
                                        if (allPartsMatch) {
                                            break;
                                        }
                                    }
                                }
                                if (allPartsMatch) {
                                    Article art = new ArticleMaking().makeArticle(chnTitle, section, color, artBody, word, millis);
                                    if (art != null) {
                                        articles.add(art);
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
            unique.setText(errorText);
            Toast.makeText(ctx, error, Toast.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }
        return articles;
    }
    private boolean wordTreating(String word, String artBody) {
        String[] parts = word.split("_");
        boolean allPartsMatch = true;
        for (String part : parts) {
            String regex = part.replace("*", "[a-zA-Zа-яА-ЯёЁіІїЇєЄґҐ]");
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(artBody);
            if (!matcher.find()) {
                allPartsMatch = false;
                break;
            }
        }
        return allPartsMatch;
    }
}
