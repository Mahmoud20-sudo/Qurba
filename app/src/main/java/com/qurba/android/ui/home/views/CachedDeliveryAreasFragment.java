package com.qurba.android.ui.home.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qurba.android.R;
import com.qurba.android.adapters.CachedAddressesAdapter;
import com.qurba.android.adapters.CachedDeliveryAdapter;
import com.qurba.android.databinding.CachedDeliveryAddressDialogBinding;
import com.qurba.android.databinding.DialogUpdateBinding;
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.network.models.UserDataModel;
import com.qurba.android.network.models.response_models.DeliveryAreaResponse;
import com.qurba.android.network.models.response_models.DeliveryValidationPayload;
import com.qurba.android.ui.address_component.view_models.DelieveryAddressViewModel;
import com.qurba.android.ui.address_component.views.AddressActivity;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.ItemClickEvents;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.ScreenUtils;
import com.qurba.android.utils.SelectAddressCallBack;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import static com.qurba.android.utils.SystemUtils.restartApp;

public class CachedDeliveryAreasFragment
        extends BottomSheetDialogFragment implements ItemClickEvents {

    private CachedDeliveryAddressDialogBinding binding;
    private CachedDeliveryAdapter adapter;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();
    private DelieveryAddressViewModel viewModel;
    private ArrayList<DeliveryAreaResponse> deliveryAreaResponses = new ArrayList<>();
    private SelectAddressCallBack selectAddressCallBack;
    private List<AddAddressModel> itemList;
    private List<AddAddressModel> savedAddress;
    private CachedAddressesAdapter cachedAddressesAdapter;
    private int selectedAddressPoisiton = -1;
    private boolean isSavedAddress;
    private boolean isWithinOrderingFlow;
    private Context mContext;

    private void initialization() {
        viewModel.setActivity((BaseActivity) getActivity());
        itemList = new ArrayList<>();
        savedAddress = new ArrayList<>();
        initializeAdapters();
        initializeObservables();
        initListeners();
    }

    private void initializeObservables() {
        viewModel.getDeliveryResponse().observe(this, this::publishCities);
        viewModel.deleteObservable().observe(this, this::deleteAddress);
//        viewModel.responseMutableLiveData.observe(this, this::checkAddress);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public void checkAddress(DeliveryValidationPayload deliveryValidationPayload) {
        Log.e("MESSAGE", "lw 7d 3rf leh observable m4 mytnada 3leh hena donan 3n ba2i L observables yb2a y2oli");
        if (deliveryValidationPayload == null) {
            String message = QurbaApplication.getContext().getString(R.string.your_cart_contain_items_hint) + " " +
                    sharedPref.getCart().getPlaceModel().getName().getEn() + " " + QurbaApplication.getContext().getString(R.string.this_place_not_deliver_hint) + " " +
                    itemList.get(selectedAddressPoisiton).getArea().getName().getEn() + ". " + QurbaApplication.getContext().getString(R.string.clear_cart);

            //                viewModel.clearCart((BaseActivity) mContext, unit -> null);
            ((BaseActivity) mContext).showClearCartDialog(sharedPref.getCart().getPlaceModel().getName().getEn(), this::updateCachedData, message);
        } else {
            //case if place support new area
            viewModel.updateCartArea((BaseActivity) mContext, deliveryValidationPayload.getPayload().get_id(), aBoolean -> null);
            updateCachedData();
        }
    }

    private void deleteAddress(AddAddressModel addAddressModel) {
        savedAddress.remove(addAddressModel);

        if (savedAddress.isEmpty() && itemList.isEmpty()) {
            restartApp((BaseActivity) mContext);
            return;
        } else if (!savedAddress.isEmpty()) {
            sharedPref.setSelectedCachedArea(savedAddress.get(savedAddress.size() - 1));
            sharedPref.setSavedDeliveryAddress(savedAddress);
        } else {
            sharedPref.setSelectedCachedArea(itemList.get(itemList.size() - 1));
            sharedPref.setSavedDeliveryAddress(itemList);
        }

        cachedAddressesAdapter.notifyDataSetChanged();
        UserDataModel userDataModel = sharedPref.getUser();
        userDataModel.setDeliveryAddresses(savedAddress);
        sharedPref.setUser(userDataModel);
        selectAddressCallBack.onSelect(sharedPref.getSelectedCachedArea());
        viewModel.setHaveAddress();
    }

    private void initListeners() {
        binding.addNewAddressLl.setOnClickListener(v -> {
            ((BaseActivity) mContext).startActivityForResult(new Intent(mContext, AddressActivity.class), 11901);
        });
    }

    private void publishCities(List<DeliveryAreaResponse> deliveryAreaResponses) {
        this.deliveryAreaResponses.addAll(deliveryAreaResponses);
        adapter.notifyDataSetChanged();
    }

    private void initializeAdapters() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
        adapter = new CachedDeliveryAdapter((BaseActivity) this.requireActivity(), itemList);
        adapter.setItemsClickEvents(this);
        binding.deliveryPlacesRv.setAdapter(this.adapter);
        binding.deliveryPlacesRv.setNestedScrollingEnabled(false);
        binding.deliveryPlacesRv.setLayoutManager(new LinearLayoutManager(this.mContext));
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        if (!sharedPref.isGuest()) {
            cachedAddressesAdapter = new CachedAddressesAdapter((BaseActivity) this.requireActivity(), savedAddress, selectAddressCallBack);
            cachedAddressesAdapter.setItemsClickEvents(this);
            binding.savedAddressesRv.setAdapter(cachedAddressesAdapter);
            binding.savedAddressesRv.setNestedScrollingEnabled(false);
            binding.savedAddressesRv.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        }
    }

    private void updateCachedData() {
        if (isSavedAddress) {
            AddAddressModel deliveryArea = savedAddress.get(selectedAddressPoisiton);
            savedAddress.remove(selectedAddressPoisiton);
            savedAddress.add(deliveryArea);
            UserDataModel userDataModel = sharedPref.getUser();
            userDataModel.setDeliveryAddresses(savedAddress);
            sharedPref.setUser(userDataModel);
            sharedPref.setSelectedCachedArea(deliveryArea);
            onItemClickListener(deliveryArea);
        } else {
            AddAddressModel deliveryArea = itemList.get(selectedAddressPoisiton);
            itemList.remove(selectedAddressPoisiton);
            itemList.add(deliveryArea);
            sharedPref.setSavedDeliveryAddress(itemList);
            sharedPref.setSelectedCachedArea(deliveryArea);
            onItemClickListener(deliveryArea);
        }
        selectedAddressPoisiton = -1;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    public Dialog onCreateDialog(Bundle bundle) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(bundle);
        bottomSheetDialog.setOnShowListener(dialogInterface -> {
//                BottomSheetBehavior.from((View) ((BottomSheetDialog) dialogInterface).
//                        findViewById(com.google.android.material.R.id.design_bottom_sheet)).setState(STATE_EXPANDED);
        });
        return bottomSheetDialog;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.cached_delivery_address_dialog, viewGroup, false);

        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(DelieveryAddressViewModel.class);
        binding.setViewModel(viewModel);

        initialization();
        return view;
    }

    public void onResume() {
        super.onResume();
        itemList.clear();

        if (!sharedPref.isGuest()) {
            savedAddress.clear();
            savedAddress.addAll(sharedPref.getUser().getDeliveryAddresses());
            cachedAddressesAdapter.notifyDataSetChanged();
            viewModel.setHaveAddress();

            for (AddAddressModel addressModel : sharedPref.getSavedDeliveryAddress()) {
                if (!savedAddress.contains(addressModel))
                    itemList.add(addressModel);
            }
        } else
            itemList.addAll(sharedPref.getSavedDeliveryAddress());

        adapter.notifyDataSetChanged();
        selectedAddressPoisiton = itemList.indexOf(sharedPref.getSelectedCachedArea());
        viewModel.setHaveDeliveryArea(itemList.isEmpty());
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override
    public void setupDialog(Dialog dialog, int n) {
        super.setupDialog(dialog, n);
        View view = View.inflate(this.getContext(), R.layout.cached_delivery_address_dialog, null);
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
        selectAddressCallBack.onSelect(addAddressModel);
        dismiss();
    }

    @Override
    public void onItemClickListener(int position, boolean isAddress) {
        dismiss();
        AddAddressModel area = isAddress ? savedAddress.get(position) : itemList.get(position);

        if (isWithinOrderingFlow) {
            selectAddressCallBack.onSelect(area);
            dismiss();
            return;
        }

        selectedAddressPoisiton = position;
        this.isSavedAddress = isAddress;

        if (sharedPref.getCart() != null) {
            if (area.getArea().get_id() != null)
                viewModel.checkIfNotDelivering(sharedPref.getCart().getId(), area.getArea().get_id(), this);
            else {
                String message = getString(R.string.your_cart_contain_items_hint) + " " +
                        sharedPref.getCart().getPlaceModel().getName().getEn() + getString(R.string.this_place_not_deliver_hint) + " " +
                        (isAddress ? savedAddress.get(position).getArea().getName().getEn() :
                                itemList.get(position).getArea().getName().getEn())
                        + ". " + getString(R.string.clear_cart);

                ((BaseActivity) mContext).showClearCartDialog(sharedPref.getCart().getPlaceModel().getName().getEn(), () -> {
                    selectedAddressPoisiton = position;
                    this.isSavedAddress = isAddress;
                    updateCachedData();
                }, message);
            }
            return;
        }
        updateCachedData();
    }

    @Override
    public void onDeleteListener(int position, boolean isAddress) {
        showDeleteConfirmialog(position, isAddress);
    }

    public void showDeleteConfirmialog(int position, boolean isAddress) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        DialogUpdateBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_update, null, false);

        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();

        binding.notNowTv.setText(getString(R.string.cancel));
        binding.updateNowTv.setText(getString(R.string.yes_delete));
        binding.contentTv.setText(getString(R.string.delete_address_msg));
        binding.titleTv.setText(getString(R.string.delete_address_title));

        binding.notNowTv.setOnClickListener(view -> {
            dialog.dismiss();
        });

        binding.updateNowTv.setOnClickListener(view -> {
            dialog.dismiss();

            if (isAddress && !savedAddress.isEmpty()) {
                viewModel.deleteAddress(savedAddress.get(position));
            } else {
                AddAddressModel deletedAddressModel = itemList.get(position);
                itemList.remove(deletedAddressModel);

                if (savedAddress.isEmpty() && itemList.isEmpty()) {
                    restartApp((BaseActivity) mContext);
                } else if (deletedAddressModel.getArea().getLocation() != null &&
                        sharedPref.getSelectedCachedArea().getArea().getLocation() != null &&
                        sharedPref.getSelectedCachedArea().getArea().getLocation().getCoordinates().toString()
                                .equals(deletedAddressModel.getArea().getLocation().getCoordinates().toString()) && itemList.isEmpty()) {
                    sharedPref.setSelectedCachedArea(savedAddress.get(savedAddress.size() - 1));
                    selectAddressCallBack.onSelect(savedAddress.get(savedAddress.size() - 1));
                    sharedPref.setSavedDeliveryAddress(savedAddress);
                } else if (itemList.isEmpty()) {
                    sharedPref.setSelectedCachedArea(savedAddress.get(savedAddress.size() - 1));
                    selectAddressCallBack.onSelect(savedAddress.get(savedAddress.size() - 1));
                    sharedPref.setSavedDeliveryAddress(itemList);
                } else {
                    sharedPref.setSelectedCachedArea(itemList.get(itemList.size() - 1));
                    selectAddressCallBack.onSelect(itemList.get(itemList.size() - 1));
                    sharedPref.setSavedDeliveryAddress(itemList);
                }
                adapter.notifyDataSetChanged();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();
    }

    @Override
    public void onBackClickListener() {
    }

    public void setSelectAddressCallBack(SelectAddressCallBack selectAddressCallBack) {
        this.selectAddressCallBack = selectAddressCallBack;
    }

    public boolean isWithinOrderingFlow() {
        return isWithinOrderingFlow;
    }

    public void setWithinOrderingFlow(boolean withinOrderingFlow) {
        isWithinOrderingFlow = withinOrderingFlow;
    }
}

