package com.example.newstg.adap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.consts.ColorCons;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.ColorLayBinding;
import com.example.newstg.obj.Color;
import com.example.newstg.obj.Word;

import java.util.ArrayList;
import java.util.List;

public class ColorUpdAd extends RecyclerView.Adapter<ColorUpdAd.ColorViewHolder> {
    private static final int TYPE_COLOR = 0;
    private static final int TYPE_ACTION = 1;

    Context ctx;
    List<Object> items = new ArrayList<>();
    NewsVM newsVM;
    InputAd inputAd;
    int pos;
    PopupWindow window;
    Word wd;
    @NonNull
    @Override
    public ColorUpdAd.ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ColorLayBinding bnd = ColorLayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ColorUpdAd.ColorViewHolder(bnd);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorUpdAd.ColorViewHolder h, int p) {
        Object item = items.get(p);
        TextView colorTV = h.bnd.color;

        if (getItemViewType(p) == TYPE_COLOR) {
            Color color = (Color) item;
            colorTV.setText(color.getName());
            colorTV.setTextColor(color.getColor());
            colorTV.setOnClickListener(v -> {
                wordUpd(wd, color.getColor());
            });
        } else {
            String action = (String) item;
            colorTV.setText(action);
            colorTV.setOnClickListener(v -> {
                if (action.equals("On/Off")) {
                    newsVM.updWd(new Word(wd.getId(), wd.getWord(), wd.getColor(), wd.getNum(), !wd.getStatus()));
                    inputAd.notifyItemChanged(pos);
                    window.dismiss();
                } else if (action.equals("Delete")) {
                    newsVM.delWd(wd);
                    inputAd.notifyItemChanged(pos);
                    window.dismiss();
                }
            });
        }
    }

    @Override
    public int getItemCount() { return items.size(); }
    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        private final ColorLayBinding bnd;
        public ColorViewHolder(@NonNull ColorLayBinding bnd) {
            super(bnd.getRoot());
            this.bnd = bnd;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Color) {
            return TYPE_COLOR;
        } else {
            return TYPE_ACTION;
        }
    }

    public void getColors(Context ctx, NewsVM newsVM, InputAd inputAd, int pos, PopupWindow window, Word wd) {
        this.ctx = ctx;
        this.newsVM = newsVM;
        this.inputAd = inputAd;
        this.pos = pos;
        this.window = window;
        this.wd = wd;
        List<Color> colors = ColorCons.getAllColors(ctx);
        items.addAll(colors);
        items.add("On/Off");
        items.add("Delete");
    }
    private void wordUpd(Word wd, int color) {
        Word updated =  new Word(wd.getId(), wd.getWord(), color, wd.getNum(), wd.getStatus());
        newsVM.updWd(updated);
        inputAd.notifyItemChanged(pos);
        window.dismiss();
    }
}
