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
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.utils.ItemClickEvents;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.List;

public class CachedDeliveryAdapter extends RecyclerView.Adapter<CachedDeliveryAdapter.RecentSearchViewHolder> {

    private final LayoutInflater inflater;
    private List<AddAddressModel> deliveryAreaResponses;
    private Context context;
    private boolean isAreaData;
    private ItemClickEvents itemsClickEvents;
    private SharedPreferencesManager sharedPref;

    public ItemClickEvents getItemsClickEvents() {
        return itemsClickEvents;
    }

    public void setItemsClickEvents(ItemClickEvents itemsClickEvents) {
        this.itemsClickEvents = itemsClickEvents;
    }

    public CachedDeliveryAdapter(Context context, List<AddAddressModel> deliveryAreaResponses) {
        this.deliveryAreaResponses = deliveryAreaResponses;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sharedPref = SharedPreferencesManager.getInstance();
    }

    @NonNull
    @Override
    public RecentSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recent_searches, parent, false);
        return new RecentSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentSearchViewHolder holder, int position) {
        holder.itemBinding.nameTv.setText(deliveryAreaResponses.get(position).getCity().getName().getEn() + ", "
                + deliveryAreaResponses.get(position).getArea().getName().getEn());
        holder.itemBinding.backIv.setOnClickListener(v -> itemsClickEvents.onBackClickListener());
        holder.itemBinding.deleteIv.setOnClickListener(v -> itemsClickEvents.onDeleteListener(position ,false));
        holder.itemView.setOnClickListener(v -> itemsClickEvents.onItemClickListener(position ,false));

        holder.itemBinding.recentIv.setVisibility(View.VISIBLE);

        if (isAreaData)
            if (position == 0)
                holder.itemBinding.backIv.setVisibility(View.VISIBLE);
            else
                holder.itemBinding.backIv.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return deliveryAreaResponses.size();
    }

    class RecentSearchViewHolder extends RecyclerView.ViewHolder {

        ItemRecentSearchesBinding itemBinding;

        RecentSearchViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }
}
