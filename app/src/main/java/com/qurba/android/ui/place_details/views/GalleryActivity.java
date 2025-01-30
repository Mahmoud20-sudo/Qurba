package com.qurba.android.ui.place_details.views;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.adapters.GalleryAdapter;
import com.qurba.android.databinding.ActivityGalleryBinding;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.RecyclerItemClickListener;
import com.qurba.android.utils.SharedPreferencesManager;

public class GalleryActivity extends BaseActivity {

    private ActivityGalleryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                Constants.USER_OPEN_IMAGE_GALLERY,
                Line.LEVEL_INFO, "User successfully opens images gallery"
                , "");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);

        binding.animToolbar.getNavigationIcon().setTint(Color.BLACK);

        binding.recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        binding.recyclerView.setLayoutManager(staggeredGridLayoutManager);
        GalleryAdapter rcAdapter = new GalleryAdapter(this);
        rcAdapter.setData(SharedPreferencesManager.getInstance().getImages());

        binding.recyclerView.setAdapter(rcAdapter);
        binding.recyclerView.setItemAnimator(null);

        binding.animToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
        });
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
}