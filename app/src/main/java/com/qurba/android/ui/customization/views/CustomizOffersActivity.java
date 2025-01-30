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
import com.qurba.android.adapters.CustomOffersAdapter;
import com.qurba.android.databinding.ActivityCustomizeOfferBinding;
import com.qurba.android.network.models.OffersModel;
import com.qurba.android.ui.customization.view_models.CustomizOffersViewModel;
import com.qurba.android.ui.offers.view_models.OfferDetailsViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;

public class CustomizOffersActivity extends BaseActivity {

    private CustomizOffersViewModel viewModel;
    private ActivityCustomizeOfferBinding binding;
    private CustomOffersAdapter customOffersAdapter;
    private OfferDetailsViewModel offerDetailsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
    }

    private void initialization() {
        viewModel = new ViewModelProvider(this).get(CustomizOffersViewModel.class);
        offerDetailsViewModel = new ViewModelProvider(this).get(OfferDetailsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customize_offer);
        binding.setViewModel(viewModel);
        binding.setOfferVM(offerDetailsViewModel);
        viewModel.setActivity(this);
        binding.addToCartLl.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#de1d31"), PorterDuff.Mode.MULTIPLY));

        OffersModel offersModel = new Gson().fromJson(getIntent().getStringExtra(Constants.OFFERS), OffersModel.class);
//        viewModel.setOffersModel(offersModel);
        viewModel.setFromDetails(getIntent().hasExtra(Constants.IS_FROM_DETAILS));
        Glide.with(this).load(offersModel.getPictureUrl()).placeholder(R.drawable.ic_image_placeholder).into(binding.offerImageIv);
        offerDetailsViewModel.setActivity(this);
        offerDetailsViewModel.getOfferDetails(offersModel.get_id());

        initToolbar();
        initializeObservables();
    }

    private void initializeObservables() {
        offerDetailsViewModel.getOfferObservable().observe(this, this::setOfferData);
    }

    private void setOfferData(OffersModel offersModel) {
        viewModel.setOffersModel(offersModel);
        initAdapter(offersModel);
    }

    private void initToolbar() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.getNavigationIcon().setTint(Color.WHITE);
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void initAdapter(OffersModel offersModel) {
        customOffersAdapter = new CustomOffersAdapter(this , offersModel);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.offerCustomizesRv.getItemAnimator().setChangeDuration(0);
        binding.offerCustomizesRv.setItemAnimator(null);
        binding.offerCustomizesRv.setNestedScrollingEnabled(false);
        binding.offerCustomizesRv.setLayoutManager(linearLayoutManager);
        binding.offerCustomizesRv.setAdapter(customOffersAdapter);
        binding.offerCustomizesRv.setItemAnimator(null);
        customOffersAdapter.setCustomizOffersViewModel(viewModel);
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
