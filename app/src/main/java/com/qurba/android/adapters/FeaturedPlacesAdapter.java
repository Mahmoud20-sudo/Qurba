package com.qurba.android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.qurba.android.R;
import com.qurba.android.databinding.ItemFeaturedPlaceBinding;
import com.qurba.android.network.models.PlaceModel;
import com.qurba.android.utils.BaseActivity;

import java.util.List;
public class FeaturedPlacesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private List<PlaceModel> placeModels;
    private BaseActivity activity;
    private int selectedPosition = -1;

    public FeaturedPlacesAdapter(BaseActivity activity, List<PlaceModel> placeModels) {
        this.placeModels = placeModels;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_featured_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlaceViewHolder placeViewHolder = (PlaceViewHolder) holder;
        Glide.with(activity).load(placeModels.get(position).getPlaceProfilePictureUrl()).placeholder(R.mipmap.offer_details_place_holder).into(placeViewHolder.itemBinding.itemPlaceImageIv);
        placeViewHolder.itemBinding.itemPlaceImageTv.setText(placeModels.get(position).getName().getEn() + " - " + placeModels.get(position).getBranchName().getEn());
        placeViewHolder.itemBinding.itemPlaceImageIv.setAlpha(selectedPosition != position ? 1.0f : 0.5f);
        placeViewHolder.itemBinding.itemPlaceImageTv.setAlpha(selectedPosition != position ? 1.0f : 0.5f);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return placeModels.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public void add(PlaceModel response) {
        if (!placeModels.contains(response)) {
            placeModels.add(response);
            notifyItemInserted(placeModels.size() - 1);
        }
    }

    public void addAll(List<PlaceModel> postItems) {
        if (postItems != null && postItems.size() > 0) {
            for (PlaceModel response : postItems) {
                add(response);
            }
        }
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {

        ItemFeaturedPlaceBinding itemBinding;

        PlaceViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);

        }
    }

}