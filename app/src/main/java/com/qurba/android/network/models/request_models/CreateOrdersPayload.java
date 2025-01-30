package com.qurba.android.network.models.request_models;

import com.qurba.android.network.models.DeliveryAddress;
import com.qurba.android.network.models.OfferIngradients;
import com.qurba.android.network.models.Offers;
import com.qurba.android.network.models.OrderProductsRequests;
import com.qurba.android.network.models.Payment;
import com.qurba.android.network.models.SectionsGroupRequests;

import java.util.List;

public class CreateOrdersPayload {

    private DeliveryAddress deliveryAddress;
    private String place;
    private List<Offers> offers;
    private List<OrderProductsRequests> products;
    private Payment payment;
    private String note;
    private String area;
    private String offer;
    private String product;

    private List<OfferIngradients> sections;
    private List<SectionsGroupRequests> sectionGroups;

    private transient int totalPrice;
    private transient int cartSize;

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public List<Offers> getOffers() {
        return offers;
    }

    public void setOffers(List<Offers> offers) {
        this.offers = offers;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<OrderProductsRequests> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProductsRequests> products) {
        this.products = products;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getCartSize() {
        return cartSize;
    }

    public void setCartSize(int cartSize) {
        this.cartSize = cartSize;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public List<SectionsGroupRequests> getSectionGroups() {
        return sectionGroups;
    }

    public void setSectionGroups(List<SectionsGroupRequests> sectionGroups) {
        this.sectionGroups = sectionGroups;
    }

    public List<OfferIngradients> getSections() {
        return sections;
    }

    public void setSections(List<OfferIngradients> sections) {
        this.sections = sections;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}
