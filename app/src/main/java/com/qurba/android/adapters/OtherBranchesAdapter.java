package com.qurba.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qurba.android.R;
import com.qurba.android.databinding.ItemOtherBranchesBinding;
import com.qurba.android.network.models.NearbyPlaceModel;
import com.qurba.android.network.models.UserDataModel;
import com.qurba.android.ui.place_details.view_models.OtherBranchesItemViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class OtherBranchesAdapter extends RecyclerView.Adapter<OtherBranchesAdapter.ViewHolder> {

    private BaseActivity activity;
    private List<NearbyPlaceModel> places;
    private LayoutInflater inflater;

    public OtherBranchesAdapter(BaseActivity activity, List<NearbyPlaceModel> places) {
        inflater = LayoutInflater.from(activity);
        this.places = places;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_other_branches, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //OffersModel offersModel = offersModels.get(position);
        ViewHolder viewHolder = holder;
        OtherBranchesItemViewModel commentItemViewModel = new OtherBranchesItemViewModel(activity, places.get(position));
        viewHolder.itemBinding.setViewModel(commentItemViewModel);

        Glide.with(activity).load(places.get(position).getPlaceProfilePictureUrl())
                .placeholder(R.drawable.grey_circle).into(viewHolder.itemBinding.placeImageIv);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOtherBranchesBinding itemBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }
}