package com.example.newstg.utils;

import androidx.room.TypeConverter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Converters {
    @TypeConverter
    public static String fromList(List<String> keywords) {
        return keywords == null ? null : String.join(",", keywords);
    }

    @TypeConverter
    public static List<String> toList(String keywords) {
        return keywords == null ? null : Arrays.asList(keywords.split(","));
    }
}
