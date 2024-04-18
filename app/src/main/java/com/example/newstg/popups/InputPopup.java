package com.example.newstg.popups;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.PopupWindowCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.adap.InputAd;
import com.example.newstg.adap.ArtAd;
import com.example.newstg.adap.SumAd;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.InputPopupBinding;
import com.example.newstg.network.GetArt;
import com.example.newstg.obj.Word;
import com.example.newstg.utils.CloseKB;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class InputPopup {
    InputPopupBinding bnd;
    Context ctx;
    PopupWindow window;
    InputAd wordsAd;
    NewsVM newsVM;
    LifecycleOwner owner;
    RecyclerView artRv;
    ArtAd artAd;
    RecyclerView sumRv;
    SumAd sumAd;
    int color;

    public void inputPopup(@NonNull Context ctx,
                           NewsVM newsVM,
                           LifecycleOwner owner,
                           RecyclerView artRv,
                           ArtAd artAd,
                           RecyclerView sumRv,
                           SumAd sumAd,
                           TextView unique
    ) {
        this.ctx = ctx;
        this.newsVM = newsVM;
        this.owner = owner;
        this.artRv = artRv;
        this.artAd = artAd;
        this.sumRv = sumRv;
        this.sumAd = sumAd;

        View popupView = LayoutInflater.from(ctx).inflate(R.layout.input_popup, new LinearLayout(ctx), false);
        bnd = InputPopupBinding.bind(popupView);
        wordsAd = new InputAd();
        setupPopupWindow(popupView);
        wordsToRv();
        color = ctx.getColor(R.color.cloud);

        bnd.enterWords.setOnKeyListener(this::wordsClick);
        bnd.period.setText("12");
        bnd.color.setBackgroundColor(color);
        bnd.color.setOnClickListener(this::showPopupMenu);
        bnd.start.setOnClickListener(v -> {
            int hours;
            if (bnd.period.getText().toString().isEmpty()) {
                hours = 100;
            } else {
                hours = Integer.parseInt(bnd.period.getText().toString());
                if (hours > 100 || hours < 1) {
                    hours = 100;
                }
            }
            new CloseKB().closeKeyboard(v);
            window.dismiss();
            Log.d("InputPopup", "Start button clicked");
            Toast.makeText(ctx, hours + " hour(s). Search started ...", Toast.LENGTH_SHORT).show();
            new GetArt().getArt(
                    ctx,
                    owner,
                    hours,
                    window,
                    artRv,
                    artAd,
                    sumRv,
                    sumAd,
                    newsVM,
                    unique
            );
            bnd.start.clearFocus();
        });
    }

    private void setupPopupWindow(View popupView) {
        window = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        window.setElevation(2f);
        window.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        PopupWindowCompat.setWindowLayoutType(window, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
    }

    private void wordsToRv() {
        newsVM.getAll().observe(owner, words -> {
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(ctx);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            bnd.words.setLayoutManager(layoutManager);
            wordsAd.setWords(
                    words,
                    ctx, newsVM);
            bnd.words.setAdapter(wordsAd);
        });
    }

    private boolean wordsClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            String inputText = bnd.enterWords.getText().toString().trim();
            if (inputText.isEmpty()) {
                Toast.makeText(ctx, "Enter Keyword", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                new CloseKB().closeKeyboard(v);

                Observer<List<Word>> observer = new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> keywords) {
                        HashSet<String> existingSet = new HashSet<>();
                        for (Word kw : keywords) {
                            existingSet.add(kw.getWord());
                        }
                        StringBuilder added = new StringBuilder();
                        StringBuilder existed = new StringBuilder();
                        for (String input : inputText.split("\\s+")) {
                            if (!existingSet.contains(input)) {
                                existingSet.add(input);
                                Word newWord = new Word(0, input, color, 0, true);
                                newsVM.insWd(newWord);
                                added.append(" ").append(input);
                            } else {
                                existed.append(" ").append(input);
                            }
                        }
                        if (added.length() > 0) {
                            Toast.makeText(ctx, "ADDED: " + added.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                        if (existed.length() > 0) {
                            Toast.makeText(ctx, "EXIST: " + existed.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                        bnd.enterWords.setText("");
                        wordsToRv();
                        newsVM.getAll().removeObserver(this);
                    }
                };
                newsVM.getAll().observe(owner, observer);
            }
            return true;
        }
        return false;
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(ctx, view);
        popupMenu.inflate(R.menu.color_menu);
        Map<Integer, Integer> colorMap = new HashMap<>();
        colorMap.put(R.id.sky, R.color.sky);
        colorMap.put(R.id.leaf, R.color.leaf);
        colorMap.put(R.id.sun, R.color.sun);
        colorMap.put(R.id.fox, R.color.fox);
        colorMap.put(R.id.evening, R.color.evening);
        colorMap.put(R.id.flower, R.color.flower);
        colorMap.put(R.id.water, R.color.water);
        colorMap.put(R.id.cloud, R.color.cloud);
        popupMenu.setOnMenuItemClickListener(item -> {
            Integer colorResource = colorMap.get(item.getItemId());
            if (colorResource != null) {
                color = ctx.getColor(colorResource);
                bnd.color.setBackgroundColor(color);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }
}
