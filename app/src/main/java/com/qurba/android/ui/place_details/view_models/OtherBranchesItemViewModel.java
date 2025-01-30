package com.qurba.android.ui.place_details.view_models;

import android.content.Intent;
import android.view.View;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;

import com.qurba.android.network.models.NearbyPlaceModel;

import com.qurba.android.ui.place_details.views.PlaceDetailsActivity;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.SharedPreferencesManager;

public class OtherBranchesItemViewModel extends ViewModel implements Observable {

    private final NearbyPlaceModel place;
    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    private BaseActivity activity;

    public OtherBranchesItemViewModel(BaseActivity activity, NearbyPlaceModel place) {
        this.place = place;
        this.activity = activity;
    }

    @Bindable
    public String getPlaceName() {
        return this.place.getName().getEn() + (place.getBranchName() != null ? " - " + place.getBranchName().getEn() : "");
    }

    @Bindable
    public String getPlaceAddress() {
        String address = "";

        if(place.getAddress() == null)
            return address;

        if (place.getAddress() != null && place.getAddress().getFirstLine() != null && !place.getAddress().getFirstLine().getEn().equals("NA")
                && !place.getAddress().getFirstLine().getAr().equals("NA")) {
            if (SharedPreferencesManager.getInstance().getLanguage().equals("en")) {
                address = place.getAddress().getFirstLine().getEn() + " "
                        + (place.getAddress().getArea() == null ? ""
                        : place.getAddress().getArea().getName().getEn());
            } else {
                address = place.getAddress().getFirstLine().getAr() + " "
                        + place.getAddress().getArea().getName().getAr();
            }
        } else {
            if (place.getAddress().getArea().getName().getEn() != null) {
                address = place.getAddress().getArea().getName().getEn();
            } else {
                address = "";
            }
        }
        return address;
    }

    public View.OnClickListener openPlaceDetails() {
        return v -> {
            Intent intent = new Intent(activity, PlaceDetailsActivity.class);
            if (SharedPreferencesManager.getInstance().getLanguage().equals("en")) {
                intent.putExtra("place_name", place.getName().getEn());
            } else {
                intent.putExtra("place_name", place.getName().getAr());
            }
            intent.putExtra("unique_name", place.getUniqueName());
            intent.putExtra("place_id", place.get_id());
//            intent.putExtra("product-tab-name", place.getCategories().get(0).getProductsTapName());
            intent.putExtra(Constants.ORDER_TYPE, Constants.OFFERS);
            activity.startActivity(intent);
            return;
        };
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