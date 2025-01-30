package com.qurba.android.ui.place_details.views;

import static com.qurba.android.utils.extenstions.ExtesionsKt.setInentResult;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.qurba.android.R;
import com.qurba.android.adapters.SimilarPlacesAdapter;
import com.qurba.android.databinding.ActivityPlaceDetailsBinding;
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.network.models.PlaceCategoryModel;
import com.qurba.android.network.models.PlaceModel;
import com.qurba.android.network.models.response_models.PlaceDetailsHeaderPayload;
import com.qurba.android.ui.address_component.views.AddressActivity;
import com.qurba.android.ui.place_details.view_models.PagerAgentViewModel;
import com.qurba.android.ui.place_details.view_models.PlaceDetailsBrandNewViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetailsActivity extends BaseActivity {

    private PlaceDetailsBrandNewViewModel viewModel;
    private ActivityPlaceDetailsBinding binding;

    private String placeUniqueName;
    private String placeId = "";
    private PagerAgentViewModel pagerAgentViewModel;

    private SharedPreferencesManager sharedPreferencesManager;
    private Fragment active;
    private InfoFragment infoFragment = new InfoFragment();
    private PlaceProductsFragment placeProductsFragment = new PlaceProductsFragment();
    private PlaceOffersFragment placeOffersFragment = new PlaceOffersFragment();
    private AddAddressModel addAddressModel;
    private SimilarPlacesAdapter similarPlacesAdapter;
    private String commentsId, commentsPage;
    private String replyId;
    private String selectedTab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
        initListeners();
    }

    public void nabigateToMenuTab() {
        this.binding.placeTabs.getTabAt(1).select();
    }

    private void initialization() {
        sharedPreferencesManager = SharedPreferencesManager.getInstance();
        viewModel = new ViewModelProvider(this).get(PlaceDetailsBrandNewViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_place_details);
        binding.setViewModel(viewModel);
        binding.setPagerAgentVM(new ViewModelProvider(this).get(PagerAgentViewModel.class));

        addAddressModel = SharedPreferencesManager.getInstance().getSelectedCachedArea();
        placeUniqueName = getIntent().getStringExtra("unique_name");
        placeId = getIntent().getStringExtra("place_id");
        commentsPage = getIntent().getExtras().getString("comments-page");
        commentsId = getIntent().getExtras().getString("comment-id");
        replyId = getIntent().getExtras().getString("reply-id");

        if (getSupportFragmentManager().findFragmentByTag(placeOffersFragment.getClass().getName()) != null) {
            getSupportFragmentManager().beginTransaction().remove(placeOffersFragment).commitAllowingStateLoss();
        }

        if (getSupportFragmentManager().findFragmentByTag(placeProductsFragment.getClass().getName()) != null) {
            getSupportFragmentManager().beginTransaction().remove(placeProductsFragment).commitAllowingStateLoss();
        }

        if (getSupportFragmentManager().findFragmentByTag(infoFragment.getClass().getName()) != null) {
            getSupportFragmentManager().beginTransaction().remove(infoFragment).commitAllowingStateLoss();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_fl, placeOffersFragment, PlaceOffersFragment.class.getName()).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_fl, placeProductsFragment, PlaceProductsFragment.class.getName()).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_fl, infoFragment, InfoFragment.class.getName()).commitAllowingStateLoss();

        initToolbar();
        initTabs();
        initAdapters();

        active = infoFragment;
        this.binding.placeTabs.getTabAt(1).select();//for seeking not displaying two fragments below each other
        this.binding.placeTabs.getTabAt(0).select();
        if (selectedTab == null && getIntent().getExtras().containsKey(Constants.ORDER_TYPE))
            selectedTab = getIntent().getExtras().get(Constants.ORDER_TYPE).toString();

        initializeObservables();
        viewModel.setActivity(this);

        pagerAgentViewModel = new ViewModelProvider(this).get(PagerAgentViewModel.class);
        getData();
        this.pagerAgentViewModel.init(this);
    }

    private void getData() {
        viewModel.getPlaceDetails(placeId);
    }

    private void initializeObservables() {
        viewModel.getDetailsObservable().observe(this, this::updatePlaceDetails);
        viewModel.getSimilarPlacesObservable().observe(this, this::updateSimilarPlaces);
    }

    private void updateSimilarPlaces(List<PlaceModel> placeModels) {
        if (placeModels.isEmpty()) {
            return;
        }
        similarPlacesAdapter.updateList(placeModels);
        new Handler(Looper.getMainLooper()).postDelayed(() -> binding.expandableLayout.expand(true), 100);
    }

    private void initListeners() {
        binding.expandableLayout.setOnClickListener(v -> {
            if (binding.expandableLayout.isCollapsed()) {
                binding.expandableLayout.expand();
            } else {
                binding.expandableLayout.collapse();
            }
        });

        binding.similarPlaceAreaCloseIv.setOnClickListener(v -> binding.expandableLayout.collapse());
        binding.notDeliveringView.editLocationTv.setOnClickListener(v -> startActivityForResult(new Intent(PlaceDetailsActivity.this, AddressActivity.class), 11901));
        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0)
                binding.animToolbar.getNavigationIcon().setTint(Color.BLACK);
            else
                //Expanded
                binding.animToolbar.getNavigationIcon().setTint(Color.WHITE);
        });
    }

    private void initAdapters() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.placeSimilarPlacesRv.setLayoutManager(linearLayoutManager);
        similarPlacesAdapter = new SimilarPlacesAdapter();
        binding.placeSimilarPlacesRv.setAdapter(similarPlacesAdapter);
    }

    private void initToolbar() {
        binding.collapsingToolbar.setTitle("");
        setSupportActionBar(binding.animToolbar);
        binding.animToolbar.getNavigationIcon().setTint(Color.WHITE);
        binding.animToolbar.setNavigationOnClickListener(view -> onBackPressed());

        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                binding.animToolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_color));

            } else {
                //Expanded
                binding.animToolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            invalidateOptionsMenu();
        });
    }

    private void initTabs() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PLACE_UNIQUE_NAME, placeUniqueName);
        bundle.putString(Constants.PLACE_ID, placeId);

        if (getIntent().hasExtra(Constants.PLACE))
            bundle.putString(Constants.PLACE, getIntent().getStringExtra(Constants.PLACE));

        this.binding.placeTabs.addTab(this.binding.placeTabs.newTab().setText(getString(R.string.info)));
        this.binding.placeTabs.addTab(this.binding.placeTabs.newTab().setText(this.getString(R.string.menu)));
        this.binding.placeTabs.addTab(this.binding.placeTabs.newTab().setText(getString(R.string.offers)));
        //this.binding.placeTabs.addTab(this.binding.placeTabs.newTab().setText(getString(R.string.reviews)));

        infoFragment.setArguments(bundle);
        placeProductsFragment.setArguments(bundle);
        placeOffersFragment.setArguments(bundle);

        this.binding.placeTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabReselected(TabLayout.Tab tab) {
            }

            public void onTabSelected(TabLayout.Tab tab) {
                int n = tab.getPosition();
                switch (n) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .hide(placeProductsFragment).hide(placeOffersFragment).show(infoFragment).commit();
                        active = infoFragment;
                        selectedTab = null;
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .hide(placeOffersFragment).hide(active).show(placeProductsFragment).commit();
                        active = placeProductsFragment;
                        selectedTab = "products";
                        break;
                    default:
                        getSupportFragmentManager().beginTransaction().hide(active)
                                .hide(placeProductsFragment).show(placeOffersFragment).
                                commit();
                        active = placeOffersFragment;
                        selectedTab = "offers";
                        break;
                }
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }
        });
    }

    private void updatePlaceDetails(PlaceDetailsHeaderPayload placeDetailsHeaderPayload) {
        if (getIntent().hasExtra(Constants.PLACE))
            placeDetailsHeaderPayload.setRecentComment(new Gson().fromJson(getIntent()
                    .getStringExtra(Constants.PLACE), PlaceModel.class).getRecentComment());

        binding.setPlaceModel(placeDetailsHeaderPayload);

        if (!viewModel.isDeliveringToArea()) {
            viewModel.voteArea(null, placeDetailsHeaderPayload.get_id());

            if (placeDetailsHeaderPayload.getDeliveringBranches() != null && !placeDetailsHeaderPayload.getDeliveringBranches().isEmpty())
                showAvailableBranchDialog(placeDetailsHeaderPayload.getDeliveringBranches().get(0).get_id(), false);
        }

        String placeName = sharedPreferencesManager.getLanguage().equalsIgnoreCase("en") ? placeDetailsHeaderPayload.getName().getEn() +
                " - " + placeDetailsHeaderPayload.getBranchName().getEn() : placeDetailsHeaderPayload.getName().getAr() +
                " - " + placeDetailsHeaderPayload.getBranchName().getAr();
        String categoryName = "";

        binding.notDeliveringView.notDeliverTitleTv.setText(placeName + " " + getString(R.string.not_deliver_hint)
                + " " + addAddressModel.getArea().getName().getEn());
        binding.placeDetailsRb.setRating(placeDetailsHeaderPayload.getPlaceRating());
        binding.placeRatingCountsTv.setText("(" + placeDetailsHeaderPayload.getFollowersCount() + ")");

        if (placeDetailsHeaderPayload.getPlaceProfilePictureUrl() != null) {
            Glide.with(this).load(placeDetailsHeaderPayload.getPlaceProfilePictureUrl()).placeholder(R.drawable.ic_bg_photo).into(binding.placeImageIv);
        }

        if (placeDetailsHeaderPayload.getPlaceProfileCoverPictureUrl() != null) {
            Glide.with(this).load(placeDetailsHeaderPayload.getPlaceProfileCoverPictureUrl()).placeholder(R.mipmap.place_details_placeholder).into(binding.backdrop);
        }

        if (commentsId != null)
            viewModel.openCommentsOverlay(replyId != null, commentsId, replyId);

        binding.productDetailsNameTv.setText(placeName);
        binding.collapsingToolbar.setTitle(placeName);
//        this.binding.placeTabs.getTabAt(1).setText(placeDetailsHeaderPayload.getCategories().get(0).getProductsTapName());
        placeOffersFragment.refreshRecyclerView();
        placeProductsFragment.refreshRecyclerView();

        for (PlaceCategoryModel categoryModel : placeDetailsHeaderPayload.getCategories()) {
            categoryName += sharedPreferencesManager.getLanguage().equalsIgnoreCase("en")
                    ? categoryModel.getName().getEn() : categoryModel.getName().getAr() + ",";
        }
        binding.placeCategoryTv.setText(categoryName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == 11901 && resultCode == -1) {
            //for address change form overalay
            AddAddressModel deliveryResponse = new Gson().fromJson(data.getStringExtra(Constants.ADDRESS), AddAddressModel.class);
            ArrayList<AddAddressModel> savedDeliveryAddressList = SharedPreferencesManager.getInstance().getSavedDeliveryAddress();
            if (!savedDeliveryAddressList.contains(deliveryResponse))
                savedDeliveryAddressList.add(deliveryResponse);
            SharedPreferencesManager.getInstance().setSavedDeliveryAddress(savedDeliveryAddressList);
            SharedPreferencesManager.getInstance().setSelectedCachedArea(deliveryResponse);
            SharedPreferencesManager.getInstance().setOrdering(true);
            reload();
        }
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.setLocationEnabled(true);
            viewModel.calculateDistance();
        }
    }

    public void initCartView() {
        if (sharedPreferencesManager.getCart() != null)
            this.pagerAgentViewModel.init(this);

        viewModel.getCart(this, cartModel -> {
            this.pagerAgentViewModel.init(this);
            return null;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        QurbaApplication.setCurrentActivity(this);
        viewModel.getSimilarPlaces(placeId);
        initCartView();
        if (selectedTab != null) {
            this.binding.placeTabs.getTabAt(selectedTab
                    .equalsIgnoreCase(Constants.PRODUCTS) ? 1 : 2).select();
            active = selectedTab
                    .equalsIgnoreCase(Constants.PRODUCTS) ? placeOffersFragment : placeProductsFragment;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        QurbaApplication.setCurrentActivity(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(Constants.PLACE, new Gson().toJson(viewModel.getPlaceDetailsModel().get_id() == null ? binding.getPlaceModel() : viewModel.getPlaceDetailsModel()));
        setInentResult(this, intent);

        super.finish();
    }
}
