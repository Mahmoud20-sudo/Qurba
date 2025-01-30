package com.qurba.android.ui.offers.views;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.qurba.android.R;
import com.qurba.android.adapters.OffersAdapter;
import com.qurba.android.databinding.ActivitySearchResultsOffersBinding;
import com.qurba.android.network.models.OffersModel;
import com.qurba.android.ui.offers.view_models.OffersSearchResultsViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.PaginationScrollListener;

import java.util.ArrayList;
import java.util.List;

public class OffersSearchResultsActivity extends BaseActivity {

    private OffersSearchResultsViewModel viewModel;
    private ActivitySearchResultsOffersBinding binding;
    private boolean isLoading;
    private int currentPage = 1;
    private String query;
    private boolean isLastPage = false;
    private OffersAdapter offersAdapter;
    private int resultsCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
    }

    private void initialization() {
        viewModel = new ViewModelProvider(this).get(OffersSearchResultsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_results_offers);
        binding.setSearchResultsVM(viewModel);

        binding.animToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("query")) {
                this.query = getIntent().getStringExtra("query");
                binding.animToolbar.setTitle(query);
                viewModel.searchOffers(query, currentPage);
            }
        }

        initializeAdapters();
        initializeObservables();
    }

    private void initializeAdapters() {
        offersAdapter = new OffersAdapter((BaseActivity) this, new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.searchResultsListRv.setLayoutManager(linearLayoutManager);
        binding.searchResultsListRv.setAdapter(offersAdapter);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        binding.searchResultsListRv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                viewModel.searchOffers(query, currentPage);
            }

            @Override
            public boolean isLastPage() {
                isLoading = false;
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void initializeObservables() {
        viewModel.getOffersSearchObservable().observe(this, this::publishSearchResults);
    }

    private void publishSearchResults(List<OffersModel> offerSearchModels) {
        if (offerSearchModels.isEmpty()) {
            if (currentPage == 1) {
                binding.searchResultsListRv.setVisibility(View.GONE);
                binding.searchOffersNoResultsRl.setVisibility(View.VISIBLE);
            } else {
                isLastPage = true;
            }
        } else {
            offerSearchModels.size();
            binding.searchResultsListRv.setVisibility(View.VISIBLE);
            binding.searchNoLocationsTv.setVisibility(View.GONE);
            resultsCount = resultsCount + offerSearchModels.size();
            binding.searchResultsCountTv.setText(resultsCount + " " + getString(R.string.search_results));
            isLoading = false;
            offersAdapter.addAll(offerSearchModels);
        }
    }
}
