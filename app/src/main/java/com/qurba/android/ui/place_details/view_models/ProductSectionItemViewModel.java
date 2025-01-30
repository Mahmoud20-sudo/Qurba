package com.qurba.android.ui.place_details.view_models;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;

import com.qurba.android.network.models.response_models.ProductsListResponse;
import com.qurba.android.utils.BaseActivity;

public class ProductSectionItemViewModel extends ViewModel implements Observable{

    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();
    private ProductsListResponse productSection;
    private BaseActivity activity;

    public ProductSectionItemViewModel(BaseActivity activity, ProductsListResponse productSection) {
        this.productSection = productSection;
        this.activity = activity;
    }

    @Bindable
    public String getName() {
        return productSection.getName().getEn();
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

}