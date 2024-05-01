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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newstg.R;
import com.example.newstg.databinding.ArLayBinding;
import com.example.newstg.obj.Article;
import com.example.newstg.utils.TimeFuncs;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArtAd extends RecyclerView.Adapter<ArtAd.ArticleViewHolder>{
    List<Article> articles = emptyList();
    Context ctx;
    String chosen;
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
        h.bnd.card.setBackgroundColor(color);
        h.bnd.channel.setBackgroundColor(art.color);
        long currentTimeMillis = System.currentTimeMillis();
        long differenceMillis = currentTimeMillis - art.time;
        long hours = TimeUnit.MILLISECONDS.toHours(differenceMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(differenceMillis) % 60;
        String timeDifference = String.format(Locale.getDefault(), "%dh %dm ago / ", hours, minutes);
        String time = TimeFuncs.convertToReadableTime(art.time);
        linkify(art.chnTitle, art.link, h.bnd.channel);
        h.bnd.artTime.setText(timeDifference + time);
        h.bnd.keyword.setText(String.valueOf(art.keywords));
        if (!Objects.equals(art.image, "IMG")) {
            Glide.with(ctx).load(R.drawable.line).placeholder(R.drawable.load).into(h.bnd.divider);
            Glide.with(ctx).load(art.image).dontTransform().placeholder(R.drawable.load).into(h.bnd.img);
        } else {
            Glide.with(ctx).load(R.drawable.line).placeholder(R.drawable.load).into(h.bnd.divider);
            h.bnd.img.setVisibility(View.GONE);
        }
        SpannableStringBuilder textWithBold = boldWords(art.body, art.keywords, chosen);
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
    public void setArticles(List<Article> articles, Context ctx, String keyword) {
        this.articles = articles;
        this.ctx = ctx;
        this.chosen = keyword;
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
    private static SpannableStringBuilder boldWords(String fullText, @NonNull List<String> wordsToBold, String chosen) {
        SpannableStringBuilder sb = new SpannableStringBuilder(fullText);
        String lowerFullText = fullText.toLowerCase(Locale.ROOT);
        for (String item : wordsToBold) {
            if (item.contains("_")) {
                String[] splitWords = item.split("_");
                for (String wordToBold : splitWords) {
                    colorizing(sb, lowerFullText, wordToBold, chosen);
                }
            } else {
                colorizing(sb, lowerFullText, item, chosen);
            }
        }
        return sb;
    }

    private static void colorizing(SpannableStringBuilder sb, String fullText, String wordToYellow, String wordToGreen) {
        if (wordToYellow != null) {
            Pattern patternYellow = Pattern.compile(wordToYellow, Pattern.CASE_INSENSITIVE);
            Matcher matcherYellow = patternYellow.matcher(fullText);
            while (matcherYellow.find()) {
                sb.setSpan(new StyleSpan(Typeface.BOLD), matcherYellow.start(), matcherYellow.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sb.setSpan(new ForegroundColorSpan(Color.YELLOW), matcherYellow.start(), matcherYellow.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        // Handle green highlighting
        if (wordToGreen != null) {
            String[] parts = wordToGreen.split("_");
            for (String part : parts) {
                Pattern patternGreen = Pattern.compile(part, Pattern.CASE_INSENSITIVE);
                Matcher matcherGreen = patternGreen.matcher(fullText);
                while (matcherGreen.find()) {
                    sb.setSpan(new StyleSpan(Typeface.BOLD), matcherGreen.start(), matcherGreen.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sb.setSpan(new ForegroundColorSpan(Color.GREEN), matcherGreen.start(), matcherGreen.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private void linkify(String title, String url, TextView tv) {
        String formattedText = "<a href='" + url + "'>" + title + "</a>";
        tv.setText(Html.fromHtml(formattedText, Html.FROM_HTML_MODE_COMPACT));
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
