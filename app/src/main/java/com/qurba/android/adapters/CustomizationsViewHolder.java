package com.qurba.android.adapters;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.qurba.android.databinding.ItemCustomizationBinding;
import com.qurba.android.databinding.ItemCustomizationBindingImpl;

public class CustomizationsViewHolder extends RecyclerView.ViewHolder {

    public ItemCustomizationBinding itemBinding;

    CustomizationsViewHolder(View itemView) {
        super(itemView);
        itemBinding = DataBindingUtil.bind(itemView);
    }
}