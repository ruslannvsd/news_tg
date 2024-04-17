package com.example.newstg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.newstg.adap.ArtAd;
import com.example.newstg.adap.SumAd;
import com.example.newstg.consts.Cons;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.FragmentFirstBinding;
import com.example.newstg.network.WorkMng;
import com.example.newstg.obj.Article;
import com.example.newstg.obj.Word;
import com.example.newstg.popups.ChnAdd;
import com.example.newstg.popups.InputPopup;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FirstFragment extends Fragment implements SumAd.OnKeywordClick {

    private FragmentFirstBinding bnd;
    NewsVM newsVM;
    LifecycleOwner owner;
    RecyclerView artRv;
    ArtAd artAd;
    RecyclerView sumRv;
    SumAd sumAd;
    TextView unique;

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
        newsVM = new ViewModelProvider(this).get(NewsVM.class);
        unique = bnd.unique;
        if (TextUtils.isEmpty(unique.getText())) {
            unique.setVisibility(View.GONE);
        }
        owner = getViewLifecycleOwner();
        artRv = bnd.articleRv;
        artAd = new ArtAd();
        sumRv = bnd.summaryRv;
        sumAd = new SumAd(this);
        bnd.on.setOnClickListener(v -> schedulePeriodicWork());
        bnd.off.setOnClickListener(v -> cancelPeriodicWork());
        bnd.channel.setOnClickListener(v -> {
            new ChnAdd().chlAdd(requireContext(), newsVM, owner);
        });
        bnd.makeChg.setOnClickListener(v ->
                new InputPopup().inputPopup(
                        requireContext(),
                        newsVM,
                        owner,
                        artRv,
                        artAd,
                        sumRv,
                        sumAd,
                        bnd.unique
                )
        );
        newsVM.getArticles().observe(getViewLifecycleOwner(), articles -> {
            artAd.setArticles(articles, requireContext());
            rvSetting(artRv, artAd, requireContext(), false);
        });
        newsVM.getResults().observe(getViewLifecycleOwner(), results -> {
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
        newsVM.getArticles().observe(getViewLifecycleOwner(), articles -> {
            List<Article> customArt = new ArrayList<>();
            if (keyword.getWord().equals(Cons.ALL)) {
                customArt = articles;
                uniqueKeywords(new ArrayList<>());
            } else {
                for (Article article : articles) {
                    if (article.keywords.contains(keyword.getWord())) {
                        customArt.add(article);
                    }
                }
                uniqueKeywords(customArt);
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

    private void schedulePeriodicWork() {
        int hours = 6;
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest newRequest = new PeriodicWorkRequest.Builder(WorkMng.class, hours, TimeUnit.HOURS)
                .setConstraints(constraints).build();
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork("PeriodicWork", ExistingPeriodicWorkPolicy.KEEP, newRequest);
        Toast.makeText(requireContext(), "Notifications Set", Toast.LENGTH_SHORT).show();
    }
    private void cancelPeriodicWork() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("PeriodicWork");
        Toast.makeText(requireContext(), "Notifications Cancelled", Toast.LENGTH_SHORT).show();
        // newsVM.delChn();
    }
    public void uniqueKeywords(List<Article> articles) {
        if (articles.isEmpty()) {
            unique.setVisibility(View.GONE);
            unique.setText(null);
            return;
        }
        Set<String> uniqueKeywordsSet = new HashSet<>();
        for (Article article : articles) {
            uniqueKeywordsSet.addAll(article.keywords);
        }
        unique.setVisibility(View.VISIBLE);
        unique.setText(String.join(" | ", new ArrayList<>(uniqueKeywordsSet)));
    }

}