package com.qurba.android.ui.place_details.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.qurba.android.R;
import com.qurba.android.adapters.ProductsAdapter;
import com.qurba.android.databinding.FragmentPlaceProductsBinding;
import com.qurba.android.network.models.PlaceModel;
import com.qurba.android.network.models.ProductData;
import com.qurba.android.ui.place_details.view_models.PlaceProductsViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import static com.qurba.android.utils.Constants.PAGE_START;

public class PlaceProductsFragment extends Fragment {

    private PlaceProductsViewModel viewModel;
    private FragmentPlaceProductsBinding binding;
    private int currentPage = PAGE_START;
    private ProductsAdapter productsAdapter;
    private boolean isOrdering = false;
    List<ProductData> productsListResponses = new ArrayList<>();

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_place_products, container, false);
        viewModel = new ViewModelProvider(this).get(PlaceProductsViewModel.class);
        binding.setPlaceProductsViewModel(viewModel);
        viewModel.setActivity((BaseActivity) context);

        callAPI();
        initializeAdapter();
        initializeObservables();
        return binding.getRoot();
    }

    private void initializeObservables() {
        viewModel.getProductsObservable().observe(getViewLifecycleOwner(), this::publishProducts);
    }

    public void refreshRecyclerView() {
        // productsAdapter.notifyDataSetChanged();
    }

    private void callAPI() {
        //currentPage = 1;
        viewModel.getProducts(currentPage, getArguments().getString(Constants.PLACE_ID));
    }

    private void initializeAdapter() {
        productsAdapter = new ProductsAdapter(((BaseActivity) context), productsListResponses);
        productsAdapter.setPlaceInfo(new Gson().fromJson(getArguments().getString(Constants.PLACE), PlaceModel.class));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        binding.placeProductsRv.setLayoutManager(linearLayoutManager);
        binding.placeProductsRv.setAdapter(productsAdapter);
        binding.placeProductsRv.showShimmerAdapter();
        binding.placeProductsRv.setNestedScrollingEnabled(false);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        NestedScrollView scroll = ((PlaceDetailsActivity) context).findViewById(R.id.nestedScrollView);
        scroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            View view = scroll.getChildAt(scroll.getChildCount() - 1);
            int diff = (view.getBottom() - (scroll.getHeight() + scroll
                    .getScrollY()));

            if (diff == 0 && productsAdapter.getItemCount() > 1) {
                binding.progressBar.setVisibility(View.VISIBLE);
                viewModel.getProducts(currentPage, getArguments().getString(Constants.PLACE_ID));
//                Toast.makeText(getActivity(), currentPage + "", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static PlaceProductsFragment getInstance() {
        return new PlaceProductsFragment();
    }

    private void publishProducts(List<ProductData> productsListResponses) {
        if(context == null) return;
        this.viewModel.setOrdering(this.isOrdering);
        binding.progressBar.setVisibility(View.GONE);
        currentPage++;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //for ultimating the tabs ui performance
            if (productsAdapter.getItemCount() == 0 || productsAdapter.getItemCount() == 1)
                this.binding.placeProductsRv.hideShimmerAdapter();
            this.productsAdapter.addAll(productsListResponses);
//            this.binding.placeProductsRv.setVisibility(View.VISIBLE);
        }, currentPage == 1 ? 1000 : 100);

        if (currentPage == 1) {
            if (productsListResponses.isEmpty()) {
                this.binding.placeProductsRv.setVisibility(View.GONE);
                this.binding.noProductsTv.setVisibility(View.VISIBLE);
                return;
            }
            productsAdapter.setPlaceProductsViewModel(viewModel);
            productsAdapter.setDiscountMenu(viewModel.isDiscountMenu());
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onActivityResult(int n, int n2, Intent intent) {
        super.onActivityResult(n, n2, intent);

        if (n == 1000 && n2 == -1) {
            ProductData productData = new Gson().fromJson(intent.getExtras().getString(Constants.PRODUCTS), ProductData.class);
            if (SharedPreferencesManager.getInstance().getProductPosition() != -1) {
                productsAdapter.updateProduct(SharedPreferencesManager.getInstance().getProductPosition(), productData);
                SharedPreferencesManager.getInstance().setProductPosition(-1);
            }
        }
    }
}