package com.example.newstg.adap;

import static java.util.Collections.emptyList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.R;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.WordsBinding;
import com.example.newstg.obj.Word;
import com.example.newstg.popups.ColorPopup;
import com.example.newstg.utils.WordSorting;

import java.util.List;

public class InputAd extends RecyclerView.Adapter<InputAd.InputViewHolder> {
    List<Word> words = emptyList();
    Context ctx;
    NewsVM newsVM;
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

        h.itemView.setOnClickListener(v -> new ColorPopup().colorPopup(ctx, newsVM, p, this, word));
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

    @SuppressLint("NotifyDataSetChanged")
    public void setWords(List<Word> words, Context ctx, NewsVM wordVm, EditText inputField) {
        this.words = words;
        this.ctx = ctx;
        if (words != null) {
            WordSorting.sortWords(words, ctx);
        }
        this.newsVM = wordVm;
        this.inputField = inputField;
        notifyDataSetChanged();
    }
}
