package com.qurba.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qurba.android.R;
import com.qurba.android.databinding.ItemRecentSearchesBinding;
import com.qurba.android.network.models.response_models.DeliveryAreaResponse;
import com.qurba.android.utils.ItemClickEvents;
import java.util.ArrayList;

public class DeliveryAreasAdapter extends RecyclerView.Adapter<DeliveryAreasAdapter.RecentSearchViewHolder> {

    private final LayoutInflater inflater;
    private ArrayList<DeliveryAreaResponse> deliveryAreaResponses;
    private boolean isAreaData;
    private ItemClickEvents itemsClickEvents;
    private boolean isFromAddress;

    public void setItemsClickEvents(ItemClickEvents itemsClickEvents) {
        this.itemsClickEvents = itemsClickEvents;
    }

    public DeliveryAreasAdapter(Context context, ArrayList<DeliveryAreaResponse> deliveryAreaResponses) {
        this.deliveryAreaResponses = deliveryAreaResponses;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecentSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recent_searches, parent, false);
        return new RecentSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentSearchViewHolder holder, int position) {
        holder.itemBinding.nameTv.setText(deliveryAreaResponses.get(position).getName().getEn());
        holder.itemBinding.backIv.setOnClickListener(v -> itemsClickEvents.onBackClickListener());
        holder.itemView.setOnClickListener(v -> itemsClickEvents.onItemClickListener(position, isAreaData));

        if (isAreaData) {
            if ((position == 0)) {
                holder.itemBinding.backIv.setVisibility(View.VISIBLE);
            } else {
                holder.itemBinding.backIv.setVisibility(View.GONE);
            }
        }
        else
            holder.itemBinding.backIv.setVisibility(View.GONE);

        holder.itemBinding.deleteIv.setVisibility(isFromAddress ? View.GONE : View.VISIBLE);
    }

    public void setIsAreaData(boolean isAreaData) {
        this.isAreaData = isAreaData;
    }

    public boolean isAreaData() {
        return isAreaData;
    }

    @Override
    public int getItemCount() {
        return deliveryAreaResponses.size();
    }

    public void setFromAddress(boolean fromAddress) {
        isFromAddress = fromAddress;
    }

    class RecentSearchViewHolder extends RecyclerView.ViewHolder {

        ItemRecentSearchesBinding itemBinding;

        RecentSearchViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }
}
