package com.qurba.android.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qurba.android.R;
import com.qurba.android.databinding.ItemMyOrderBinding;
import com.qurba.android.network.models.OrdersModel;
import com.qurba.android.ui.my_orders.view_models.MyOrderItemViewModel;
import com.qurba.android.ui.order_status.views.OrderStatusActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;

import java.util.List;

//import com.nguyenhoanglam.imagepicker.model.Image;
//import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    private List<OrdersModel> ordersModels;
    private LayoutInflater inflater;

    public MyOrdersAdapter(Activity activity, List<OrdersModel> ordersModels) {
        inflater = LayoutInflater.from(activity);
        this.ordersModels = ordersModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_my_order, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrdersModel ordersModel = ordersModels.get(position);

        ViewHolder viewHolder = holder;
        MyOrderItemViewModel myOrderItemViewModel = new MyOrderItemViewModel(viewHolder.itemView.getContext(), ordersModel);
        viewHolder.itemBinding.setViewModel(myOrderItemViewModel);

        if (ordersModel.getPlaceInfo() != null)
            Glide.with(viewHolder.itemView.getContext()).load(ordersModel.getPlaceInfo().getPlaceProfilePictureUrl())
                    .into(viewHolder.itemBinding.itemOrderAvatarIv);

        viewHolder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(QurbaApplication.getContext(), OrderStatusActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.ORDER_ID, ordersModel.get_id());
            QurbaApplication.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ordersModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemMyOrderBinding itemBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);

        }
    }
}
