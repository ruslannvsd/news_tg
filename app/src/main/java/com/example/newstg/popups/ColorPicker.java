package com.example.newstg.popups;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.core.widget.PopupWindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.adap.ColorPickerAd;
import com.example.newstg.adap.ColorUpdAd;
import com.example.newstg.adap.InputAd;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.ColorMenuBinding;
import com.example.newstg.obj.Word;
import com.example.newstg.utils.ColorPickerCallback;

public class ColorPicker {
    Context ctx;
    ColorMenuBinding bnd;
    PopupWindow window;
    ColorPickerCallback callback;
    public void colorPopup(Context ctx, Button btn, ColorPickerCallback callback) {
        this.ctx = ctx;
        this.callback = callback;
        View popupView = LayoutInflater.from(ctx).inflate(R.layout.color_menu, new LinearLayout(ctx), false);
        bnd = ColorMenuBinding.bind(popupView);
        setupPopupWindow(popupView);

        RecyclerView rv = bnd.colorsRv;
        ColorPickerAd ad = new ColorPickerAd(callback);
        ad.getColors(ctx, window, btn);
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
