package com.example.newstg.popups;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.example.newstg.data.WordVM;
import com.example.newstg.databinding.InputPopupBinding;
import com.example.newstg.network.GetArt;
import com.example.newstg.obj.Word;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

public class InputPopup {
    InputPopupBinding bnd;
    Context ctx;
    PopupWindow window;
    InputAd wordsAd;
    WordVM wordVm;
    LifecycleOwner owner;
    RecyclerView artRv;
    ArtAd artAd;
    RecyclerView sumRv;
    SumAd sumAd;

    public void inputPopup(@NonNull Context ctx,
                           WordVM wordVM,
                           LifecycleOwner owner,
                           RecyclerView artRv,
                           ArtAd artAd,
                           RecyclerView sumRv,
                           SumAd sumAd
    ) {
        this.ctx = ctx;
        this.wordVm = wordVM;
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

        bnd.enterWords.setOnKeyListener(this::wordsClick);
        bnd.period.setText("24");
        bnd.start.setOnClickListener(v -> {
            int hours;
            if (bnd.period.getText().toString().isEmpty()) {
                hours = 100;
            } else {
                hours = Integer.parseInt(bnd.period.getText().toString());
            }
            closeKeyboard(v);
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
                    wordVM
            );
        });
    }

    private void setupPopupWindow(View popupView) {
        window = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        window.setElevation(2f);
        window.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        PopupWindowCompat.setWindowLayoutType(window, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        Toast.makeText(ctx, "Enter 0 hours to cancel", Toast.LENGTH_SHORT).show();
    }

    private void wordsToRv() {
        wordVm.getAll().observe(owner, words -> {
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(ctx);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            bnd.words.setLayoutManager(layoutManager);
            wordsAd.setWords(
                    words,
                    ctx, wordVm);
            bnd.words.setAdapter(wordsAd);
        });
    }

    private boolean wordsClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            final Observer<Boolean> existObserver = new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean exists) {
                    String input = bnd.enterWords.getText().toString().trim();
                    if (!input.isEmpty()) {
                        if (!exists) {
                            Word word = new Word(0, input, ctx.getColor(R.color.c_gray), 0);
                            wordVm.insWd(word);
                            Toast.makeText(ctx, input + " is added", Toast.LENGTH_SHORT).show();
                            bnd.enterWords.setText("");
                            wordsToRv();
                        } else {
                            Toast.makeText(ctx, "Keyword's already added.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ctx, "Enter Keyword", Toast.LENGTH_SHORT).show();
                    }
                    wordVm.exist(input).removeObserver(this);
                }
            };
            String inputText = bnd.enterWords.getText().toString().trim();
            if (!inputText.isEmpty()) {
                wordVm.exist(inputText).observe(owner, existObserver);
            } else {
                Toast.makeText(ctx, "Enter Keyword", Toast.LENGTH_SHORT).show();
            }
            closeKeyboard(v);
            return true;
        }
        return true;
    }
    private void closeKeyboard(@NonNull View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }
}
