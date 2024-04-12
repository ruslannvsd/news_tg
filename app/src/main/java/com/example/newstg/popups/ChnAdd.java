package com.example.newstg.popups;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.core.widget.PopupWindowCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.newstg.R;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.ChnAddBinding;
import com.example.newstg.obj.Chn;
import com.example.newstg.utils.CloseKB;

public class ChnAdd {
    ChnAddBinding bnd;
    Context ctx;
    PopupWindow window;
    NewsVM newsVM;
    LifecycleOwner owner;

    public void chlAdd(Context ctx, NewsVM newsVM, LifecycleOwner owner) {
        this.ctx = ctx;
        this.newsVM = newsVM;
        this.owner = owner;
        View popupView = LayoutInflater.from(ctx).inflate(R.layout.chn_add, new LinearLayout(ctx), false);
        bnd = ChnAddBinding.bind(popupView);
        setupPopupWindow(popupView);
    }

    private void setupPopupWindow(View popupView) {
        window = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        window.setElevation(2f);
        window.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        PopupWindowCompat.setWindowLayoutType(window, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        Toast.makeText(ctx, "Enter your channel as copied.", Toast.LENGTH_SHORT).show();
        bnd.chn.setOnKeyListener(this::linkAdding);
    }

    private boolean linkAdding(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            String inputText = bnd.chn.getText().toString().trim();
            if (!inputText.isEmpty()) {
                String[] items = inputText.split(" ");
                for (String i : items) {
                    String item = modifyLink(i);
                    if (item.contains("https://t.me/s/")) {
                        newsVM.linkExist(item).observe(owner, new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean exists) {
                                if (!exists) {
                                    Chn chn = new Chn(0, item);
                                    newsVM.insChn(chn);
                                    Toast.makeText(ctx, "Channel " + item + " is added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ctx, "Channel " + item + " already added.", Toast.LENGTH_SHORT).show();
                                }
                                newsVM.linkExist(item).removeObserver(this);
                            }
                        });
                    }
                }
                Toast.makeText(ctx, "Channels are added", Toast.LENGTH_SHORT).show();
                bnd.chn.setText("");
            } else {
                Toast.makeText(ctx, "Enter Keyword", Toast.LENGTH_SHORT).show();
            }
            new CloseKB().closeKeyboard(v);
            return true;
        }
        return false;
    }


    public static String modifyLink(String link) {
        if (link.contains("https://t.me/") && !link.contains("https://t.me/s/")) {
            return link.replaceFirst("https://t.me/", "https://t.me/s/");
        }
        return link;
    }
}
