package com.example.newstg.adap;

import static java.util.Collections.emptyList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.data.WordVM;
import com.example.newstg.databinding.WordsBinding;
import com.example.newstg.obj.Word;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class InputAd extends RecyclerView.Adapter<InputAd.InputViewHolder> {
    List<Word> words = emptyList();
    Context ctx;
    WordVM wordVm;

    @NonNull
    @Override
    public InputViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        WordsBinding bnd = WordsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new InputViewHolder(bnd);
    }

    @Override
    public void onBindViewHolder(@NonNull InputAd.InputViewHolder h, int p) {
        Word word = words.get(p);
        String title = word.getWord();
        h.bnd.word.setText(title);
        h.bnd.word.setTextColor(word.getColor());
        h.bnd.card.setCardBackgroundColor(ctx.getColor(R.color.gray));
        h.itemView.setOnClickListener(v -> showPopupMenu(v, word, p));
    }

    @Override
    public int getItemCount() { return words.size(); }

    public static class InputViewHolder extends RecyclerView.ViewHolder {
        private final WordsBinding bnd;

        public InputViewHolder(@NonNull WordsBinding bnd) {
            super(bnd.getRoot());
            this.bnd = bnd;
        }
    }
    private void showPopupMenu(View view, Word wd, int p) {
        PopupMenu popupMenu = new PopupMenu(ctx, view);
        popupMenu.inflate(R.menu.item_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.yellow) {
                Word updWdYellow = new Word(wd.getId(), wd.getWord(), ctx.getColor(R.color.c_yellow), wd.getNum());
                wordVm.updWd(updWdYellow);
                notifyItemChanged(p);
            } else if (id == R.id.green) {
                Word updWdGreen = new Word(wd.getId(), wd.getWord(), ctx.getColor(R.color.c_green), wd.getNum());
                wordVm.updWd(updWdGreen);
                notifyItemChanged(p);
            } else if (id == R.id.blue) {
                Word updWdBlue = new Word(wd.getId(), wd.getWord(), ctx.getColor(R.color.c_blue), wd.getNum());
                wordVm.updWd(updWdBlue);
                notifyItemChanged(p);
            } else if (id == R.id.pink) {
                Word updWdPink = new Word(wd.getId(), wd.getWord(), ctx.getColor(R.color.c_pink), wd.getNum());
                wordVm.updWd(updWdPink);
                notifyItemChanged(p);
            } else if (id == R.id.purple) {
                Word updWdPurple = new Word(wd.getId(), wd.getWord(), ctx.getColor(R.color.c_purple), wd.getNum());
                wordVm.updWd(updWdPurple);
                notifyItemChanged(p);
            } else if (id == R.id.orange) {
                Word updWdOrange = new Word(wd.getId(), wd.getWord(), ctx.getColor(R.color.c_orange), wd.getNum());
                wordVm.updWd(updWdOrange);
                notifyItemChanged(p);
            } else if (id == R.id.gray) {
                Word updWdGray = new Word(wd.getId(), wd.getWord(), ctx.getColor(R.color.c_gray), wd.getNum());
                wordVm.updWd(updWdGray);
                notifyItemChanged(p);
            } else if (id == R.id.delete) {
                wordVm.delWd(wd);
                notifyItemChanged(p);
            }
            return false;
        });
        popupMenu.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setWords(List<Word> words, Context ctx, WordVM wordVm) {
        if (words != null) {
            words.sort(sorting());
        }
        this.words = words;
        this.ctx = ctx;
        this.wordVm = wordVm;
        notifyDataSetChanged();
    }
    private Comparator<Word> sorting() {
        return Comparator
                .comparing(Word::getColor, Comparator.reverseOrder())
                .thenComparing(Word::getWord,
                        Comparator.nullsFirst(
                                Collator.getInstance(new Locale("ru", "RU"))
                                        .thenComparing(Collator.getInstance(Locale.ENGLISH))
                        )
                );
    }
}
