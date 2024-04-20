package com.example.newstg.adap;

import static java.util.Collections.emptyList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.consts.ColorCons;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.WordsBinding;
import com.example.newstg.obj.Word;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class InputAd extends RecyclerView.Adapter<InputAd.InputViewHolder> {
    List<Word> words = emptyList();
    Context ctx;
    NewsVM wordVm;
    EditText inputField;

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
        if (word.getStatus()) {
            h.bnd.card.setCardBackgroundColor(ctx.getColor(R.color.gray));
        } else {
            h.bnd.card.setCardBackgroundColor(ctx.getColor(R.color.black));
        }

        h.itemView.setOnClickListener(v -> showPopupMenu(v, word, p));
        h.itemView.setOnLongClickListener(v -> {
            String field = inputField.getText().toString();
            if (field.isEmpty()) {
                inputField.setText(word.getWord());
            } else {
                String text = field + " " + word.getWord();
                inputField.setText(text);
            }
            return true;
        });
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
            if (id == R.id.sky) {
                wordUpd(wd, ColorCons.sky(ctx), p);
            } else if (id == R.id.leaf) {
                wordUpd(wd, ColorCons.leaf(ctx), p);
            } else if (id == R.id.sun) {
                wordUpd(wd, ColorCons.sun(ctx), p);
            } else if (id == R.id.fox) {
                wordUpd(wd, ColorCons.fox(ctx), p);
            } else if (id == R.id.evening) {
                wordUpd(wd, ColorCons.evening(ctx), p);
            } else if (id == R.id.flower) {
                wordUpd(wd, ColorCons.flower(ctx), p);
            } else if (id == R.id.water) {
                wordUpd(wd, ColorCons.water(ctx), p);
            } else if (id == R.id.cloud) {
                wordUpd(wd, ColorCons.cloud(ctx), p);
            } else if (id == R.id.on_off) {
                wordVm.updWd(new Word(wd.getId(), wd.getWord(), wd.getColor(), wd.getNum(), !wd.getStatus()));
                notifyItemChanged(p);
            } else if (id == R.id.delete) {
                wordVm.delWd(wd);
                notifyItemChanged(p);
            }
            return true;
        });
        popupMenu.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setWords(List<Word> words, Context ctx, NewsVM wordVm, EditText inputField) {
        if (words != null) {
            words.sort(sorting());
        }
        this.words = words;
        this.ctx = ctx;
        this.wordVm = wordVm;
        this.inputField = inputField;
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
    private void wordUpd(Word wd, int color, int p) {
        Word updated =  new Word(wd.getId(), wd.getWord(), color, wd.getNum(), wd.getStatus());
        wordVm.updWd(updated);
        notifyItemChanged(p);
    }
}
