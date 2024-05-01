package com.example.newstg.utils;

import android.content.Context;
import androidx.core.content.ContextCompat;

import com.example.newstg.R;
import com.example.newstg.obj.Chn;
import com.example.newstg.obj.Word;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.Collator;

public class WordSorting {
    private static Map<Integer, Integer> colorMap(Context context) {
        Map<Integer, Integer> colorOrder = new HashMap<>();
        colorOrder.put(ContextCompat.getColor(context, R.color.sunset_glow), 1);
        colorOrder.put(ContextCompat.getColor(context, R.color.harvest_moon), 2);
        colorOrder.put(ContextCompat.getColor(context, R.color.morning_sun), 3);
        colorOrder.put(ContextCompat.getColor(context, R.color.spring_dew), 4);
        colorOrder.put(ContextCompat.getColor(context, R.color.forest_mist), 5);
        colorOrder.put(ContextCompat.getColor(context, R.color.gentle_tide), 6);
        colorOrder.put(ContextCompat.getColor(context, R.color.clear_sky), 7);
        colorOrder.put(ContextCompat.getColor(context, R.color.winter_morning), 8);
        colorOrder.put(ContextCompat.getColor(context, R.color.twilight_blue), 9);
        colorOrder.put(ContextCompat.getColor(context, R.color.dusk_lavender), 10);
        colorOrder.put(ContextCompat.getColor(context, R.color.aurora_pink), 11);
        colorOrder.put(ContextCompat.getColor(context, R.color.cherry_blossom), 12);
        return colorOrder;
    }

    private static Comparator<Word> getWordComparator(Map<Integer, Integer> colorOrder) {
        return Comparator
                .comparing((Word word) -> colorOrder.getOrDefault(word.getColor(), Integer.MAX_VALUE))
                .thenComparing(Word::getWord, Comparator.nullsFirst(
                        Collator.getInstance(new Locale("ru", "RU")).thenComparing(
                                Collator.getInstance(Locale.ENGLISH)
                        )
                ));
    }

    public static void sortWords(List<Word> words, Context context) {
        Map<Integer, Integer> colorOrder = colorMap(context);
        Comparator<Word> comparator = getWordComparator(colorOrder);
        words.sort(comparator);
    }

    private static Map<Integer, Integer> catOrderMap(Context ctx) {
        Map<Integer, Integer> categoryOrder = new HashMap<>();
        categoryOrder.put(ContextCompat.getColor(ctx, R.color.chn_1), 1);
        categoryOrder.put(ContextCompat.getColor(ctx, R.color.chn_2), 2);
        categoryOrder.put(ContextCompat.getColor(ctx, R.color.chn_3), 3);
        categoryOrder.put(ContextCompat.getColor(ctx, R.color.chn_4), 4);
        categoryOrder.put(ContextCompat.getColor(ctx, R.color.chn_5), 5);
        return categoryOrder;
    }

    private static Comparator<Chn> getChnComparator(Map<Integer, Integer> categoryOrder) {
        return Comparator
                .comparing((Chn chn) -> categoryOrder.getOrDefault(chn.category, Integer.MAX_VALUE))
                .thenComparing(chn -> chn.name, Comparator.nullsFirst(
                        Collator.getInstance(new Locale("uk", "UA")).thenComparing(
                                Collator.getInstance(Locale.ENGLISH)
                        )
                ));
    }

    public static void sortChannels(List<Chn> channels, Context ctx) {
        Map<Integer, Integer> categoryOrder = catOrderMap(ctx);
        Comparator<Chn> comparator = getChnComparator(categoryOrder);
        channels.sort(comparator);
    }
}
