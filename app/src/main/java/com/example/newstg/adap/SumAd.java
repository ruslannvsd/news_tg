package com.example.newstg.adap;

import static java.util.Collections.emptyList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.databinding.SumLayBinding;
import com.example.newstg.obj.Word;

import java.util.List;
import java.util.Set;

public class SumAd extends RecyclerView.Adapter<SumAd.SumViewHolder> {
    List<Word> keywords = emptyList();
    Set<String> associated = null;
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
        String sum = wd.getWord() + " : " + wd.getNum();
        h.bnd.keyword.setText(sum);

        int backgroundColor;
        int textColor;
        if (p == 0) {
            backgroundColor = wd.getId();
            textColor = wd.getColor();
        } else if (pressed == p) {
            backgroundColor = wd.getColor();
            textColor = ctx.getColor(R.color.black);
        } else if (associated != null && associated.contains(wd.getWord()) && pressed != p) {
            backgroundColor = ctx.getColor(R.color.black);
            textColor = wd.getColor();
        } else {
            backgroundColor = wd.getId();
            textColor = wd.getColor();
        }

        h.bnd.card.setBackgroundColor(backgroundColor);
        h.bnd.keyword.setTextColor(textColor);

        h.itemView.setOnClickListener(v -> {
            notifyItemChanged(pressed);
            pressed = h.getBindingAdapterPosition();
            notifyItemChanged(pressed);
            if (onKeywordClick != null) {
                onKeywordClick.onKeywordClick(wd);
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
    public void setSummary(List<Word> keywords, Context ctx, int pressed) {
        this.pressed = pressed;
        this.keywords = keywords;
        this.ctx = ctx;
        notifyDataSetChanged();
    }
    public void associated(Set<String> associated) {
        this.associated = associated;
    }

    public interface OnKeywordClick {
        void onKeywordClick(Word keyword);
    }
    public SumAd(OnKeywordClick onKeywordClick) { this.onKeywordClick = onKeywordClick; }
}
