package com.example.newstg.utils;

import org.jsoup.nodes.Element;

public class WordFuncs {
    public static String getLink(Element section) {
        Element photoElement = section.select("a.tgme_widget_message_photo_wrap").first();
        if (photoElement != null) {
            String styleValue = photoElement.attr("style");
            return styleValue.substring(styleValue.indexOf("url('") + 5, styleValue.lastIndexOf("')"));
        } else {
            return "IMG";
        }
    }
}


