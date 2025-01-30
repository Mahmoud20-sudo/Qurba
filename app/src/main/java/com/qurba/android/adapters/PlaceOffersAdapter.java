package com.qurba.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qurba.android.R;
import com.qurba.android.databinding.ItemOfferPlaceOfferBinding;
import com.qurba.android.databinding.ItemOrderNowOrdersBinding;
import com.qurba.android.network.models.OffersModel;
import com.qurba.android.network.models.OrdersModel;
import com.qurba.android.network.models.PlaceModel;
import com.qurba.android.ui.my_orders.view_models.MyOrderItemViewModel;
import com.qurba.android.ui.offers.view_models.OfferItemViewModel;
import com.qurba.android.ui.order_status.views.OrderStatusActivity;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.DateUtils;
import com.qurba.android.utils.SharedPreferencesManager;
import java.util.ArrayList;
import java.util.List;

public class PlaceOffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private List<OffersModel> offers;
    private ArrayList<OrdersModel> orders = new ArrayList<>();

    private BaseActivity activity;
    private static final int TYPE_OFFER = 1;
    private static final int TYPE_ORDER = 2;
    public static final int ORDER_DISPLAY_FREQUENCY = 2;
    private PlaceModel placeModel;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();

    public PlaceOffersAdapter(BaseActivity activity, List<OffersModel> offers) {
        this.offers = offers;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setPlaceInfo(PlaceModel place) {
        placeModel = place;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ORDER:
                View view1 = inflater.inflate(R.layout.item_order_now_orders, parent, false);
                return new OrderViewHolder(view1);
            default:
                View view = inflater.inflate(R.layout.item_offer_place_offer, parent, false);
                return new OfferViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < ORDER_DISPLAY_FREQUENCY && position < orders.size()) {
            return TYPE_ORDER;
        }
        return TYPE_OFFER;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_OFFER:
                OfferViewHolder offerViewHolder = (OfferViewHolder) holder;
                OfferItemViewModel offerItemViewModel = new OfferItemViewModel(activity, offers.get(holder.getAdapterPosition()),false);
                offerItemViewModel.setPlaceInfo(placeModel != null ? placeModel : offers.get(position).getPlaceId());

                offerViewHolder.itemBinding.setViewModel(offerItemViewModel);
                Glide.with(activity).load(offers.get(position).getPictureUrl()).placeholder(R.mipmap.offer_details_place_holder).into(offerViewHolder.itemBinding.offerImageIv);
                offerViewHolder.itemBinding.setPosition(position);
                DateUtils.getShortDateFromTimeStamp(offers.get(position).getEndDate());
                offerViewHolder.itemBinding.offerOldPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                setIcon(offers.get(position).isOrderable(), offerViewHolder);

                break;
            case TYPE_ORDER:
                OrdersModel ordersModel = orders.get(position);
                OrderViewHolder viewHolder = (OrderViewHolder) holder;
                MyOrderItemViewModel myOrderItemViewModel = new MyOrderItemViewModel(activity, ordersModel);
                viewHolder.itemBinding.setViewModel(myOrderItemViewModel);

                viewHolder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(activity, OrderStatusActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.ORDER_ID, ordersModel.get_id());
                    activity.startActivity(intent);
                });
                break;
        }
    }

    private void setIcon(boolean orderable, OfferViewHolder offerViewHolder){
        Drawable imgRes = orderable ? activity.getResources().getDrawable(R.drawable.ic_grey_clock) : activity.getResources().getDrawable(R.drawable.ic_closed);
        offerViewHolder.itemBinding.availabilityTv.setCompoundDrawablesWithIntrinsicBounds(
                sharedPref.getLanguage().equalsIgnoreCase("ar") ? null : imgRes,
                null,
                sharedPref.getLanguage().equalsIgnoreCase("en") ? null : imgRes,
                null);
    }

    public void addOrders(List<OrdersModel> postItems) {
        if (!postItems.isEmpty()) {
            orders.addAll(postItems);
            notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {

        ItemOfferPlaceOfferBinding itemBinding;

        OfferViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        ItemOrderNowOrdersBinding itemBinding;

        OrderViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }

    }

    public void add(OffersModel response) {
        if (!offers.contains(response)) {
            offers.add(response);
            notifyItemInserted(offers.size() - 1);
        }
    }

    public void addAll(List<OffersModel> postItems) {
        if (postItems != null && postItems.size() > 0) {
            for (OffersModel response : postItems) {
                add(response);
            }
        }
    }

    public void updateOffer(int position, OffersModel offersModel) {
        this.offers.set(position, offersModel);
        this.notifyItemChanged(position);
    }

    public void clearAll() {
        offers.clear();
        notifyDataSetChanged();
    }


//    private void loadNativeAd(AdViewHolder holder) {
//        NativeAd nativeAd = new NativeAd(activity, activity.getString(R.string.placement_id));
//        nativeAd.setAdListener(new NativeAdListener() {
//            @Override
//            public void onMediaDownloaded(Ad ad) {
//
//            }
//
//            @Override
//            public void onError(Ad ad, AdError adError) {
//
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                inflateAd(nativeAd, holder);
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//
//            }
//        });
//
//        nativeAd.loadAd();
//    }

//    private void inflateAd(NativeAd nativeAd, AdViewHolder holder) {
//
//        holder.itemAdNearbyBinding.nativeAdTitle.setText(nativeAd.getAdvertiserName());
//        holder.itemAdNearbyBinding.nativeAdSocialContext.setText(nativeAd.getAdBodyText());
//        holder.itemAdNearbyBinding.nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
//        holder.itemAdNearbyBinding.nativeAdSponsoredLabel.setText(R.string.sponsored);
//        holder.itemAdNearbyBinding.nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
//
//        holder.itemAdNearbyBinding.nativeAdCallToAction.setVisibility(
//                nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
//        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, holder.itemAdNearbyBinding.container);
//        holder.itemAdNearbyBinding.container.addView(adOptionsView, 0);
//
//        List<View> clickableViews = new ArrayList<>();
//        clickableViews.add(holder.itemAdNearbyBinding.nativeAdMediaView);
//        clickableViews.add(holder.itemAdNearbyBinding.nativeAdCallToAction);
//        nativeAd.registerViewForInteraction(holder.itemAdNearbyBinding.container, holder.itemAdNearbyBinding.nativeAdMediaView, clickableViews);
//
//    }
}