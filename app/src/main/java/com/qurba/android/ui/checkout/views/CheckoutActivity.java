package com.qurba.android.ui.checkout.views;

import static com.qurba.android.utils.extenstions.ExtesionsKt.setGradientTextColor;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.qurba.android.R;
import com.qurba.android.adapters.OrderItemAdapter;
import com.qurba.android.databinding.ActivityCreateOrderBinding;
import com.qurba.android.network.models.CartItems;
import com.qurba.android.network.models.CartModel;
import com.qurba.android.ui.add_edit_address.views.AddAddressActivity;
import com.qurba.android.ui.checkout.view_models.CheckoutViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckoutActivity extends BaseActivity{

    private ActivityCreateOrderBinding binding;

    private CheckoutViewModel viewModel;
    private SharedPreferencesManager sharePref = SharedPreferencesManager.getInstance();

    private String blockCharacterSet = " , .";

    private InputFilter filter = (source, start, end, dest, dstart, dend) -> {

        if (source.equals("")) { // for backspace
            return source;
        }
        if (source.toString().matches("[a-zA-Z0-9 ]+")) {
            return source;
        }
        if (blockCharacterSet.contains(("" + source))) {
            return source;
        }
        return "";
    };
    private GoogleMap googleMap;
    private List<CartItems> listItems = new ArrayList<>();
    private OrderItemAdapter rcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

        viewModel.setActivity(this);
        viewModel.setPreviousIntent(getIntent());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_order);
        binding.setCreateOrderVM(viewModel);
        binding.mapView.onCreate(savedInstanceState);

        initialization();
    }

    private void initialization() {
        sharePref.clearCart();//re-synch cart with backend
        viewModel.setLoading(true);
        binding.mapView.getMapAsync(mMap -> {
            googleMap = mMap;
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext()
                    , R.raw.uber_style));

            if (Objects.requireNonNull(sharePref.getSelectedCachedArea().getArea()).getLocation() != null) {
                LatLng latLng = new LatLng(sharePref.getSelectedCachedArea().getArea().getLocation().getCoordinates().get(1),
                        sharePref.getSelectedCachedArea().getArea().getLocation().getCoordinates().get(0));
                moveMapToCurrentLocation(latLng);
            }
        });
        initAdapter();
        initListeners();
        setGradientTextColor(binding.totalSavingTv);
        setGradientTextColor(binding.totalSavingValueTv);
    }

    private void showAddressSheet() {
        Intent intent = new Intent(this, AddAddressActivity.class);
        intent.putExtra(Constants.PLACE_ID, sharePref.getCart().getId());
        intent.putExtra(Constants.IS_ORDERING, true);
        intent.putExtra(Constants.ADDRESS, new Gson().toJson(sharePref.getSelectedCachedArea()));
        startActivity(intent);
    }

    private void initAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.orderOffersRv.setLayoutManager(linearLayoutManager);
        rcAdapter = new OrderItemAdapter(this, listItems, true);
        binding.orderOffersRv.setAdapter(rcAdapter);
    }

    private void initData(CartModel cartModel) {
        sharePref.setCart(cartModel, true);
        addAll(cartModel.getCartItems());
        viewModel.note.set(cartModel.getSpecialNote());
        binding.orderOffersRv.setHasFixedSize(true);

        viewModel.orderName.set(cartModel.getPlaceModel().getName().getEn());
        viewModel.logCheckoutOrPurchaseEvent(false, "");

        if (viewModel.isLostFreeDelivery() || cartModel.getDeliveryFees() == 0) {
            binding.deliveryFeesTv.setTypeface(null, Typeface.BOLD);
            viewModel.deliveryFees.set(getString(R.string.free_delivery_hint));
            setGradientTextColor(binding.deliveryFeesTv);
        }

        viewModel.setOrderTotalPrice(cartModel);
    }

    private void initListeners() {
        binding.animToolbar.setNavigationOnClickListener(view -> onBackPressed());
        binding.editAddressTv.setOnClickListener(v -> showAddressSheet());
    }

    @Override
    protected void onPause() {
        super.onPause();
        QurbaApplication.setCurrentActivity(null);
        binding.mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        QurbaApplication.setCurrentActivity(this);
        viewModel.setMobileNumber();
        viewModel.setAddressType();
        viewModel.setAddress();
        new Handler(Looper.getMainLooper()).postDelayed(this::run, 100);
        viewModel.getCart(this, cartModel -> {
            clearData();
            viewModel.setLoading(false);
            if (cartModel.getProducts() == null && cartModel.getOffers() == null) {
                finish();
                return null;
            }
            initData(cartModel);
            return null;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    private void moveMapToCurrentLocation(LatLng latLng) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);

//        if (googleMap != null) {
//            MarkerOptions options = new MarkerOptions();
//            options.position(latLng);
//            BitmapDescriptor icon =
//                    BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_icon);
//            options.icon(icon);

        if (googleMap != null) {
            googleMap.clear();
            BitmapDescriptor icon =
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_icon);
            options.icon(icon);
            googleMap.addMarker(options);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void run() {
        binding.mapView.onResume();
    }

    public void clearData(){
        listItems.clear();
        rcAdapter.notifyDataSetChanged();
    }

    public void addAll(List<CartItems> postItems) {
        if (postItems != null && postItems.size() > 0) {
            for (CartItems response : postItems) {
                add(response);
            }
        }
    }

    public void add(CartItems response) {
        if (!listItems.contains(response)) {
            listItems.add(response);
            rcAdapter.notifyItemInserted(listItems.size() - 1);
        }
    }
}
