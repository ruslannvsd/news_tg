package com.example.newstg.utils;

import android.content.Context;
import androidx.core.content.ContextCompat;

import com.example.newstg.R;
import com.example.newstg.obj.Word;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.Collator;

public class WordSorting {
    public static Map<Integer, Integer> createColorOrderMap(Context context) {
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

    public static Comparator<Word> getWordComparator(Map<Integer, Integer> colorOrder) {
        return Comparator
                .comparing((Word word) -> colorOrder.getOrDefault(word.getColor(), Integer.MAX_VALUE))
                .thenComparing(Word::getWord, Comparator.nullsFirst(
                        Collator.getInstance(new Locale("ru", "RU")).thenComparing(
                                Collator.getInstance(Locale.ENGLISH)
                        )
                ));
    }

    public static void sortWords(List<Word> words, Context context) {
        Map<Integer, Integer> colorOrder = createColorOrderMap(context);
        Comparator<Word> comparator = getWordComparator(colorOrder);
        words.sort(comparator);
    }
}
