package com.qurba.android.network.models;

import android.content.Context;

import com.qurba.android.R;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.SharedPreferencesManager;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrdersModel implements Serializable {

    private float totalPrice;
    private String _id;
    private AddAddressModel deliveryAddress;
    private PlaceModel place;
    private List<OrdersOffers> offers;
    private float deliveryFees;
    private String transactionID;
    private List<OrderHistory> history;
    private String createdAt;
    private String updatedAt;
    private String id;
    private String note;
    private Payment payment;
    private String status;
    private List<OrderProducts> products;
    private String offerName;
    private boolean current;
    private float totalSavings;
    private User user;
    private double VAT;
    private float vatPrice;
    private float subTotalPrice;

    public double getVAT() {
        return VAT;
    }

    public void setVAT(double VAT) {
        this.VAT = VAT;
    }

    public String getLocalizedVat(BaseActivity activity) {
        return SharedPreferencesManager.getInstance()
                .getLanguage().equalsIgnoreCase("ar") ? NumberFormat.getInstance().format(vatPrice) + " " + activity.getString(R.string.currency) : activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(vatPrice);
    }

    public int getTotalPrice() {
        return Math.round(totalPrice);
    }

    public String getTotalPrice(Context activity) {
        return SharedPreferencesManager.getInstance().getLanguage().equals("ar") ?
                NumberFormat.getInstance().format(getTotalPrice()) + " " + activity.getString(R.string.currency) :
                activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(getTotalPrice());
    }

    public String getVAT(Context activity) {
        return SharedPreferencesManager.getInstance().getLanguage().equals("ar") ?
                NumberFormat.getInstance().format(getPlaceInfo().getVAT() * 100) + " " + activity.getString(R.string.currency) :
                activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(getPlaceInfo().getVAT()  * 100);
    }

    public String getTotalSavings(Context activity) {
        return SharedPreferencesManager.getInstance().getLanguage().equals("ar") ?
                NumberFormat.getInstance().format(getTotalSavings()) + " " + activity.getString(R.string.currency) :
                activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(getTotalSavings());
    }

    public String getDeliveryFees(Context activity) {
        return SharedPreferencesManager.getInstance().getLanguage().equals("ar") ?
                NumberFormat.getInstance().format(getDeliveryFees()) + " " + activity.getString(R.string.currency) :
                activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(getDeliveryFees());
    }


    public String getSubTotal(Context activity) {
        return SharedPreferencesManager.getInstance().getLanguage().equals("ar") ?
                NumberFormat.getInstance().format(subTotalPrice) + " " + activity.getString(R.string.currency) :
                activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(subTotalPrice);
    }

    public int getSubTotal() {
        return getTotalPrice() - getDeliveryFees();
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public AddAddressModel getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(AddAddressModel deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<OrdersOffers> getOffers() {
        return offers;
    }

    public void setOffers(List<OrdersOffers> offers) {
        this.offers = offers;
    }

    public int getDeliveryFees() {
        return Math.round(deliveryFees);
    }

    public void setDeliveryFees(int deliveryFees) {
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

    public PlaceModel getPlaceInfo() {
        return place;
    }

    public void setPlaceInfo(PlaceModel placeInfo) {
        this.place = placeInfo;
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

    public int getTotalSavings() {
        return Math.round(totalSavings);
    }

    public void setTotalSavings(int totalSavings) {
        this.totalSavings = totalSavings;
    }

    public User getUser() {
        return user;
    }

    public String getNote() {
        return note != null ? note.trim() : "";
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getVatPrice() {
        return Math.round(vatPrice);
    }

    public void setVatPrice(float vatPrice) {
        this.vatPrice = vatPrice;
    }

    public class User {
        private String _id;
        private String mobile;
        private String firstName;
        private String lastName;

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMobile() {
            return mobile != null ? String.format(new Locale(SharedPreferencesManager.getInstance().getLanguage()), "%d", Long.parseLong(
                    mobile.replace("", ""))) : "";
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }
}
