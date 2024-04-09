package com.example.newstg.adap;

import static java.util.Collections.emptyList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.databinding.SumLayBinding;
import com.example.newstg.obj.Word;

import java.util.List;

public class SumAd extends RecyclerView.Adapter<SumAd.SumViewHolder> {
    int largest;
    int smallest;
    List<Word> keywords = emptyList();
    Context ctx;
    private int pressed;
    private final OnKeywordClick onKeywordClick;

    @NonNull
    @Override
    public SumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SumLayBinding bnd = SumLayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SumAd.SumViewHolder(bnd);
    }

    @Override
    public void onBindViewHolder(@NonNull SumViewHolder h, int p) {
        Word wd = keywords.get(p);
        String sum = wd.getWord() + " - " + wd.getNum();
        h.bnd.keyword.setText(sum);
        int color = cardBgColor(wd.getNum(), ctx);
        h.bnd.card.setBackgroundColor(color);
        h.bnd.keyword.setTextColor(wd.getColor());

        if (pressed == p) {
            h.bnd.card.setBackgroundColor(ctx.getColor(R.color.black));
        } else {
            if (p == 0) {
                h.bnd.card.setBackgroundColor(ctx.getColor(R.color.gray));
            } else {
                h.bnd.card.setBackgroundColor(color);
            }
        }
        h.itemView.setOnClickListener(v -> {
            notifyItemChanged(pressed);
            pressed = h.getAdapterPosition();
            notifyItemChanged(pressed);
            if (onKeywordClick != null) {
                onKeywordClick.onKeywordClick(keywords.get(p));
            }
        });

    }

    @Override
    public int getItemCount() { return keywords.size(); }

    public static class SumViewHolder extends RecyclerView.ViewHolder {
        private final SumLayBinding bnd;
        public SumViewHolder(@NonNull SumLayBinding bnd) {
            super(bnd.getRoot());
            this.bnd = bnd;
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setWords(List<Word> keywords, Context ctx, int pressed) {
        this.pressed = pressed;
        this.keywords = keywords;
        this.ctx = ctx;
        if (keywords.size() > 1) {
            this.largest = keywords.get(1).getNum();
            this.smallest = keywords.get(keywords.size()-1).getNum();
        }
        notifyDataSetChanged();
    }
    public int cardBgColor(int quantity, Context ctx) {
        if (smallest == largest) {
            return ctx.getColor(R.color.two);
        }
        float range = (largest - smallest) / 5.0f;
        if (quantity > largest) {
            return ctx.getColor(R.color.green);
        } else if (quantity <= smallest + range) {
            return ctx.getColor(R.color.five);
        } else if (quantity <= smallest + 2 * range) {
            return ctx.getColor(R.color.four);
        } else if (quantity <= smallest + 3 * range) {
            return ctx.getColor(R.color.three);
        } else {
            return ctx.getColor(R.color.two);
        }
    }

    public interface OnKeywordClick {
        void onKeywordClick(Word keyword);
    }
    public SumAd(OnKeywordClick onKeywordClick) { this.onKeywordClick = onKeywordClick; }
}
