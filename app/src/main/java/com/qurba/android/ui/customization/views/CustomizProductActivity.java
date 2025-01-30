package com.qurba.android.ui.customization.views;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qurba.android.R;
import com.qurba.android.adapters.CustomProductsAdapter;
import com.qurba.android.databinding.ActivityCustomizeProductBinding;
import com.qurba.android.network.models.ProductData;
import com.qurba.android.ui.customization.view_models.CustomizProductsViewModel;
import com.qurba.android.ui.products.view_models.ProductDetailsViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;

import java.util.Objects;

public class CustomizProductActivity extends BaseActivity {

    private CustomizProductsViewModel viewModel;
    private ProductDetailsViewModel productDetailsViewModel;

    private ActivityCustomizeProductBinding binding;
    private CustomProductsAdapter customProductsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
    }

    private void initialization() {
        viewModel = new ViewModelProvider(this).get(CustomizProductsViewModel.class);
        productDetailsViewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_customize_product);
        binding.setViewModel(viewModel);
        binding.setProductVM(productDetailsViewModel);
        viewModel.setActivity(this);
        binding.addToCartLl.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#de1d31"), PorterDuff.Mode.MULTIPLY));

        ProductData productData = new Gson().fromJson(getIntent().getStringExtra(Constants.PRODUCTS), ProductData.class);
        productDetailsViewModel.setActivity(this);
        productDetailsViewModel.getProductDetails(productData.get_id());
        viewModel.setFromDetails(getIntent().hasExtra(Constants.IS_FROM_DETAILS));

        initToolbar();
        initializeObservables();
    }

    private void initializeObservables() {
        Objects.requireNonNull(productDetailsViewModel.getProductObservable()).observe(this, this::setProductData);
    }

    private void setProductData(ProductData productData) {
        viewModel.setProductData(productData);
        Glide.with(this).load(productData.getImageURL())
                .placeholder(R.drawable.ic_image_placeholder).into(binding.offerImageIv);
        initAdapter(productData);
    }

    private void initToolbar() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.getNavigationIcon().setTint(Color.WHITE);
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void initAdapter(ProductData productData) {
        customProductsAdapter = new CustomProductsAdapter(this, productData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.offerCustomizesRv.setNestedScrollingEnabled(false);
        binding.offerCustomizesRv.setLayoutManager(linearLayoutManager);
        binding.offerCustomizesRv.setAdapter(customProductsAdapter);
        binding.offerCustomizesRv.setItemAnimator(null);
        customProductsAdapter.setCustomizProductsViewModel(viewModel);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        QurbaApplication.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        QurbaApplication.setCurrentActivity(null);
    }
}
