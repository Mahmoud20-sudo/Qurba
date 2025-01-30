package com.qurba.android.ui.place_details.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.paginate.Paginate;
import com.qurba.android.R;
import com.qurba.android.adapters.OffersAdapter;
import com.qurba.android.adapters.PlaceOffersAdapter;
import com.qurba.android.databinding.FragmentPlaceOffersBinding;
import com.qurba.android.network.models.OffersModel;
import com.qurba.android.network.models.PlaceModel;
import com.qurba.android.ui.place_details.view_models.PlaceOffersViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import static com.qurba.android.utils.Constants.PAGE_START;

public class PlaceOffersFragment extends Fragment {

    private PlaceOffersViewModel viewModel;
    private FragmentPlaceOffersBinding binding;
    private int currentPage = PAGE_START;
    private PlaceOffersAdapter offersAdapter;
    private boolean isOrdering = false;
    private List<OffersModel> offersModels = new ArrayList<>();
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_place_offers, container, false);
        viewModel = new ViewModelProvider(this).get(PlaceOffersViewModel.class);

        binding.setPlaceOffersViewModel(viewModel);
        initializeAdapter();
        callAPI();
        initializeObservables();
        return binding.getRoot();
    }

    public void refreshRecyclerView() {
        //offersAdapter.notifyDataSetChanged();
    }

    public void callAPI() {
        this.offersAdapter.clearAll();
        currentPage = 1;
//        viewModel.getOffers(currentPage, getArguments().getString(Constants.PLACE_ID));
    }

    public void setIsOrdering() {
        isOrdering = true;
    }

    private void initializeObservables() {
        viewModel.getOffersObservable().observe(getViewLifecycleOwner(), this::publishOffers);
    }

    private void initializeAdapter() {
        offersAdapter = new PlaceOffersAdapter(((BaseActivity) context), offersModels);
        offersAdapter.setPlaceInfo(new Gson().fromJson(getArguments().getString(Constants.PLACE), PlaceModel.class));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.placeOffersRv.setLayoutManager(linearLayoutManager);
        binding.placeOffersRv.setAdapter(offersAdapter);
        binding.placeOffersRv.showShimmerAdapter();
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        NestedScrollView scroll = ((PlaceDetailsActivity) context).findViewById(R.id.nestedScrollView);
        scroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            View view = scroll.getChildAt(scroll.getChildCount() - 1);
            int diff = (view.getBottom() - (scroll.getHeight() + scroll
                    .getScrollY()));

            if (diff == 0) {
                binding.progressBar.setVisibility(View.VISIBLE);
                viewModel.getOffers(currentPage, getArguments().getString(Constants.PLACE_ID));
            }
        });
    }

    public static PlaceOffersFragment getInstance() {
        return new PlaceOffersFragment();
    }

    private void publishOffers(List<OffersModel> offersModels) {
        if (context == null) return;
        currentPage++;
        viewModel.setOrdering(isOrdering);
        binding.progressBar.setVisibility(View.INVISIBLE);

        if (!offersModels.isEmpty() && offersModels.size() != 0) {
            if (this.offersAdapter.getItemCount() == 0) {
                this.binding.placeOffersRv.hideShimmerAdapter();
                this.binding.placeOffersRv.scrollToPosition(0);
            }
            this.binding.placeOffersRv.setVisibility(View.VISIBLE);
            this.binding.noOfferTv.setVisibility(View.GONE);
            new Handler(Looper.getMainLooper()).postDelayed(() -> addItem(offersModels), 20);
            return;
        }
        if (this.offersAdapter.getItemCount() == 0) {
            this.binding.placeOffersRv.setVisibility(View.GONE);
            this.binding.noOfferTv.setVisibility(View.VISIBLE);
            return;
        }
    }

    private void addItem(List<OffersModel> offersModels) {
        for (OffersModel offerModel : offersModels) {
            if (!this.offersModels.contains(offerModel)) {
                this.offersModels.add(offerModel);
                offersAdapter.notifyItemInserted(this.offersModels.size() - 1);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        offersAdapter.notifyDataSetChanged();
    }
}