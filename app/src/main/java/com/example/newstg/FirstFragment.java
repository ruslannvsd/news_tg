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
        owner = getViewLifecycleOwner();
        artRv = bnd.articleRv;
        artAd = new ArtAd();
        sumRv = bnd.summaryRv;
        sumAd = new SumAd(this);

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


        bnd.on.setOnClickListener(v -> new Scheduler().setupWorkSchedules(requireContext(), 22, 24));
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
                            bnd.unique
                    );
                    bnd.unique.setVisibility(View.GONE);
                }
        );
        newsVM.getArticles().observe(getViewLifecycleOwner(), articles -> {
            artAd.setArticles(articles, requireContext(), null);
            rvSetting(artRv, artAd, requireContext(), false);
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

    @Override
    public void onKeywordClick(@NonNull Word keyword) {
        newsVM.getArticles().observe(getViewLifecycleOwner(), new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles == null || articles.isEmpty()) {
                    return;
                }

                if (keyword.getWord().equals(Cons.ALL)) {
                    updateUI(articles, null, keyword.getWord());
                } else {
                    List<Article> filteredArticles = articles.stream()
                            .filter(article -> article.keywords.contains(keyword.getWord()))
                            .collect(Collectors.toList());

                    Set<String> associated = associated(filteredArticles);
                    updateUI(filteredArticles, associated, keyword.getWord());
                }
                newsVM.getArticles().removeObserver(this);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(List<Article> articles, Set<String> associatedKeywords, String keyword) {
        artAd.setArticles(articles, requireContext(), keyword);
        artAd.notifyDataSetChanged();
        sumAd.associated(associatedKeywords);
        sumAd.notifyDataSetChanged();
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

    public Set<String> associated(List<Article> articles) {
        if (articles.isEmpty()) {
            return null;
        }
        Set<String> uniqueKeywordsSet = new HashSet<>();
        for (Article article : articles) {
            uniqueKeywordsSet.addAll(article.keywords);
        }
        return uniqueKeywordsSet;
    }
}