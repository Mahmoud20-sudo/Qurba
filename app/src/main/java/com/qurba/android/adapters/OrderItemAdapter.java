package com.qurba.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qurba.android.R;
import com.qurba.android.databinding.ItemOrderOfferBinding;
import com.qurba.android.network.models.CartItems;
import com.qurba.android.ui.checkout.view_models.OrdersViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.SharedPreferencesManager;

import java.text.NumberFormat;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private BaseActivity activity;
    private List<CartItems> cartItemsList;
    private LayoutInflater inflater;
    private boolean isCreatingOrder;
    private SharedPreferencesManager sharePref = SharedPreferencesManager.getInstance();

    public OrderItemAdapter(BaseActivity activity, List<CartItems> cartItemsList, boolean... isCreatingOrder) {
        inflater = LayoutInflater.from(activity);
        this.cartItemsList = cartItemsList;
        this.activity = activity;
        this.isCreatingOrder = isCreatingOrder.length != 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_order_offer, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CartItems cartItems = cartItemsList.get(position);
        OrdersViewModel ordersViewModel = new OrdersViewModel(activity, cartItems, position);
        String ingredients = ordersViewModel.setDatangredient();
        holder.itemBinding.titleTv.setText(cartItems.getTitle().getEn());
        holder.itemBinding.offerQtyTv.setText(NumberFormat.getInstance().format(cartItems.getQuantity()));
        holder.itemBinding.offerPriceTv.setText(sharePref.getLanguage().equalsIgnoreCase("ar") ? NumberFormat.getInstance().format(cartItems.getPrice()) + " " + activity.getString(R.string.currency) : activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(cartItems.getPrice()));
        Glide.with(activity).load(cartItems.getImageUrl()).placeholder(R.mipmap.offer_details_place_holder).into(holder.itemBinding.itemImageIv);
//        ((LinearLayout.LayoutParams)holder.itemBinding.itemImageIv.getLayoutParams()).weight = isCreatingOrder ? 1.0f : 0.0f;
//        ((LinearLayout.LayoutParams)holder.itemBinding.offerPriceTv.getLayoutParams()).weight = isCreatingOrder ? 1.0f : 2.0f;
//        holder.itemBinding.itemImageIv.setVisibility(isCreatingOrder ? View.GONE : View.VISIBLE);

        if (!ingredients.isEmpty())
            holder.itemBinding.descriptionTv.setText(ingredients.charAt(ingredients.length() - 1) != '-'
                    ? ingredients : ingredients.substring(0, ingredients.length() - 1));
    }

    @Override
    public int getItemCount() {
        return cartItemsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderOfferBinding itemBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }
}