package com.qurba.android.ui.offers.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.qurba.android.R;
import com.qurba.android.adapters.CommentsAdapter;
import com.qurba.android.adapters.GalleryAdapter;
import com.qurba.android.adapters.ImagePagerAdapter;
import com.qurba.android.adapters.OffersAdapter;
import com.qurba.android.adapters.PlaceOffersAdapter;
import com.qurba.android.databinding.ActivityOffersDetailsV2Binding;
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.network.models.OffersModel;
import com.qurba.android.network.models.CommentModel;
import com.qurba.android.network.models.PlaceDetailsModel;
import com.qurba.android.ui.address_component.views.AddressActivity;
import com.qurba.android.ui.offers.view_models.OfferDetailsViewModel;
import com.qurba.android.ui.place_details.view_models.PlaceOffersViewModel;
import com.qurba.android.ui.place_details.views.GalleryActivity;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.SelectAddressCallBack;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import static com.qurba.android.utils.ViewUtils.setLineStrike;
import static com.qurba.android.utils.extenstions.ExtesionsKt.setInentResult;

public class OfferDetailsActivity extends BaseActivity implements SelectAddressCallBack {

    private OfferDetailsViewModel viewModel;
    private PlaceOffersViewModel placeOffersViewModel;

    private ActivityOffersDetailsV2Binding binding;
    private boolean appBarExpanded = true;
    private Menu collapsedMenu;
    private OffersAdapter offersAdapter;
    private boolean isFavoritedByUser = false;
    private List<CommentModel> commentList = new ArrayList<>();
    private CommentsAdapter commentsAdapter;
    private AddAddressModel deliveryResponse;
    private String commentsId, commentsPage;
    private String replyId;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
    }

    private void initialization() {
        viewModel = new ViewModelProvider(this).get(OfferDetailsViewModel.class);
        placeOffersViewModel = new ViewModelProvider(this).get(PlaceOffersViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_offers_details_v2);
        binding.setViewModel(viewModel);
        viewModel.setActivity(this);
        binding.animToolbar.getNavigationIcon().setTint(Color.BLACK);

        initListeners();
        initializeAdapters();
        initializeObservables();
        getOfferDetails();
    }

    private void initializeAdapters() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentsAdapter = new CommentsAdapter(this, this.commentList, getIntent().getExtras().getString("offer_id"), "offer", null);
        binding.offersCommentsRv.setAdapter(this.commentsAdapter);
        binding.offersCommentsRv.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
    }

    private void initListeners() {
//        binding.expandableLayout.setOnClickListener(v -> {
//                    if (binding.expandableLayout.isCollapsed())
//                        binding.expandableLayout.expand(true);
//                    else
//                        binding.expandableLayout.collapse(true);
//                });
        binding.notDeliveringView.editLocationTv.setOnClickListener(v -> startActivityForResult(new Intent(OfferDetailsActivity.this, AddressActivity.class), 11901));
        binding.animToolbar.setNavigationOnClickListener(view -> onBackPressed());

        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                binding.animToolbar.getNavigationIcon().setTint(Color.BLACK);
            else
                //Expanded
                binding.animToolbar.getNavigationIcon().setTint(Color.WHITE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initializeObservables() {
        viewModel.getOfferObservable().observe(this, this::setOfferData);
        placeOffersViewModel.getOffersObservable().observe(this, this::setOtherOffersData);
    }

    private void setUpViewPager(List<String> offerImages) {

        ImagePagerAdapter mViewPagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), offerImages);
        mViewPagerAdapter.setItemSelectionListener(this);

        binding.bannerViewPager.setAdapter(mViewPagerAdapter);
        binding.bannerViewPageIndicator.setViewPager(binding.bannerViewPager);

        binding.bannerViewPager.setScrollDurationFactor(2.0);
        binding.bannerViewPager.setOffscreenPageLimit(offerImages.size() - 1);

        int[] listIndex = new int[]{0};
        Handler handler = new Handler(Looper.getMainLooper());
        Thread timer = new Thread() {
            public void run() {
                try {
                    if (listIndex[0] == offerImages.size()) {
                        listIndex[0] = 0;
                        binding.bannerViewPager.setCurrentItem(listIndex[0], false);
                    } else {
                        binding.bannerViewPager.setCurrentItem(listIndex[0], true);
                        listIndex[0]++;
                    }

                    handler.postDelayed(this, 3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        timer.start();
    }

    private void getOfferDetails() {
        if (getIntent().getExtras() != null && getIntent().hasExtra("offer_id")) {
            commentsPage = getIntent().getExtras().getString("comments-page");
            commentsId = getIntent().getExtras().getString("comment-id");
            replyId = getIntent().getExtras().getString("reply-id");
            String offerID = getIntent().getExtras().getString("offer_id");
            isFavoritedByUser = getIntent().getExtras().getBoolean("favorite");
            viewModel.setFromOrderNow(getIntent().getExtras().getBoolean("is-from-ordernow"));
            viewModel.getOfferDetails(offerID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_offers_share, menu);
        collapsedMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (collapsedMenu != null && (!appBarExpanded || collapsedMenu.size() != 1)) {
            //collapsed
            menu.getItem(0).setIcon(R.drawable.ic_share);
            if (isFavoritedByUser) {
                collapsedMenu.add("Favorite")
                        .setIcon(R.drawable.ic_red_heart)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            } else {
                collapsedMenu.add("Favorite")
                        .setIcon(R.drawable.ic_path_copy)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
        }
        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(Constants.ADDRESS, new Gson().toJson(deliveryResponse));
        intent.putExtra(Constants.OFFERS, new Gson().toJson(this.binding.getOffersModel()));
        setInentResult(this, intent);
        super.finish();
    }

    private void setOfferData(OffersModel offersModel) {
        commentList.clear();
        commentList.addAll(offersModel.getComments());
        commentsAdapter.notifyItemRangeInserted(0, offersModel.getComments().size());

        binding.setOffersModel(offersModel);
        placeOffersViewModel.setCurrentPage(0);
        placeOffersViewModel.getOffers(1, offersModel.getPlaceId().get_id(), offersModel.get_id());

        if (!offersModel.isDeliveringToArea()) {
            viewModel.voteArea(null, offersModel.getPlaceId().get_id());

            if (offersModel.getDeliveringBranches() != null && !offersModel.getDeliveringBranches().isEmpty())
                showAvailableBranchDialog(offersModel.getDeliveringBranches().get(0).get_id(), true);
        }

        if (commentsId != null)
            viewModel.openCommentsOverlay(replyId != null, commentsId, replyId);

        if (offersModel.getGalleryUrls() != null && !offersModel.getGalleryUrls().isEmpty()) {
            setGalleryData(offersModel);
        }

        ArrayList<String> viewPagerImages = new ArrayList<>();
        viewPagerImages.add(offersModel.getPictureUrl());
        viewPagerImages.addAll(offersModel.getGalleryUrls());

        setUpViewPager(viewPagerImages);
        if (offersModel.getPlaceId() != null && offersModel.getPlaceId().getPlaceProfilePictureUrl() != null) {
            Glide.with(this).load(offersModel.getPlaceId().getPlaceProfilePictureUrl()).error(R.drawable.ic_bg_photo).placeholder(R.drawable.ic_bg_photo).into(binding.placeImageIv);
        }

        if (offersModel.isOrderable()) {
            binding.availabilityTv.setCompoundDrawablesWithIntrinsicBounds(
                    sharedPref.getLanguage().equalsIgnoreCase("ar") ? null : getResources().getDrawable(R.drawable.ic_grey_clock),
                    null,
                    sharedPref.getLanguage().equalsIgnoreCase("en") ? null : getResources().getDrawable(R.drawable.ic_grey_clock),
                    null);
        } else {
            binding.availabilityTv.setCompoundDrawablesWithIntrinsicBounds(
                    sharedPref.getLanguage().equalsIgnoreCase("ar") ? null : getResources().getDrawable(R.drawable.ic_closed),
                    null,
                    sharedPref.getLanguage().equalsIgnoreCase("en") ? null : getResources().getDrawable(R.drawable.ic_closed),
                    null);
        }

        setLineStrike(binding.offerDetailsOldPriceTv);
        setImages(offersModel.getGalleryUrls());
        binding.notDeliveringView.notDeliverTitleTv.setText(offersModel.getPlaceId().getName().getEn() + " " + getString(R.string.not_deliver_hint) + " " + SharedPreferencesManager.getInstance().getSelectedCachedArea().getArea().getName().getEn());
//        new Handler(Looper.getMainLooper()).postDelayed(() -> binding.expandableLayout.expand(true), 200);
    }

    private void setGalleryData(OffersModel offersModel) {

            binding.galleryRv.setHasFixedSize(true);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
            binding.galleryRv.setLayoutManager(staggeredGridLayoutManager);
            GalleryAdapter rcAdapter = new GalleryAdapter(this);
            rcAdapter.setData(offersModel.getGalleryUrls().size() > 6
                    ? offersModel.getGalleryUrls().subList(0, 6) : offersModel.getGalleryUrls());

            binding.galleryRv.setAdapter(rcAdapter);
            binding.galleryRv.setItemAnimator(null);

            binding.viewAllGalleryTv.setOnClickListener(view -> {
                SharedPreferencesManager.getInstance().setImages(offersModel.getGalleryUrls());
                Intent intent = new Intent(this, GalleryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });

    }


    private void setOtherOffersData(List<OffersModel> otherOffersData) {
        if (!otherOffersData.isEmpty()) {
            binding.otherOffersRv.setVisibility(View.VISIBLE);
            PlaceOffersAdapter offersAdapter = new PlaceOffersAdapter(this, otherOffersData.size() > 3 ?
                    otherOffersData.subList(0, 3) : otherOffersData);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            binding.otherOffersRv.setHasFixedSize(true);
            binding.otherOffersRv.setLayoutManager(linearLayoutManager);
            binding.otherOffersRv.setAdapter(offersAdapter);
            binding.otherOffersRv.setNestedScrollingEnabled(false);
        } else {
            binding.otherOffersRv.setVisibility(View.GONE);
            binding.otherOffersTv.setVisibility(View.GONE);
            binding.viewAllOfferLl.setVisibility(View.GONE);
        }
    }

    private void setImages(List<String> urls) {
        if (urls != null && !urls.isEmpty()) {
            binding.galleryRv.setHasFixedSize(true);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
            binding.galleryRv.setLayoutManager(staggeredGridLayoutManager);
            GalleryAdapter rcAdapter = new GalleryAdapter(this);
            rcAdapter.setData(urls.size() > 6
                    ? urls.subList(0, 6) : urls);

            binding.galleryRv.setAdapter(rcAdapter);
            binding.galleryRv.setItemAnimator(null);

            binding.viewAllGalleryTv.setOnClickListener(view -> {
                SharedPreferencesManager.getInstance().setImages(urls);
                Intent intent = new Intent(this, GalleryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onSelect(AddAddressModel deliveryResponse) {
        viewModel.openImagesPreview();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11901 && resultCode == -1) {
            //for address change form overalay
            deliveryResponse = new Gson().fromJson(data.getStringExtra(Constants.ADDRESS), AddAddressModel.class);
            ArrayList<AddAddressModel> savedDeliveryAddressList = SharedPreferencesManager.getInstance().getSavedDeliveryAddress();
            if (!savedDeliveryAddressList.contains(deliveryResponse))
                savedDeliveryAddressList.add(deliveryResponse);
            SharedPreferencesManager.getInstance().setSavedDeliveryAddress(savedDeliveryAddressList);
            SharedPreferencesManager.getInstance().setSelectedCachedArea(deliveryResponse);
            SharedPreferencesManager.getInstance().setOrdering(true);
            getOfferDetails();
        }
    }
}
