package com.qurba.android.network.models;

import com.qurba.android.R;
import com.qurba.android.utils.BaseActivity;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductData implements Serializable {

    private boolean hasSections;
    private boolean orderable;
    private boolean isActive;
    private boolean isMainProduct;
    private String _id;
    private String uniqueName;
    private PlaceDescriptionModel name;
    private PlaceDescriptionModel description;
    private String imageURL;
    private PlaceModel place;
    private float beforePrice;//
    private float price;//beforePrice
    private int quantity;
    private int likesCount;
    private String hashKey;
    private boolean isLikedByUser;
    private List<Sections> sections;
    private boolean isDiscountOnMenu;
    private boolean isDeliveringToArea;
    private boolean isModuleAvailable;
    private boolean isPlaceOpen;
    private String deepLink;
    private int deliveryFees;
    private String pricing;

    private int numberOfComments;
    private int numberOfOrders;
    private int numberOfShares;

    private int clicksCount;
    private int ordersCount;
    private int sharesCount;
    private int commentsCount;

    private float basePrice;

    private List<CommentModel> comments;
    private float __v;
    private String module;
    private Sections category;
    private List<SectionsGroup> sectionGroups;
    private PlaceDescriptionModel availability;
    private List<PlaceModel> deliveringBranches;

    private CommentModel recentComment;

    public String getDeepLink() {
        return deepLink;
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isMainProduct() {
        return isMainProduct;
    }

    public void setMainProduct(boolean mainProduct) {
        isMainProduct = mainProduct;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public PlaceDescriptionModel getName() {
        return name;
    }

    public void setName(PlaceDescriptionModel name) {
        this.name = name;
    }

    public PlaceDescriptionModel getDescription() {
        return description;
    }

    public void setDescription(PlaceDescriptionModel description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getPrice() {
        return Math.round(price);
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float get__v() {
        return __v;
    }

    public void set__v(float __v) {
        this.__v = __v;
    }

    public float getBeforePrice() {
        return beforePrice;
    }

    public void setBeforePrice(float beforePrice) {
        this.beforePrice = beforePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public PlaceModel getPlaceInfo() {
        return place == null ? new PlaceModel() : place;
    }

    public void setPlaceInfo(PlaceModel placeInfo) {
        this.place = placeInfo;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public String getLocalizedLikesCount(BaseActivity activity) {
//        1 إعجاب
//        2 - 10 إعجابات
//        11- إعجاب
        if (likesCount == 1 || likesCount > 10)
            return NumberFormat.getInstance().format(likesCount) +" " + activity.getString(R.string.like_hint);
        else
            return NumberFormat.getInstance().format(likesCount)+" " + activity.getString(R.string.likes);
    }

    public String getLocalizedCommentsCount() {
        return NumberFormat.getInstance().format(commentsCount);
    }

    public String getLocalizedSharesCount() {
        return NumberFormat.getInstance().format(sharesCount);
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isProductLikedByUser() {
        return isLikedByUser;
    }

    public void setProductLikedByUser(boolean productLikedByUser) {
        isLikedByUser = productLikedByUser;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public List<Sections> getSections() {
        return sections;
    }

    public void setSections(List<Sections> sections) {
        this.sections = sections;
    }

    public boolean isDiscountOnMenu() {
        return isDiscountOnMenu;
    }

    public void setDiscountOnMenu(boolean discountOnMenu) {
        isDiscountOnMenu = discountOnMenu;
    }

    public String getShareLink() {
        return deepLink;
    }

    public void setShareLink(String shareLink) {
        this.deepLink = shareLink;
    }

    public int getDeliveryFees() {
        return deliveryFees;
    }

    public void setDeliveryFees(int deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    public String getPricing() {
        return pricing;
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public List<CommentModel> getProductCommentsSample() {
        return comments == null ? new ArrayList<>() : comments;
    }

    public void setProductCommentsSample(List<CommentModel> productCommentsSample) {
        this.comments = productCommentsSample;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int productCommentsCount) {
        this.commentsCount = productCommentsCount;
    }

    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(int numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public int getNumberOfShares() {
        return numberOfShares;
    }

    public void setNumberOfShares(int numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    public boolean isHassections() {
        return hasSections;
    }

    public void setHassections(boolean hassections) {
        this.hasSections = hassections;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Sections getCategory() {
        return category;
    }

    public void setCategory(Sections category) {
        this.category = category;
    }

    public boolean isDeliveringToArea() {
        return isDeliveringToArea;
    }

    public void setDeliveringToArea(boolean deliveringToArea) {
        isDeliveringToArea = deliveringToArea;
    }

    public boolean isPlaceOpen() {
        return isPlaceOpen;
    }

    public void setPlaceOpen(boolean placeOpen) {
        isPlaceOpen = placeOpen;
    }

    public boolean isModuleAvailable() {
        return isModuleAvailable;
    }

    public void setModuleAvailable(boolean moduleAvailable) {
        isModuleAvailable = moduleAvailable;
    }

    public int getClicksCount() {
        return clicksCount;
    }

    public void setClicksCount(int clicksCount) {
        this.clicksCount = clicksCount;
    }

    public int getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(int ordersCount) {
        this.ordersCount = ordersCount;
    }

    public int getSharesCount() {
        return sharesCount;
    }

    public void setSharesCount(int sharesCount) {
        this.sharesCount = sharesCount;
    }

    public List<SectionsGroup> getSectionGroups() {
        return sectionGroups;
    }

    public void setSectionGroups(List<SectionsGroup> sectionGroups) {
        this.sectionGroups = sectionGroups;
    }

    public CommentModel getRecentComment() {
        return recentComment;
    }

    public void setRecentComment(CommentModel recentComment) {
        this.recentComment = recentComment;
    }

    public PlaceDescriptionModel getAvailability() {
        return availability;
    }

    public void setAvailability(PlaceDescriptionModel availability) {
        this.availability = availability;
    }

    public int getBasePrice() {
        return Math.round(basePrice);
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public List<PlaceModel> getDeliveringBranches() {
        return deliveringBranches;
    }

    public void setDeliveringBranches(List<PlaceModel> deliveringBranches) {
        this.deliveringBranches = deliveringBranches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductData)) return false;
        if (_id.equals(((ProductData) o)._id)) return true;

        return false;
    }


}


