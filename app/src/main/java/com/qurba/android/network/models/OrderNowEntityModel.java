package com.qurba.android.network.models;
import java.io.Serializable;

public class OrderNowEntityModel implements Serializable {

    private String type;
    private OffersModel offer;
    private ProductData product;

    public String getEntityType() {
        return type;
    }

    public void setEntityType(String entityType) {
        this.type = entityType;
    }

    public ProductData getProductData() {
        return product;
    }

    public void setProductData(ProductData product) {
        this.product = product;
    }

    public OffersModel getOffer() {
        return offer;
    }

    public void setOffer(OffersModel offer) {
        this.offer = offer;
    }
}
