package com.qurba.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qurba.android.R;
import com.qurba.android.databinding.ItemPlaceFacilityBinding;
import com.qurba.android.network.models.PlaceCategoryModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.SharedPreferencesManager;


import java.util.List;

public class FacilitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PlaceCategoryModel> facilities;
    private final LayoutInflater inflater;
    private BaseActivity activity;

    public FacilitiesAdapter(BaseActivity activity, List<PlaceCategoryModel> facilities) {
        this.facilities = facilities;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_place_facility, parent, false);
        return new FacilitiesPlaceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FacilitiesPlaceHolder facilitiesPlaceHolder = (FacilitiesPlaceHolder) holder;
        facilitiesPlaceHolder.itemBinding.facilityItemTv.setText(facilities.get(position).getName().getEn());
        Glide.with(activity).load(facilities.get(position).getMobileIconUrl()).into(facilitiesPlaceHolder.itemBinding.facilityItemIv);
    }

    @Override
    public int getItemCount() {
        return facilities.size();
    }

    class FacilitiesPlaceHolder extends RecyclerView.ViewHolder {

        ItemPlaceFacilityBinding itemBinding;

        FacilitiesPlaceHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }
}