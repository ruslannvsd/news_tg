package com.example.newstg.popups;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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
    Word wd;
    EditText wordEt;
    public void colorPopup(Context ctx, NewsVM newsVM, int p, InputAd inputAd, Word wd) {
        this.ctx = ctx;
        this.newsVM = newsVM;
        this.p = p;
        this.inputAd = inputAd;
        this.wd = wd;
        View popupView = LayoutInflater.from(ctx).inflate(R.layout.color_menu, new LinearLayout(ctx), false);
        bnd = ColorMenuBinding.bind(popupView);
        setupPopupWindow(popupView);
        wordEt = bnd.word;
        wordEt.setText(wd.getWord());
        wordEt.setTextColor(wd.getColor());
        RecyclerView rv = bnd.colorsRv;
        ColorUpdAd ad = new ColorUpdAd();
        ad.getColors(ctx, newsVM, inputAd, p, window, wd, wordEt);
        rv.setLayoutManager(new GridLayoutManager(ctx, 3));
        rv.setAdapter(ad);
        wordEt.setOnKeyListener(this::titleClick);
    }
    private void setupPopupWindow(View popupView) {
        window = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        window.setElevation(2f);
        window.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        PopupWindowCompat.setWindowLayoutType(window, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
    }
    private boolean titleClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            String title = wordEt.getText().toString().trim();
            Word newWd = new Word(wd.getId(), title, wd.getColor(), wd.getNum(), wd.getStatus());
            newsVM.updWd(newWd);
            inputAd.notifyItemChanged(p);
            window.dismiss();
        }
        return true;
    }
}
