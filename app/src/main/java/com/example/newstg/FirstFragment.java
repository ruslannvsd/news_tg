package com.example.newstg;

import static com.example.newstg.network.FetchUtils.sortingNum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.adap.ArtAd;
import com.example.newstg.adap.ChnAd;
import com.example.newstg.adap.SumAd;
import com.example.newstg.consts.Cons;
import com.example.newstg.data.NewsVM;
import com.example.newstg.databinding.FragmentFirstBinding;
import com.example.newstg.obj.Article;
import com.example.newstg.obj.Word;
import com.example.newstg.popups.ChnAdd;
import com.example.newstg.popups.InputPopup;
import com.example.newstg.utils.Count;
import com.example.newstg.utils.Scheduler;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FirstFragment extends Fragment implements SumAd.OnKeywordClick, SumAd.OnLongKeywordClick {
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
        owner = getViewLifecycleOwner();
        artRv = bnd.articleRv;
        artAd = new ArtAd();
        sumRv = bnd.summaryRv;
        sumAd = new SumAd(this, this);

        newsVM.offArticles().observe(owner, articles -> {
            newsVM.clearArticles();
            if (articles.size() != 0) {
                Log.i("List size", Integer.toString(articles.size()));
                newsVM.setArticles(articles);
                newsVM.getAll().observe(owner, keywords -> {
                    newsVM.setWords(sortingNum(new Count().results(keywords, articles), requireContext()));
                });
            }
        });


        bnd.on.setOnClickListener(v -> new Scheduler().setupWorkSchedules(requireContext(), 24, 4));
        bnd.off.setOnClickListener(v -> {
            Scheduler scheduler = new Scheduler();
            scheduler.cancelPeriodicWork(Scheduler.HOURS_24);
            scheduler.cancelPeriodicWork(Scheduler.HOURS_4);
            Toast.makeText(requireContext(), "Notification cancelled", Toast.LENGTH_SHORT).show();
        });

        bnd.channel.setOnClickListener(v -> {
            new ChnAdd().chlAdd(requireContext(), newsVM, owner, new ChnAd());
        });
        bnd.makeChg.setOnClickListener(v -> {
                    new InputPopup().inputPopup(
                            requireContext(),
                            newsVM,
                            owner,
                            artRv,
                            artAd,
                            sumRv,
                            sumAd,
                            unique
                    );
                }
        );
        newsVM.getArticles().observe(getViewLifecycleOwner(), articles -> {
            artAd.setArticles(articles, requireContext(), null);
            rvSetting(artRv, artAd, requireContext(), false);
            new Count().articleAmountText(unique, articles.size());
        });
        newsVM.getResults().observe(getViewLifecycleOwner(), results -> {
            sumAd.setSummary(results, requireContext(), -1);
            rvSetting(sumRv, sumAd, requireContext(), true);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bnd = null;
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

    public Set<String> associated(@NonNull List<Article> articles) {
        if (articles.isEmpty()) {
            return null;
        }
        Set<String> uniqueKeywordsSet = new HashSet<>();
        for (Article article : articles) {
            uniqueKeywordsSet.addAll(article.keywords);
        }
        return uniqueKeywordsSet;
    }

    @Override
    public void onKeywordClick(@NonNull Word keyword) {
        newsVM.getArticles().observe(getViewLifecycleOwner(), new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles == null || articles.isEmpty()) {
                    return;
                }
                if (keyword.getWord().equals(Cons.ALL)) {
                    updateUI(articles, null, null, true);
                } else {
                    List<Article> filteredArticles = articles.stream()
                            .filter(article -> article.keywords.contains(keyword.getWord()))
                            .collect(Collectors.toList());

                    Set<String> associated = associated(filteredArticles);
                    Set<String> words = new HashSet<>();
                    words.add(keyword.getWord());
                    updateUI(filteredArticles, associated, words, true);
                }
                newsVM.getArticles().removeObserver(this);
            }
        });
    }

    @Override
    public void onLongClick(List<Word> words) {
        newsVM.getArticles().observe(getViewLifecycleOwner(), new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles == null || articles.isEmpty()) {
                    return;
                }
                Set<String> wordSet = words.stream()
                        .map(Word::getWord)
                        .collect(Collectors.toSet());
                List<Article> filteredArticles = articles.stream()
                        .filter(article -> new HashSet<>(article.keywords).containsAll(wordSet))
                        .collect(Collectors.toList());
                Set<String> associated = associated(filteredArticles);
                updateUI(filteredArticles, associated, wordSet, false);
                newsVM.getArticles().removeObserver(this);
            }
        });
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void updateUI(List<Article> articles, Set<String> associatedKeywords, Set<String> words, boolean shortClick) {
        int amount = articles.size();
        new Count().articleAmountText(unique, amount);
        artAd.setArticles(articles, requireContext(), words);
        artAd.notifyDataSetChanged();
        sumAd.associated(associatedKeywords, shortClick);
        sumAd.notifyDataSetChanged();
    }
}
