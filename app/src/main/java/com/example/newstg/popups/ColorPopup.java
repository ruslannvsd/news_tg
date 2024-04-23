package com.example.newstg.popups;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.core.widget.PopupWindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.adap.ColorUpdAd;
import com.example.newstg.adap.InputAd;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.ColorMenuBinding;
import com.example.newstg.obj.Word;

public class ColorPopup {
    Context ctx;
    ColorMenuBinding bnd;
    PopupWindow window;
    NewsVM newsVM;
    InputAd inputAd;
    int p;
    public void colorPopup(Context ctx, NewsVM newsVM, int p, InputAd inputAd, Word wd) {
        this.ctx = ctx;
        this.newsVM = newsVM;
        this.p = p;
        this.inputAd = inputAd;
        View popupView = LayoutInflater.from(ctx).inflate(R.layout.color_menu, new LinearLayout(ctx), false);
        bnd = ColorMenuBinding.bind(popupView);
        setupPopupWindow(popupView);
        bnd.word.setText(wd.getWord());
        bnd.word.setTextColor(wd.getColor());
        RecyclerView rv = bnd.colorsRv;
        ColorUpdAd ad = new ColorUpdAd();
        ad.getColors(ctx, newsVM, inputAd, p, window, wd);
        rv.setLayoutManager(new GridLayoutManager(ctx, 3));
        rv.setAdapter(ad);
    }
    private void setupPopupWindow(View popupView) {
        window = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        window.setElevation(2f);
        window.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        PopupWindowCompat.setWindowLayoutType(window, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
    }
}
