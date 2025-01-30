package com.qurba.android.ui.order_status.views;

import static com.qurba.android.utils.extenstions.ExtesionsKt.setGradientTextColor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.qurba.android.R;
import com.qurba.android.adapters.OrderItemAdapter;
import com.qurba.android.databinding.ActivityOrderStatusBinding;
import com.qurba.android.network.models.CartItems;
import com.qurba.android.network.models.OrderHistory;
import com.qurba.android.network.models.OrderProducts;
import com.qurba.android.network.models.OrdersModel;
import com.qurba.android.network.models.OrdersOffers;
import com.qurba.android.network.models.SectionItems;
import com.qurba.android.network.models.Sections;
import com.qurba.android.network.models.SectionsGroup;
import com.qurba.android.ui.order_status.view_models.OrderConfirmationViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;

import java.util.ArrayList;
import java.util.List;

public class OrderStatusActivity extends BaseActivity {

    private ActivityOrderStatusBinding binding;
    private OrderConfirmationViewModel viewModel;
    private List<CartItems> cartItemsList = new ArrayList<>();
    private OrderItemAdapter rcAdapter;
    LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
        initializeObservables();
    }

    private void initialization() {
        viewModel = new ViewModelProvider(this).get(OrderConfirmationViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_status);
        binding.setViewModel(viewModel);
        viewModel.setActivity(this);

        binding.orderOffersRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.orderOffersRv.setLayoutManager(linearLayoutManager);
        rcAdapter = new OrderItemAdapter(this, cartItemsList);
        binding.orderOffersRv.setAdapter(rcAdapter);

        if (getIntent().hasExtra("is-creating")) {
            viewModel.setOrderCreated(true);
        }

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewModel.getOrderDetails(getIntent().getExtras().getString(Constants.ORDER_ID));
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
        setGradientTextColor( binding.totalSavingTv);
        setGradientTextColor( binding.totalSavingSavingTv);
    }

    private void initializeObservables() {
        viewModel.getOrdersModelObservable().observe(this, this::publishOrder);
    }

    private void publishOrder(OrdersModel ordersModel) {
        if (ordersModel == null)
            viewModel.setDataFound(false);
        else {
            viewModel.setOrderObject(ordersModel);
            viewModel.setDataFound(true);
            setOrderIngredients(ordersModel);
        }
    }

    private void setOrderIngredients(OrdersModel ordersModel) {
        if (ordersModel.getPlaceInfo() != null)
            Glide.with(this).load(ordersModel.getPlaceInfo().getPlaceProfilePictureUrl())
                    .placeholder(R.mipmap.offer_details_place_holder).into(binding.placeImageIv);

        if (ordersModel.getStatus().equalsIgnoreCase("pending"))
            viewModel.deliveryTime.set(getString(R.string.order_pending));
        else
            viewModel.deliveryTime.set(String.valueOf(Html.fromHtml(getText(R.string.estimated_time) + " " + ordersModel.getPlaceInfo().getTimeLabel(this) + "")));

        int price = 0;

        for (OrdersOffers offers : ordersModel.getOffers()) {
            CartItems cartItems = new CartItems();
            cartItems.setImageUrl(offers.getOffer().getPictureUrl());
            cartItems.setQuantity(offers.getQuantity());
            cartItems.set_id(offers.getOffer().get_id());
            cartItems.setTitle(offers.getOffer().getTitle());
            cartItems.setPrice(offers.getPrice());
            List List = new ArrayList();

            for (Sections offerIngradients : offers.getSections()) {
                Sections sections = new Sections();
                sections.setTitle(offerIngradients.getTitle());
                sections.set_id(offerIngradients.get_id());
                List sectionItemsList = new ArrayList();

                for (SectionItems responseItems : offerIngradients.getItems()) {
                    SectionItems sectionItem = new SectionItems();
                    sectionItem.set_id(responseItems.get_id());
                    sectionItem.setTitle(responseItems.getTitle());
                    sectionItemsList.add(sectionItem);
                }

                sections.setItems(sectionItemsList);
                List.add(sections);
                cartItems.setSections(List);
            }

            price += offers.getPrice();
            this.cartItemsList.add(cartItems);
        }

        if (ordersModel.getProductData() != null) {
            for (OrderProducts orderProducts : ordersModel.getProductData()) {
                CartItems cartItems = new CartItems();
                cartItems.setImageUrl(orderProducts.getProduct().getImageURL());
                cartItems.set_id(orderProducts.getProduct().get_id());
                cartItems.setQuantity(orderProducts.getQuantity());
                cartItems.setPrice(orderProducts.getPrice());
                cartItems.setTitle(orderProducts.getProduct().getName());
                List List = new ArrayList();

                if (orderProducts.getSectionGroups() != null)
                    for (SectionsGroup sectionsGroup : orderProducts.getSectionGroups()) {
                        for (Sections sec : sectionsGroup.getSections()) {
                            sec.setSectionsGroup(sectionsGroup);
                            orderProducts.getSections().add(sec);
                        }
                    }

                for (Sections productSecions : orderProducts.getSections()) {
                    Sections sections = new Sections();
                    sections.setTitle(productSecions.getTitle());
                    sections.set_id(productSecions.get_id());
                    sections.setSectionsGroup(productSecions.getSectionsGroup());

                    List sectionItemsList = new ArrayList();

                    for (SectionItems responseItems : productSecions.getItems()) {
                        SectionItems sectionItem = new SectionItems();
                        sectionItem.set_id(responseItems.get_id());
                        sectionItem.setTitle(responseItems.getTitle());
                        sectionItemsList.add(sectionItem);
                    }

                    sections.setItems(sectionItemsList);
                    List.add(sections);
                    cartItems.setSections(List);
                }

                price += orderProducts.getPrice();
                this.cartItemsList.add(cartItems);
            }
        }

        for (int i = 0; i < ordersModel.getHistory().size(); i++) {
            OrderHistory history = ordersModel.getHistory().get(i);

            View view = layoutInflater.inflate(R.layout.item_order_status, null);
            TextView orderStatusTxt = view.findViewById(R.id.order_status_tv);
            ImageView orderStatusImg = view.findViewById(R.id.order_status_iv);
            View divider = view.findViewById(R.id.divider);

            switch (history.getStatus()) {
                case "PENDING":
                    viewModel.orderStatusFull.set(getString(R.string.pending));
                    continue;
                case "RECEIVED":
                    orderStatusTxt.setText(getString(R.string.order_recieved_status));
                    orderStatusImg.setImageResource(R.drawable.ic_received_active);
                    viewModel.setRecieved(true);
                    viewModel.orderStatusFull.set(getString(R.string.order_recieved));
                    break;
                case "PREPARING":
                    orderStatusTxt.setText(getString(R.string.order_preparing_status));
                    orderStatusImg.setImageResource(R.drawable.ic_preparing_active);
                    viewModel.setPrepared(true);
                    viewModel.orderStatusFull.set(getString(R.string.order_preparing));
                    break;
                case "DELIVERING":
                    orderStatusTxt.setText(getString(R.string.order_delivery_status));
                    orderStatusImg.setImageResource(R.drawable.ic_delivery_active);
                    viewModel.setDelivering(true);
                    viewModel.orderStatusFull.set(getString(R.string.order_out_for_delivery));
                    break;
                case "COMPLETED":
                    orderStatusTxt.setText(getString(R.string.order_complete_status));
                    orderStatusImg.setImageResource(R.drawable.ic_completed_active);
                    viewModel.setCompleted(true);
                    viewModel.orderStatusFull.set(getString(R.string.order_complete));
                    break;
                case "CANCELLED":
//                    orderStatusTxt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    orderStatusTxt.setText(getString(R.string.order_canceled_status));
                    orderStatusImg.setImageResource(R.drawable.ic_baseline_cancel_24);
                    viewModel. setTypeFace(Typeface.BOLD);
                    viewModel.setCancelable(false);
                    viewModel.setCancled(true);
                    viewModel.orderStatusFull.set(getString(R.string.order_canceled));
                    break;
            }

            divider.setVisibility(i == ordersModel.getHistory().size() - 1 ? View.GONE : View.VISIBLE);
            binding.orderStatusContainer.addView(view);
        }

//        ObservableField observableField = this.viewModel.orderPrice;
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("EGP ");
//        stringBuilder.append(price);
//        observableField.set(stringBuilder.toString());
        this.rcAdapter.notifyDataSetChanged();

        viewModel.setTotalSavings(ordersModel.getTotalSavings() > 0);
        viewModel.setHaveVAT(ordersModel.getVatPrice() > 0f);

        if (ordersModel.getDeliveryFees() == 0) {
            binding.deliveryFeesTv.setTypeface(null, Typeface.BOLD);
            viewModel.fees.set(getString(R.string.free_delivery_hint));
            setGradientText();
        }
    }

    private void setGradientText() {
        TextPaint paint = binding.deliveryFeesTv.getPaint();
        float width = paint.measureText(getString(R.string.free_delivery_hint));
        Shader textShader = new LinearGradient(0, 0, width, binding.deliveryFeesTv.getTextSize(),
                new int[]{Color.parseColor("#fa6400"), Color.parseColor("#db1f30")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        binding.deliveryFeesTv.getPaint().setShader(textShader);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //viewModel.getLastOrder();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        QurbaApplication.setCurrentActivity(null);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.REFRESH_ORDER_STATUS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        QurbaApplication.setCurrentActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
