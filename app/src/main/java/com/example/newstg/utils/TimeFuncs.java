package com.example.newstg.utils;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class TimeFuncs {
    private static final String TG_TIME = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private static final String ART_TIME = "hh.mma dd/MM/yy";
    public static long convertToMillis(String datetimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TG_TIME);
        LocalDateTime dateTime = LocalDateTime.parse(datetimeStr, formatter);
        return dateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    @NonNull
    public static String convertToReadableTime(long millis) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ART_TIME);
        return dateTime.format(formatter).toUpperCase();
    }
}
