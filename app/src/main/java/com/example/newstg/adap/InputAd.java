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

        if (word.getImp()) {
            h.bnd.card.setCardBackgroundColor(ctx.getColor(R.color.dark_green));
        } else {
            h.bnd.card.setCardBackgroundColor(ctx.getColor(R.color.dark_red));
        }

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
            if (id == R.id.on_off) {
                Word updWd = new Word(wd.getId(), wd.getWord(), !wd.getImp(), wd.getNum());
                wordVm.updWd(updWd);
                this.notifyItemChanged(p);
                return true;
            }
            else if (id == R.id.delete) {
                wordVm.delWd(wd);
                this.notifyItemRemoved(p);
                return true;
            }
            else return false;
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
                .comparing(Word::getImp, Comparator.reverseOrder())
                .thenComparing(Word::getWord,
                        Comparator.nullsFirst(
                                Collator.getInstance(new Locale("ru", "RU"))
                                        .thenComparing(Collator.getInstance(Locale.ENGLISH))
                        )
                );
    }
}
