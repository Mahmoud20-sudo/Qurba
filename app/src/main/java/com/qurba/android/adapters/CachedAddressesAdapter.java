package com.qurba.android.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.qurba.android.R;
import com.qurba.android.databinding.ItemSavedAddressBinding;
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.ui.add_edit_address.views.AddAddressActivity;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.ItemClickEvents;
import com.qurba.android.utils.SelectAddressCallBack;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.List;

public class CachedAddressesAdapter extends RecyclerView.Adapter<CachedAddressesAdapter.SavedAddressesHolder> {

    private final LayoutInflater inflater;
    private List<AddAddressModel> savedAddresses;
    private BaseActivity activity;
    private boolean isAreaData;
    private ItemClickEvents itemsClickEvents;
    private PopupWindow popup;
    private SharedPreferencesManager sharedPref;
    private int popupedPosition = -1;
    private SelectAddressCallBack selectAddressCallBack;

    public ItemClickEvents getItemsClickEvents() {
        return itemsClickEvents;
    }

    public void setItemsClickEvents(ItemClickEvents itemsClickEvents) {
        this.itemsClickEvents = itemsClickEvents;
    }

    //
    public CachedAddressesAdapter(BaseActivity activity, List<AddAddressModel> savedAddresses, SelectAddressCallBack selectAddressCallBack) {
        this.savedAddresses = savedAddresses;
        this.activity = activity;
        this.selectAddressCallBack = selectAddressCallBack;
        sharedPref = SharedPreferencesManager.getInstance();
        inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        setPopup();
    }

    @NonNull
    @Override
    public SavedAddressesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_saved_address, parent, false);
        return new SavedAddressesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedAddressesHolder holder, int position) {
        AddAddressModel addAddressModel = savedAddresses.get(position);
        holder.itemBinding.itemLabelTv.setText(addAddressModel.getLabel(activity));
        holder.itemBinding.itemDetailsTv.setText(addAddressModel.getStreet() +
                ", " + activity.getString(R.string.building_hint) + " " + addAddressModel.getBuilding() + ", " +
                activity.getString(R.string.floor_hint) + " " + addAddressModel.getFloor() + ", " +
                activity.getString(R.string.flat_hint) + " " + addAddressModel.getFlat());
        holder.itemBinding.itemDetailsTv.setGravity(sharedPref.getLanguage().equalsIgnoreCase("ar") ? Gravity.END : Gravity.START);

        switch (addAddressModel.getLabel()) {
            case "Home":
                holder.itemBinding.addressIcon.setImageResource(R.drawable.ic_baseline_home_24);
                break;
            case "Work":
                holder.itemBinding.addressIcon.setImageResource(R.drawable.ic_work);
                break;
            default:
                holder.itemBinding.addressIcon.setImageResource(R.drawable.ic_pin_drop_24_px);
        }

        holder.itemView.setOnClickListener(v -> itemsClickEvents.onItemClickListener(position, true));

        holder.itemBinding.itemActionsIv.setOnClickListener(v -> {
            popupedPosition = position;
            popup.showAsDropDown(v, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90
                    , activity.getResources().getDisplayMetrics()), -v.getHeight());
        });
    }

    private void setPopup() {
        View popupView = inflater.inflate(R.layout.popup_address, null);
        popup = new PopupWindow(popupView);
        popup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popup.setElevation(10);
        popup.setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, activity.getResources().getDisplayMetrics()));
        popup.setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 85, activity.getResources().getDisplayMetrics()));
        popupView.bringToFront();

        popupView.findViewById(R.id.edit_linear_ll).setOnClickListener(v -> {

            Intent intent = new Intent(activity, AddAddressActivity.class);

            intent.putExtra(Constants.ADDRESS, new Gson().toJson(savedAddresses.get(popupedPosition)));
            activity.startActivityForResult(intent, 34131);
            popup.dismiss();
            popupedPosition = -1;
        });

        popupView.findViewById(R.id.delete_linear_ll).setOnClickListener(v -> {
            itemsClickEvents.onDeleteListener(popupedPosition, true);
            popup.dismiss();
            popupedPosition = -1;
        });
    }

    @Override
    public int getItemCount() {
        return savedAddresses.size();
    }

    class SavedAddressesHolder extends RecyclerView.ViewHolder {

        ItemSavedAddressBinding itemBinding;

        SavedAddressesHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }
}