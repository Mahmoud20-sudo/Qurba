package com.qurba.android.network.models;

import java.io.Serializable;
import java.util.List;

public class MyOrdersModel implements Serializable {

    private float totalPrice;
    private String _id;
    private DeliveryAddress deliveryAddress;
    private OrderPlaceInfo placeInfo;
    private List<Offers> offers;
    private float deliveryFees;
    private String transactionID;
    private List<OrderHistory> history;
    private String createdAt;
    private String updatedAt;
    private String id;
    private Payment payment;
    private String status;
    private List<OrderProducts> products;

    private String cancellationReason;
    private String offerName;
    private boolean current;

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<Offers> getOffers() {
        return offers;
    }

    public void setOffers(List<Offers> offers) {
        this.offers = offers;
    }

    public float getDeliveryFees() {
        return deliveryFees;
    }

    public void setDeliveryFees(float deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrderPlaceInfo getPlaceInfo() {
        return placeInfo;
    }

    public void setPlaceInfo(OrderPlaceInfo placeInfo) {
        this.placeInfo = placeInfo;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public List<OrderHistory> getHistory() {
        return history;
    }

    public void setHistory(List<OrderHistory> history) {
        this.history = history;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getCancellation_reason() {
        return cancellationReason;
    }

    public void setCancellation_reason(String cancellation_reason) {
        this.cancellationReason = cancellation_reason;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public List<OrderProducts> getProductData() {
        return products;
    }

    public void setProductData(List<OrderProducts> productData) {
        this.products = productData;
    }
}
