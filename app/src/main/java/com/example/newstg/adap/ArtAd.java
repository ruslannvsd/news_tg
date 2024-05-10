package com.example.newstg.adap;

import static java.util.Collections.emptyList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.consts.Cons;
import com.example.newstg.databinding.ArLayBinding;
import com.example.newstg.obj.Article;
import com.example.newstg.utils.TimeFuncs;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArtAd extends RecyclerView.Adapter<ArtAd.ArticleViewHolder>{
    List<Article> articles = emptyList();
    Context ctx;
    Set<String> words;
    @NonNull
    public ArticleViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        ArLayBinding bnd = ArLayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArticleViewHolder(bnd);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder h, int p) {
        Article art = articles.get(p);
        int color = cardBgColor(art.time, ctx);
        ImageView img = h.bnd.img;
        h.bnd.card.setBackgroundColor(color);
        h.bnd.channel.setBackgroundColor(art.color);
        long currentTimeMillis = System.currentTimeMillis();
        long differenceMillis = currentTimeMillis - art.time;
        long hours = TimeUnit.MILLISECONDS.toHours(differenceMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(differenceMillis) % 60;
        String timeDifference = String.format(Locale.getDefault(), "%dh %dm ago / ", hours, minutes);
        String time = TimeFuncs.convertToReadableTime(art.time);
        titlesAndLinks(art.chnTitle, art.link, h.bnd.channel, h.bnd.forwarded);

        h.bnd.artTime.setText(timeDifference + time);
        h.bnd.keyword.setText(String.valueOf(art.keywords));
        h.bnd.artTime.setText(timeDifference + time);
        if (!Objects.equals(art.image, "IMG")) {
            img.setVisibility(View.VISIBLE);
            Picasso.get().load(R.drawable.line).placeholder(R.drawable.load).into(h.bnd.divider);
            Picasso.get()
                    .load(art.image)
                    .noFade().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.load).into(img);
        } else {
            img.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.line).placeholder(R.drawable.load).into(h.bnd.divider);
        }
        SpannableStringBuilder textWithBold = wordStyling(art.body, art.keywords, words);
        h.bnd.artBody.setText(textWithBold);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        private final ArLayBinding bnd;
        public ArticleViewHolder(@NonNull ArLayBinding bnd) {
            super(bnd.getRoot());
            this.bnd = bnd;
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setArticles(List<Article> articles, Context ctx, Set<String> words) {
        this.articles = articles;
        this.ctx = ctx;
        this.words = words;
        notifyDataSetChanged();
    }

    public int cardBgColor(long artTime, Context ctx) {
        long now = System.currentTimeMillis();
        long timeDiff = (artTime - now) / (3600000);
        if (timeDiff == 0) {
            return ctx.getColor(R.color.one);
        } else if (timeDiff == -1 || timeDiff == -2) {
            return ctx.getColor(R.color.two);
        } else if (timeDiff <= -3 && timeDiff > -6) {
            return ctx.getColor(R.color.three);
        } else if (timeDiff <= -6 && timeDiff > -11) {
            return ctx.getColor(R.color.four);
        } else if (timeDiff <= -11 && timeDiff > -23) {
            return ctx.getColor(R.color.five);
        } else if (timeDiff <= -23 && timeDiff > -47) {
            return ctx.getColor(R.color.six);
        } else {
            return ctx.getColor(R.color.seven);
        }
    }

    @NonNull
    private static SpannableStringBuilder wordStyling(String fullText, @NonNull List<String> wordsToStyle, Set<String> wordsSpecific)  {
        SpannableStringBuilder sb = new SpannableStringBuilder(fullText);
        for (String item : wordsToStyle) {
            String[] splitWords = item.split("[/_]");
            for (String word : splitWords) {
                coloring(sb, word, fullText, Color.YELLOW, false);
            }
        }
        if (wordsSpecific != null) {
            for (String item : wordsSpecific) {
                String[] parts = item.split("[/_]");
                for (String part : parts) {
                    coloring(sb, part, fullText, Color.GREEN, true);
                }
            }
        }
        return sb;
    }

    public static void coloring(Spannable sb, String textToStyle, String fullText, int color, boolean bold) {
        if (textToStyle != null) {
            Pattern pattern = Pattern.compile(textToStyle, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(fullText);
            while (matcher.find()) {
                if (bold) {
                    sb.setSpan(new StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                sb.setSpan(new ForegroundColorSpan(color), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private void linkify(String title, String url, TextView tv, int color) {
        String formattedText = "<a href='" + url + "'>" + title + "</a>";
        tv.setText(Html.fromHtml(formattedText, Html.FROM_HTML_MODE_COMPACT));
        tv.setLinkTextColor(color);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void titlesAndLinks(String title, String link, TextView main, TextView forward) {
        String[] titles = title.split("\\|");
        String[] links = link.split("\\|");
        linkify(titles[0], links[0], main, Color.YELLOW);
        if (titles.length > 1 && links.length > 1 && !titles[1].isEmpty() && !links[1].isEmpty()) {
            forward.setVisibility(View.VISIBLE);
            maxLength(titles[1], links[1], forward, Cons.LENGTH);
        } else {
            forward.setText(null);
            forward.setVisibility(View.GONE);
        }
    }

    private void maxLength(String title, String link, TextView textView, int length) {
        String text = title.trim();
        if (text.length() > length) {
            text = text.substring(0, length - 3) + "...";
        }
        linkify(text, link, textView, Color.GRAY);
    }
}
