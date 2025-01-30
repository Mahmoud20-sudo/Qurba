package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.AddressDetailsModel;
import com.qurba.android.network.models.LastAddressModel;
import com.qurba.android.network.models.PlaceDescriptionModel;

public class AddressModel {

    private LastAddressModel payload;

    public LastAddressModel getAddress() {
        return payload;
    }

    public void setAddress(LastAddressModel payload) {
        this.payload = payload;
    }
}
