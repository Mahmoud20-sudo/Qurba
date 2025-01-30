package com.qurba.android.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qurba.android.R;
import com.qurba.android.databinding.ItemPriceFilterBinding;
import com.qurba.android.databinding.ItemPriceFilterHeaderBinding;
import com.qurba.android.utils.BaseActivity;

public class OfferPricesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private final BaseActivity activity;
    private String[] filters;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;
    private int row_index = -1;
    private FilteringListeners filteringListeners;
    boolean isOverlay;

    public void setSelction(int row_index ) {
        this.row_index = row_index;
        notifyDataSetChanged();
    }


    public interface FilteringListeners {
        void onFilterClicked(int price);
    }

    public OfferPricesAdapter(BaseActivity activity, String[] filters, FilteringListeners filteringListeners, boolean... isOverlay) {
        this.filters = filters;
        this.activity = activity;
        this.isOverlay = isOverlay.length > 0 ? true : false;
        this.filteringListeners = filteringListeners;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                View view1 = inflater.inflate(R.layout.item_price_filter_header, parent, false);
                return new HeaderViewHolder(view1);
            default:
                View view2 = inflater.inflate(R.layout.item_price_filter, parent, false);
                return new ItemViewHolder(view2);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                break;
            case TYPE_ITEM:
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.itemBinding.priceTv.setText(filters[position] + " " +  activity.getString(R.string.currency));

                if (position == row_index) {
                    itemViewHolder.itemBinding.priceTv.setTextColor(Color.WHITE);//
                    itemViewHolder.itemBinding.priceTv.
                            setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d0021b")));
                } else {
                    itemViewHolder.itemBinding.priceTv.setTextColor(Color.parseColor("#747474"));//
                    itemViewHolder.itemBinding.priceTv.
                            setBackgroundTintList(null);
                }

                itemViewHolder.itemBinding.priceTv.setOnClickListener(view -> {
                    if (row_index == position) {
                        row_index = -1;
                        filteringListeners.onFilterClicked(0);
                        notifyDataSetChanged();
                    } else {
                        row_index = position;
                        filteringListeners.onFilterClicked(Integer.parseInt(filters[position]));
                        notifyDataSetChanged();
                    }
                });

                break;

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return filters.length;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && !isOverlay)
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        ItemPriceFilterBinding itemBinding;

        ItemViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        ItemPriceFilterHeaderBinding itemBinding;

        HeaderViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }


}
