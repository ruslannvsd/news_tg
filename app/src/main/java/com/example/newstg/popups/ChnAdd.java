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
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.adap.ChnAd;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.ChnAddBinding;
import com.example.newstg.obj.Chn;
import com.example.newstg.utils.CloseKB;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.HashSet;
import java.util.List;

public class ChnAdd {
    ChnAddBinding bnd;
    Context ctx;
    PopupWindow window;
    NewsVM newsVM;
    LifecycleOwner owner;
    ChnAd chnAd;

    public void chlAdd(Context ctx, NewsVM newsVM, LifecycleOwner owner, ChnAd chnAd) {
        this.ctx = ctx;
        this.newsVM = newsVM;
        this.owner = owner;
        this.chnAd = chnAd;
        View popupView = LayoutInflater.from(ctx).inflate(R.layout.chn_add, new LinearLayout(ctx), false);
        bnd = ChnAddBinding.bind(popupView);
        setupPopupWindow(popupView);
        newsVM.getChannels().observe(owner, channels -> {
            setRv(bnd.channels, chnAd, channels, ctx, newsVM);
        });
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
            if (inputText.isEmpty()) {
                Toast.makeText(ctx, "Enter Keyword", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                new CloseKB().closeKeyboard(v);
                Observer<List<Chn>> observer = new Observer<List<Chn>>() {
                    @Override
                    public void onChanged(List<Chn> channels) {
                        HashSet<String> existingSet = new HashSet<>();
                        for (Chn chn : channels) {
                            existingSet.add(chn.link);
                        }
                        int added = 0;
                        int existed = 0;
                        int corrupted = 0;
                        for (String input : inputText.split("\\s+")) {
                            String chn = modifyLink(input.trim());
                            if (chn == null) {
                                corrupted += 1;
                                break;
                            }
                            if (!existingSet.contains(chn)) {
                                existingSet.add(chn);
                                int lastIndex = chn.lastIndexOf('/');
                                String name = chn.substring(lastIndex + 1);
                                Chn newChn = new Chn(0, chn, name, ctx.getColor(R.color.fog));
                                newsVM.insChn(newChn);
                                added += 1;
                            } else {
                                existed += 1;
                            }
                            bnd.chn.setText("");
                            newsVM.getChannels().removeObserver(this);
                        }
                        if (added > 0) {
                            Toast.makeText(ctx, "ADDED: " + added + " channel(s)", Toast.LENGTH_SHORT).show();
                        }
                        if (existed > 0) {
                            Toast.makeText(ctx, "EXIST: " + existed + " channel(s)", Toast.LENGTH_SHORT).show();
                        }
                        if (corrupted > 0) {
                            Toast.makeText(ctx, "CORRUPTED: " + corrupted + " link(s)", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                newsVM.getChannels().observe(owner, observer);
            }
            return true;
        }
        return false;
    }

    public static String modifyLink(String link) {
        if (link.contains("https://t.me/") && !link.contains("https://t.me/s/")) {
            return link.replaceFirst("https://t.me/", "https://t.me/s/");
        } else if (link.contains("https://t.me/s/")) {
            return link;
        }
        return null;
    }

    public static void setRv(RecyclerView rv, ChnAd chnAd, List<Chn> channels, Context ctx, NewsVM newsVM) {
        chnAd.setChannels(channels, ctx, newsVM);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(ctx);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        rv.setLayoutManager(flexboxLayoutManager);
        chnAd.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        rv.setAdapter(chnAd);
    }
}
