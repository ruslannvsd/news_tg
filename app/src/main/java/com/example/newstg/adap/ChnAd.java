package com.example.newstg.adap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.WordsBinding;
import com.example.newstg.obj.Chn;
import com.example.newstg.popups.ChnPopup;
import com.example.newstg.utils.WordSorting;

import java.util.ArrayList;
import java.util.List;

public class ChnAd extends RecyclerView.Adapter<ChnAd.ChnViewHolder> {
    List<Chn> channels = new ArrayList<>();
    Context ctx;
    NewsVM newsVM;
    @NonNull
    @Override
    public ChnAd.ChnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        WordsBinding bnd = WordsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChnAd.ChnViewHolder(bnd);
    }

    @Override
    public void onBindViewHolder(@NonNull ChnViewHolder h, int p) {
        Chn chn = channels.get(p);
        h.bnd.word.setText(chn.name);
        h.bnd.card.setCardBackgroundColor(chn.category);
        h.bnd.card.setOnClickListener(v -> {
            new ChnPopup().colorPopup(ctx, chn, newsVM, p, this);
            String[] parts = chn.link.split("/");
            String title = parts[parts.length - 1];
            Toast.makeText(ctx, title, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() { return channels.size(); }

    public static class ChnViewHolder extends RecyclerView.ViewHolder {
        private final WordsBinding bnd;
        public ChnViewHolder(@NonNull WordsBinding bnd) {
            super(bnd.getRoot());
            this.bnd = bnd;
        }
    }
    public void setChannels(List<Chn> channels, Context ctx, NewsVM newsVM) {
        WordSorting.sortChannels(channels, ctx);
        this.channels = channels;
        this.ctx = ctx;
        this.newsVM = newsVM;
        this.notifyDataSetChanged();
    }
}
