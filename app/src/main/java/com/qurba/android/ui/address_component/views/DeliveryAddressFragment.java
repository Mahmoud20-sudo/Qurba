package com.qurba.android.ui.address_component.views;

/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  android.app.Dialog
 *  android.content.Context
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnShowListener
 *  android.os.Bundle
 *  android.view.KeyEvent
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.ViewGroup
 *  android.view.ViewParent
 *  android.view.inputmethod.InputMethodManager
 *  android.widget.FrameLayout
 *  android.widget.ImageView
 *  android.widget.TextView
 *  android.widget.TextView$OnEditorActionListener
 *  androidx.appcompat.widget.AppCompatEditText
 *  androidx.databinding.DataBindingUtil
 *  androidx.fragment.app.Fragment
 *  androidx.fragment.app.FragmentActivity
 *  androidx.lifecycle.LifecycleOwner
 *  androidx.lifecycle.MutableLiveData
 *  androidx.lifecycle.Observer
 *  androidx.lifecycle.ViewModel
 *  androidx.lifecycle.ViewModelProviders
 *  androidx.recyclerview.widget.LinearLayoutManager
 *  androidx.recyclerview.widget.RecyclerView
 *  androidx.recyclerview.widget.RecyclerView$Adapter
 *  androidx.recyclerview.widget.RecyclerView$LayoutManager
 *  com.google.android.material.bottomsheet.BottomSheetBehavior
 *  com.google.android.material.bottomsheet.BottomSheetDialog
 *  com.google.android.material.bottomsheet.BottomSheetDialogFragment
 *  com.qurba.android.adapters.CommentsAdapter
 *  com.qurba.android.network.models.ProductCommentModel
 *  com.qurba.android.ui.comments.view_models.CommentsPopUpViewModel
 *  com.qurba.android.utils.AddCommentCallBack
 *  com.qurba.android.utils.BaseActivity
 *  com.qurba.android.utils.ScreenUtils
 *  com.qurba.android.utils.SharedPreferencesManager
 *  com.qurba.android.utils.SocialLoginCallBack
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 */

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.adapters.DeliveryAreasAdapter;
import com.qurba.android.databinding.DeliveryAddressDialogBinding;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.network.models.response_models.DeliveryAreaResponse;
import com.qurba.android.ui.address_component.view_models.DelieveryAddressViewModel;
import com.qurba.android.utils.AreaSelectCallBack;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.EndlessRecyclerViewScrollListener;
import com.qurba.android.utils.ItemClickEvents;
import com.qurba.android.utils.ScreenUtils;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.qurba.android.utils.extenstions.ExtesionsKt.addRxTextWatcher;

public class DeliveryAddressFragment
        extends BottomSheetDialogFragment implements ItemClickEvents {

    private DeliveryAddressDialogBinding binding;
    private DeliveryAreasAdapter adapter;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();
    private DelieveryAddressViewModel viewModel;
    private ArrayList<DeliveryAreaResponse> deliveryAreaResponses = new ArrayList<>();
    private int currentPage = 1;
    private AreaSelectCallBack areaSelectCallBack;
    private SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance();
    private EndlessRecyclerViewScrollListener recyclerViewScrollListener;
    private DeliveryAreaResponse cityDeliveryResponse;
    private String queryText = "";

    private void initialization() {
        initializeAdapters();
        initializeObservables();
        viewModel.getCities(currentPage);

//        binding.searchOffersSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                return false;
//            }
//        });

        addRxTextWatcher(binding.searchOffersSearchView
                .findViewById(androidx.appcompat.R.id.search_src_text))
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (s.length() == 0 && !adapter.isAreaData()) return;
                    currentPage = 1;
                    this.deliveryAreaResponses.clear();
                    this.deliveryAreaResponses.add(cityDeliveryResponse);
                    adapter.notifyDataSetChanged();
                    queryText = s;
                    viewModel.getAreas(currentPage, cityDeliveryResponse.get_id(), s);
                });
    }

    private void initializeObservables() {
        viewModel.getDeliveryResponse().observe(this, this::publishCities);
    }

    private void publishCities(List<DeliveryAreaResponse> deliveryAreaResponses) {
        for (DeliveryAreaResponse response : deliveryAreaResponses) {
            if (!this.deliveryAreaResponses.contains(response)) {
                this.deliveryAreaResponses.add(response);
                adapter.notifyItemChanged(this.deliveryAreaResponses.size() - 1);
            }
        }

        if (!adapter.isAreaData()) {
            binding.optionsTitleTv.setText(getString(R.string.select_your_city));
        } else {
            binding.optionsTitleTv.setText(getString(R.string.select_your_area));
        }
    }

    private void initializeAdapters() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        adapter = new DeliveryAreasAdapter((BaseActivity) this.requireActivity(), deliveryAreaResponses);
        adapter.setFromAddress(true);
        adapter.setItemsClickEvents(this);
        binding.deliveryPlacesRv.setAdapter(this.adapter);
        binding.deliveryPlacesRv.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.deliveryPlacesRv.setNestedScrollingEnabled(true);

        recyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                currentPage++;
                if (adapter.isAreaData())
                    viewModel.getAreas(currentPage, cityDeliveryResponse.get_id(), queryText);
                else
                    viewModel.getCities(currentPage);
            }
        };
        binding.deliveryPlacesRv.addOnScrollListener(recyclerViewScrollListener);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    public Dialog onCreateDialog(Bundle bundle) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(bundle);
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            public void onShow(DialogInterface dialogInterface) {
//                BottomSheetBehavior.from((View) ((BottomSheetDialog) dialogInterface).
//                        findViewById(com.google.android.material.R.id.design_bottom_sheet)).setState(STATE_EXPANDED);
            }
        });
        return bottomSheetDialog;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.delivery_address_dialog, viewGroup, false);

        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(DelieveryAddressViewModel.class);
        viewModel.setActivity((BaseActivity) getActivity());
        binding.setViewModel(viewModel);
        initialization();
        return view;
    }

    public void onResume() {
        super.onResume();
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override
    public void setupDialog(Dialog dialog, int n) {
        super.setupDialog(dialog, n);
        View view = View.inflate(this.getContext(), R.layout.delivery_address_dialog, null);
        dialog.setContentView(view);
        this.mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        BottomSheetBehavior<View> bottomSheetBehavior = this.mBottomSheetBehavior;
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setPeekHeight(new ScreenUtils(this.getContext()).getHeight() - 40);
            view.requestLayout();
        }
    }

    @Override
    public void onItemClickListener(AddAddressModel addAddressModel) {
    }

    @Override
    public void onItemClickListener(int position, boolean flag) {
        if (adapter.isAreaData() && position == 0)//city not clickable
            return;

        if (adapter.isAreaData()) {
            //caching selected address
            AddAddressModel deliveryResponse = new AddAddressModel();

            DeliveryAreaResponse city = new DeliveryAreaResponse();
            city.set_id(deliveryAreaResponses.get(position).getCity());
            city.setName(deliveryAreaResponses.get(0).getName());

            deliveryResponse.setCity(city);

            DeliveryAreaResponse country = new DeliveryAreaResponse();
            country.set_id(deliveryAreaResponses.get(position).getCountry());
            deliveryResponse.setCountry(country);

            deliveryResponse.setArea(deliveryAreaResponses.get(position));

//            ArrayList<AddAddressModel> savedDeliveryAddressList = sharedPreferencesManager.getSavedDeliveryAddress();
//            if (!savedDeliveryAddressList.contains(deliveryResponse))
//                savedDeliveryAddressList.add(deliveryResponse);
//            sharedPreferencesManager.setSavedDeliveryAddress(savedDeliveryAddressList);

            areaSelectCallBack.onAreaSelect(deliveryResponse);

            QurbaLogger.Companion.logging(getActivity(),
                    Constants.USER_ADDRESS_COMPONENT_SELECT_AREA_SUCCESS, Line.LEVEL_INFO,
                    "User select area from address component", "");

            dismiss();
        } else {
            currentPage = 1;
            binding.searchOffersSearchView.setVisibility(View.VISIBLE);
            adapter.setIsAreaData(true);
            cityDeliveryResponse = deliveryAreaResponses.get(position);
//            cityId = cityDeliveryResponse.get_id();
            deliveryAreaResponses.clear();
            adapter.notifyDataSetChanged();
            deliveryAreaResponses.add(cityDeliveryResponse);
            viewModel.getAreas(currentPage, cityDeliveryResponse.get_id(), queryText);

            QurbaLogger.Companion.logging(getActivity(),
                    Constants.USER_ADDRESS_COMPONENT_SELECT_CITY_SUCCESS, Line.LEVEL_INFO,
                    "User select city from address component", "");
        }
    }

    @Override
    public void onDeleteListener(int position, boolean isAddress) {

    }

    @Override
    public void onBackClickListener() {
        adapter.setIsAreaData(false);
        deliveryAreaResponses.clear();
//        adapter.notifyDataSetChanged();
        viewModel.getCities(1);
        binding.searchOffersSearchView.setVisibility(View.GONE);
        binding.searchOffersSearchView.setQuery("", false);
    }

    public void setAreaSelectCallBack(AreaSelectCallBack areaSelectCallBack) {
        this.areaSelectCallBack = areaSelectCallBack;
    }
}

