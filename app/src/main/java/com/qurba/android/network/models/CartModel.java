package com.qurba.android.network.models;


import com.qurba.android.R;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.SharedPreferencesManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CartModel {

    private String id;
    private String hashKey;
    //private String placeId;
//    private PlaceDescriptionModel placeName;
    //  private List<OffersModel> offersModels;
    private String placeProductsName;
    private List<CartItems> cartItems;

    private List<ClassZyada34anTar2> offers;
    private List<ClassZyada34anTar2> products;

    private String unique_name;
    private long timeStamp;
    private PlaceModel place;
    private String note;
    private PlaceDescriptionModel branchName;

    private float totalPrice;
    private float deliveryFees;
    private float totalSavings;
    private float VAT;
    private boolean isVATIncluded;
    private boolean isCartValid;
    private float vatPrice;
    private float subTotalPrice;

    private List<PlaceDescriptionModel> cartErrors;

    public boolean isVATIncluded() {
        return isVATIncluded;
    }

    public String getLocalizedVat(BaseActivity activity) {
        return SharedPreferencesManager.getInstance()
                .getLanguage().equalsIgnoreCase("ar") ? NumberFormat.getInstance().format(vatPrice) + " " + activity.getString(R.string.currency) : activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(vatPrice);
    }

    public String getLocalizedSubTotal(BaseActivity activity) {
        return SharedPreferencesManager.getInstance()
                .getLanguage().equalsIgnoreCase("ar") ? NumberFormat.getInstance().format(subTotalPrice) + " " + activity.getString(R.string.currency) : activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(subTotalPrice);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBranchName(PlaceDescriptionModel branchName) {
        this.branchName = branchName;
    }

//    public List<OffersModel> getOffersModels() {
//        return offersModels;
//    }
//
//    public void setOffersModels(List<OffersModel> offersModels) {
//        this.offersModels = offersModels;
//    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public String getPlaceProductsName() {
        return placeProductsName;
    }

    public void setPlaceProductsName(String placeProductsName) {
        this.placeProductsName = placeProductsName;
    }

    public List<CartItems> getCartItems() {
        return cartItems;
    }

    private List<CartItems> setItemsData() {
        List<CartItems> items = new ArrayList<>();

        return items;
    }

    public void setCartItems(List<CartItems> cartItems) {
        this.cartItems = cartItems;
    }

    public String getUnique_name() {
        return unique_name;
    }

    public void setUnique_name(String unique_name) {
        this.unique_name = unique_name;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public PlaceModel getPlaceModel() {
        return place;
    }

    public void setPlaceModel(PlaceModel placeModel) {
        this.place = placeModel;
    }

    public String getSpecialNote() {
        return note;
    }

    public void setSpecialNote(String specialNote) {
        this.note = specialNote;
    }

    public List<ClassZyada34anTar2> getOffers() {
        return offers;
    }

    public void setOffers(List<ClassZyada34anTar2> offers) {
        this.offers = offers;
    }

    public List<ClassZyada34anTar2> getProducts() {
        return products;
    }

    public void setProducts(List<ClassZyada34anTar2> products) {
        this.products = products;
    }

    public boolean getIsVATIncluded() {
        return isVATIncluded;
    }

    public void setIsVATIncluded(boolean isVATIncluded) {
        this.isVATIncluded = isVATIncluded;
    }

    public float getVAT() {
        return VAT;
    }

    public int getVatPrice() {
        return Math.round(vatPrice);
    }

    public int getTotalSavings() {
        return Math.round(totalSavings);
    }

    public int getTotalPrice() {
        return Math.round(totalPrice);
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getDeliveryFees() {
        return Math.round(deliveryFees);
    }

    public String getLocalizedDeliveryFees(BaseActivity activity) {
        return SharedPreferencesManager.getInstance()
                .getLanguage().equalsIgnoreCase("ar") ? NumberFormat.getInstance().format(deliveryFees) + " " + activity.getString(R.string.currency) : activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(deliveryFees);
    }

    public void setDeliveryFees(float deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    public List<PlaceDescriptionModel> getCartErrors() {
        return cartErrors;
    }

    public class ClassZyada34anTar2{
        private OffersModel offer;
        private ProductData product;
        private String _id;
        private List<Sections> sections;
        private List<SectionsGroup> sectionGroups;
        private int quantity;
        private float price;
        private float totalOfferPrice;
        private float totalProductPrice;

        private float singleOfferPrice;
        private float singleProductPrice;

        public ProductData getProduct() {
            return product;
        }

        public void setProduct(ProductData product) {
            this.product = product;
        }

        public OffersModel getOffer() {
            return offer;
        }

        public void setOffer(OffersModel offer) {
            this.offer = offer;
        }

        public int getTotalOfferPrice() {
            return Math.round(totalOfferPrice);
        }

        public void setTotalOfferPrice(float totalOfferPrice) {
            this.totalOfferPrice = totalOfferPrice;
        }

        public int getPrice() {
            return Math.round(price);
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public List<SectionsGroup> getSectionGroups() {
            return sectionGroups;
        }

        public void setSectionGroups(List<SectionsGroup> sectionGroups) {
            this.sectionGroups = sectionGroups;
        }

        public List<Sections> getSections() {
            return sections;
        }

        public void setSections(List<Sections> sections) {
            this.sections = sections;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int getTotalProductPrice() {
            return Math.round(totalProductPrice);
        }

        public void setTotalProductPrice(float totalProductPrice) {
            this.totalProductPrice = totalProductPrice;
        }

        public int getSingleProductPrice() {
            return Math.round(singleProductPrice);
        }

        public void setSingleProductPrice(float singleProductPrice) {
            this.singleProductPrice = singleProductPrice;
        }

        public int getSingleOfferPrice() {
            return Math.round(singleOfferPrice);
        }

        public void setSingleOfferPrice(float singleOfferPrice) {
            this.singleOfferPrice = singleOfferPrice;
        }
    }
}
