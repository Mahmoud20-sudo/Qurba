package com.qurba.android.ui.products.views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qurba.android.R;
import com.qurba.android.adapters.CommentsAdapter;
import com.qurba.android.adapters.OffersAdapter;
import com.qurba.android.databinding.ActivityProductDetailsBinding;
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.network.models.CommentModel;
import com.qurba.android.network.models.ProductData;
import com.qurba.android.network.models.SectionItems;
import com.qurba.android.network.models.Sections;
import com.qurba.android.network.models.SectionsGroup;
import com.qurba.android.ui.address_component.views.AddressActivity;
import com.qurba.android.ui.products.view_models.ProductDetailsViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import static com.qurba.android.utils.ViewUtils.setLineStrike;
import static com.qurba.android.utils.extenstions.ExtesionsKt.setInentResult;

public class ProductDetailsActivity extends BaseActivity {

    private ProductDetailsViewModel viewModel;
    private ActivityProductDetailsBinding binding;
    private boolean appBarExpanded = true;
    private Menu collapsedMenu;
    private OffersAdapter offersAdapter;
    private boolean isFavoritedByUser = false;
    private String offerUniqueName;
    private String offerTitle;
    private String deepLink;
    private CommentsAdapter commentsAdapter;
    private List<CommentModel> commentList = new ArrayList<>();
    private String productId;
    private AddAddressModel deliveryResponse;
    private String commentsId, commentsPage;
    private String replyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
    }

    private void initialization() {
        viewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        binding.setViewModel(viewModel);
        binding.productItemOrderBtn.getBackground().setColorFilter(Color.parseColor("#de1d31"), PorterDuff.Mode.SRC_IN);

        initToolbar();
        initListeners();
        initializeObservables();
        initializeAdapters();
        viewModel.setActivity(this);
        viewModel.setCommentsAdapter(commentsAdapter);
        getProductDetails();
    }

    private void initListeners() {
        binding.notDeliveringView.editLocationTv.setOnClickListener(v -> startActivityForResult(new Intent(ProductDetailsActivity.this, AddressActivity.class), 11901));
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
        viewModel.getProductObservable().observe(this, this::setProductData);
    }

    private void getProductDetails() {
        if (getIntent().getExtras() != null && getIntent().hasExtra(Constants.PRODUCT_ID)) {
            productId = getIntent().getExtras().getString(Constants.PRODUCT_ID);
            isFavoritedByUser = getIntent().getExtras().getBoolean("favorite");
            commentsPage = getIntent().getExtras().getString("comments-page");
            commentsId = getIntent().getExtras().getString("comment-id");
            replyId = getIntent().getExtras().getString("reply-id");

            viewModel.getProductDetails(productId);
        }
    }

    private void initializeAdapters() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentsAdapter = new CommentsAdapter(this, this.commentList, getIntent().getExtras().getString(Constants.PRODUCT_ID), "product", null);
        binding.productsCommentsRv.setAdapter(this.commentsAdapter);
        binding.productsCommentsRv.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
    }

    private void initSectionItems(ProductData productData) {
        if (productData.getSections() == null || productData.getSections().isEmpty())
            return;

        binding.productCusomizationsLl.removeAllViews();

        for (Sections sections : productData.getSections()) {
            initSectionsData(sections, null);
        }

        //for group of sections
        if (productData.getSectionGroups() != null) {
            for (SectionsGroup group : productData.getSectionGroups()) {
                for (Sections sections : group.getSections()) {
                    initSectionsData(sections, group);
                }
            }
        }
    }

    private void initSectionsData(Sections sections, SectionsGroup group) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 17, getResources()
                        .getDisplayMetrics());

        if (group != null) {
            TextView groupNameTv = new TextView(this);
            groupNameTv.setText(group.getTitle().getEn());
            groupNameTv.setTextColor(Color.parseColor("#333333"));
            groupNameTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            groupNameTv.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            groupNameTv.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.0f, getResources().getDisplayMetrics()), 1.0f);
            binding.productCusomizationsLl.addView(groupNameTv);

            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) groupNameTv.getLayoutParams();
            param.topMargin = marginInDp;
            groupNameTv.setLayoutParams(param);
        }

        TextView sectionTv = new TextView(this);
        sectionTv.setText(sections.getTitle().getEn());
        sectionTv.setTextColor(Color.parseColor("#333333"));
        sectionTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        sectionTv.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        sectionTv.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.0f, getResources().getDisplayMetrics()), 1.0f);

        binding.productCusomizationsLl.addView(sectionTv);
        binding.productCusomizationsLl.addView(linearLayout);

        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) sectionTv.getLayoutParams();
        param.topMargin = marginInDp;
//        param.setMarginStart(group != null ? marginInDp : 0);
        sectionTv.setLayoutParams(param);

        for (SectionItems sectionItems : sections.getItems()) {
            TextView itemsTv = new TextView(this);
            itemsTv.setText(sectionItems.getTitle().getEn());
            itemsTv.setTextColor(Color.parseColor("#333333"));
            itemsTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            itemsTv.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
            itemsTv.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.0f, getResources().getDisplayMetrics()), 1.0f);
            linearLayout.addView(itemsTv);

            LinearLayout.LayoutParams childParam = (LinearLayout.LayoutParams) itemsTv.getLayoutParams();
            childParam.topMargin = marginInDp;
//            childParam.setMarginStart(marginInDp);
            itemsTv.setLayoutParams(param);
        }
    }

    private void initToolbar() {
        binding.collapsingToolbar.setTitle("");
        setSupportActionBar(binding.animToolbar);

        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                appBarExpanded = false;
                //binding.animToolbar.setNavigationIcon(R.mipmap.back);
                invalidateOptionsMenu();

            } else {
                //Expanded
                //binding.animToolbar.setNavigationIcon(R.drawable.ic_back_white);
                appBarExpanded = true;
                invalidateOptionsMenu();
            }
        });

        binding.animToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_offers_share, menu);
        collapsedMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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
        intent.putExtra(Constants.PRODUCTS, new Gson().toJson(this.binding.getProductModel()));
        intent.putExtra(Constants.ADDRESS, new Gson().toJson(deliveryResponse));
        setInentResult(this, intent);
        super.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setProductData(ProductData productData) {
        binding.setProductModel(productData);

        commentList.clear();
        commentList.addAll(productData.getProductCommentsSample());
        commentsAdapter.notifyDataSetChanged();

        if (productData.getImageURL() != null) {
            Glide.with(this).load(productData.getImageURL()).placeholder(R.drawable.ic_image_placeholder).into(binding.productIv);
        } else{
            binding.productIv.setForeground(getDrawable(R.drawable.gradient));}

        if (!productData.isDeliveringToArea()) {
            viewModel.voteArea(null, productData.getPlaceInfo().get_id());

            if (productData.getDeliveringBranches() != null && !productData.getDeliveringBranches().isEmpty())
                showAvailableBranchDialog(productData.getDeliveringBranches().get(0).get_id(), false);
        }

        Glide.with(this).load(productData.getPlaceInfo().getPlaceProfilePictureUrl()).into(binding.placeImageIv);

        if (commentsId != null)
            viewModel.openCommentsOverlay(replyId != null, commentsId, replyId);

        if (!viewModel.isHasImage()) {
            binding.animToolbar.getNavigationIcon().setTint(Color.WHITE);
            binding.collapsingToolbar.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        } else
            binding.animToolbar.getNavigationIcon().setTint(Color.BLACK);

        setLineStrike(binding.productDetailsOldPriceTv);
        initSectionItems(productData);
        binding.notDeliveringView.notDeliverTitleTv.setText(productData.getPlaceInfo().getName().getEn() + " " + getString(R.string.not_deliver_hint) + " " + SharedPreferencesManager.getInstance().getSelectedCachedArea().getArea().getName().getEn());
        binding.productDetailsLikeFb.bringToFront();
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
            getProductDetails();
        }
    }
}
