package com.example.newstg.adap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.databinding.ColorLayBinding;

public class ChnColorUpdAd extends RecyclerView.Adapter<ChnColorUpdAd.ChnViewHolder> {
    int[] colors;
    OnKeywordClick listener;
    @NonNull
    @Override
    public ChnColorUpdAd.ChnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ColorLayBinding bnd = ColorLayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChnColorUpdAd.ChnViewHolder(bnd);
    }

    @Override
    public void onBindViewHolder(@NonNull ChnViewHolder h, int p) {
        int color = colors[p];
        CardView card = h.bnd.card;
        card.setCardBackgroundColor(color);
        card.setOnClickListener(v -> listener.onKeywordClick(color));
    }

    @Override
    public int getItemCount() { return colors.length; }

    public static class ChnViewHolder extends RecyclerView.ViewHolder {
        private final ColorLayBinding bnd;
        public ChnViewHolder(@NonNull ColorLayBinding bnd) {
            super(bnd.getRoot());
            this.bnd = bnd;
        }
    }

    public void setColors(Context ctx) {
        colors = new int[] {
                ctx.getColor(R.color.chn_1),
                ctx.getColor(R.color.chn_2),
                ctx.getColor(R.color.chn_3),
                ctx.getColor(R.color.chn_4),
                ctx.getColor(R.color.chn_5)
        };
    }
    public interface OnKeywordClick {
        void onKeywordClick(int color);
    }
    public ChnColorUpdAd(ChnColorUpdAd.OnKeywordClick listener) { this.listener = listener; }
}
