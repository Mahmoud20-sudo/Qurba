package com.qurba.android.ui.place_details.views;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.adapters.GalleryAdapter;
import com.qurba.android.adapters.ImagePagerAdapter;
import com.qurba.android.databinding.ActivityGalleryBinding;
import com.qurba.android.databinding.ActivityImagePreviewBinding;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.List;

public class GalleryPreviewActivity extends BaseActivity {

    private ActivityImagePreviewBinding binding;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_preview);
        binding.animToolbar.getNavigationIcon().setTint(Color.BLACK);

        setUpViewPager(getIntent().getExtras().getStringArrayList("images"));

        binding.animToolbar.setNavigationOnClickListener(this::onClick);

        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                Constants.USER_OPEN_IMAGE_PREVEIEW,
                Line.LEVEL_INFO, "User successfully opens image preview"
                , "");
    }

    private void setUpViewPager(List<String> offerImages) {
        ImagePagerAdapter mViewPagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), offerImages);
        binding.bannerViewPager.setAdapter(mViewPagerAdapter);
        binding.bannerViewPageIndicator.setViewPager(binding.bannerViewPager);
        binding.bannerViewPager.setOffscreenPageLimit(offerImages.size() - 1);
        binding.bannerViewPageIndicator.setVisibility(offerImages.size() > 1 ? View.VISIBLE : View.GONE);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesManager.getInstance().setImages(null);
    }

    private void onClick(View view) {
        onBackPressed();
        finish();
    }
}