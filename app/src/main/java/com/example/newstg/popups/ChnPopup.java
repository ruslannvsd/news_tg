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
import com.example.newstg.adap.ChnAd;
import com.example.newstg.adap.ChnColorUpdAd;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.ChnUpdPopupBinding;
import com.example.newstg.databinding.ColorMenuBinding;
import com.example.newstg.obj.Chn;

public class ChnPopup {
    ChnUpdPopupBinding bnd;
    PopupWindow window;
    EditText chnNameEt;
    EditText chnLinkEt;
    Chn chn;
    NewsVM newsVM;
    ChnAd chnAd;
    ChnColorUpdAd chnColorAd;
    int p;
    int color;
    public void colorPopup(Context ctx, Chn chn, NewsVM newsVM, int p, ChnAd chnAd) {
        this.chn = chn;
        this.newsVM = newsVM;
        this.p = p;
        this.chnAd = chnAd;
        this.color = chn.category;
        View popupView = LayoutInflater.from(ctx).inflate(R.layout.chn_upd_popup, new LinearLayout(ctx), false);
        bnd = ChnUpdPopupBinding.bind(popupView);
        setupPopupWindow(popupView);
        chnNameEt = bnd.chnName;
        chnLinkEt = bnd.chnLink;
        RecyclerView rv = bnd.colorsRv;
        chnNameEt.setText(chn.name);
        chnLinkEt.setText(chn.link);
        chnNameEt.setBackgroundColor(chn.category);
        chnNameEt.setOnKeyListener(this::titleClick);
        chnColorAd = new ChnColorUpdAd(color -> {
            this.color = color;
            chnNameEt.setBackgroundColor(color);
        });
        bnd.update.setOnClickListener(v -> {
            Chn updChn = new Chn(
                    chn.id,
                    chnLinkEt.getText().toString(),
                    chnNameEt.getText().toString(),
                    color
            );
            newsVM.updChn(updChn);
            chnAd.notifyItemChanged(p);
            window.dismiss();
        });
        setRv(rv, chnColorAd, ctx);
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
            String title = chnNameEt.getText().toString().trim();
            Chn newChn = new Chn(chn.id, chn.link, title, chn.category);
            newsVM.updChn(newChn);
            chnAd.notifyItemChanged(p);
            window.dismiss();
        }
        return true;
    }
    public static void setRv(RecyclerView rv, ChnColorUpdAd chnColorAd, Context ctx) {
        chnColorAd.setColors(ctx);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ctx, 5);
        rv.setLayoutManager(gridLayoutManager);
        rv.setAdapter(chnColorAd);
    }
}
