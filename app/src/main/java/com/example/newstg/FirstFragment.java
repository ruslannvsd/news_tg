package com.example.newstg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.adap.ArtAd;
import com.example.newstg.adap.SumAd;
import com.example.newstg.consts.Cons;
import com.example.newstg.data.WordVM;
import com.example.newstg.databinding.FragmentFirstBinding;
import com.example.newstg.obj.Article;
import com.example.newstg.obj.Word;
import com.example.newstg.popups.InputPopup;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment implements SumAd.OnKeywordClick {

    private FragmentFirstBinding bnd;
    WordVM wordVM;
    LifecycleOwner owner;
    RecyclerView artRv;
    ArtAd artAd;
    RecyclerView sumRv;
    SumAd sumAd;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        bnd = FragmentFirstBinding.inflate(inflater, container, false);
        return bnd.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wordVM = new ViewModelProvider(this).get(WordVM.class);
        owner = getViewLifecycleOwner();
        artRv = bnd.articleRv;
        artAd = new ArtAd();
        sumRv = bnd.summaryRv;
        sumAd = new SumAd(this);
        bnd.makeChg.setOnClickListener(v ->
                new InputPopup().inputPopup(
                        requireContext(),
                        wordVM,
                        owner,
                        artRv,
                        artAd,
                        sumRv,
                        sumAd
                )
        );
        wordVM.getArticles().observe(getViewLifecycleOwner(), articles -> {
            artAd.setArticles(articles, requireContext());
            rvSetting(artRv, artAd, requireContext(), false);
        });
        wordVM.getResults().observe(getViewLifecycleOwner(), results -> {
            sumAd.setWords(results, requireContext(), -1);
            rvSetting(sumRv, sumAd, requireContext(), true);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bnd = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onKeywordClick(@NonNull Word keyword) {
        wordVM.getArticles().observe(getViewLifecycleOwner(), articles -> {
            List<Article> customArt = new ArrayList<>();
            if (keyword.getWord().equals(Cons.ALL)) {
                customArt = articles;
            } else {
                for (Article article : articles) {
                    if (article.keywords.contains(keyword.getWord())) {
                        customArt.add(article);
                    }
                }
            }
            artAd.setArticles(customArt, requireContext());
            artAd.notifyDataSetChanged();
        });

    }

    private void rvSetting(
            RecyclerView rv,
            RecyclerView.Adapter<? extends RecyclerView.ViewHolder> ad,
            Context ctx,
            boolean useFlexbox
    ) {
        RecyclerView.LayoutManager layoutManager;
        if (useFlexbox) {
            FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(ctx);
            flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
            layoutManager = flexboxLayoutManager;
        } else {
            layoutManager = new LinearLayoutManager(ctx);
        }
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(ad);
    }
}