package com.qurba.android.network.models;

import com.qurba.android.network.models.response_models.ProductsListResponse;

import java.util.List;


public class ProductsPayload {

    private Products products;
    private Offer offer;
    private List<ProductData> docs;

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public List<ProductData> getDocs() {
        return docs;
    }

    public void setDocs(List<ProductData> docs) {
        this.docs = docs;
    }

    public class Products {
        private List<ProductData> docs;

        public List<ProductData> getDocs() {
            return docs;
        }

        public void setDocs(List<ProductData> docs) {
            this.docs = docs;
        }
    }

    public class Offer {
        private String _id;
        private float discountRatio;
        private String endDate;
        private PlaceDescriptionModel message;

        public PlaceDescriptionModel getMessage() {
            return message;
        }

        public void setMessage(PlaceDescriptionModel message) {
            this.message = message;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public float getDiscountRatio() {
            return discountRatio;
        }

        public void setDiscountRatio(float discountRatio) {
            this.discountRatio = discountRatio;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }
}
