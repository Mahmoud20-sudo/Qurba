package com.qurba.android.network.models;

import android.content.Context;

import androidx.annotation.Nullable;

import com.qurba.android.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class OffersModel{

    private String uniqueName;
    private List<String> galleryUrls;
    //    private TimedOfferDetails timedOfferDetails;
    private CommentModel recentComment;

    private boolean hasSections;
    private List<Sections> sections;
    private PlaceLocation placeLocation;
    private int likesCount;
    private int clicksCount;
    private int ordersCount;
    private int sharesCount;
    private int commentsCount;
    private int claimsCount;
    private String _id;
    private PlaceDescriptionModel description;
    private PlaceDescriptionModel title;
    private double discountRatio;
    private String startDate;
    private String type;
    private String endDate;
    private String pictureUrl;
    private PlaceModel place;
    private String PMId;
    private String createdAt;
    private String updatedAt;
    private String __v;
    private String status;
    private String id;
    private boolean isLikedByUser;
    private PlaceDescriptionModel openingTimes;
    //    private boolean isClaimedByUser;
//    private List<OffersModel> moreOffersForThisPlace;
    private PlaceDescriptionModel termsAndConditions;
    private float price;
    private float beforePrice;
    private boolean orderable;
    private boolean isDeliveringToArea;
    private boolean isOfferAvailable;
    private boolean isModuleAvailable;

    private boolean isPlaceOpen;
    private PlaceDescriptionModel message;
    private PlaceDescriptionModel availability;

    private String hashKey;
    private String pricing;
    private int quantity;
    private boolean sponsored;
    private String deepLink;
    private PlaceModel placeInfo;
    private boolean isDiscountOnMenu;
    private int deliveryFees;
    private String module;
    private List<SectionsGroup> sectionGroups;
    private List<PlaceModel> deliveringBranches;
    private List<CommentModel> comments;

    private float basePrice;

    public transient boolean isFreeDeliveryCanceled;

    public boolean isFreeDeliveryCanceled() {
        return isFreeDeliveryCanceled;
    }

    public void setFreeDeliveryCanceled(boolean freeDeliveryCanceled) {
        isFreeDeliveryCanceled = freeDeliveryCanceled;
    }

    public String getPricing() {
        return pricing;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public List<String> getGalleryUrls() {
        return galleryUrls;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public String getLocalizedLikesCount() {
        return NumberFormat.getInstance().format(likesCount);
    }

    public String getLocalizedLikesCount(Context context) {
        if (likesCount == 1 || likesCount > 10)
            return NumberFormat.getInstance().format(likesCount) + " " + context.getString(R.string.like_hint);
        else
            return NumberFormat.getInstance().format(likesCount) + " " + context.getString(R.string.likes);
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

    public int getClaimsCount() {
        return claimsCount;
    }

    public void setClaimsCount(int claimsCount) {
        this.claimsCount = claimsCount;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description != null ? description.getEn() : "";
    }

    public void setDescription(PlaceDescriptionModel description) {
        this.description = description;
    }

    public PlaceDescriptionModel getTitle() {
        return title;
    }

    public void setTitle(PlaceDescriptionModel title) {
        this.title = title;
    }

    public double getDiscountRatio() {
        return discountRatio;
    }

    public void setDiscountRatio(double discountRatio) {
        this.discountRatio = discountRatio;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public PlaceModel getPlaceId() {
        return place == null ? new PlaceModel() : place;
    }

    public void setPlaceId(PlaceModel placeId) {
        this.place = placeId;
    }

    public String getPMId() {
        return PMId;
    }

    public void setPMId(String PMId) {
        this.PMId = PMId;
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

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLikedByUser() {
        return isLikedByUser;
    }

    public void setLikedByUser(boolean isLikedByUser) {
        this.isLikedByUser = isLikedByUser;
    }

//    public boolean isClaimedByUser() {
//        return isClaimedByUser;
//    }
//
//    public void setClaimedByUser(boolean claimedByUser) {
//        isClaimedByUser = claimedByUser;
//    }

//    public List<OffersModel> getMoreOffersForThisPlace() {
//        return moreOffersForThisPlace;
//    }

//    public void setMoreOffersForThisPlace(List<OffersModel> moreOffersForThisPlace) {
//        this.moreOffersForThisPlace = moreOffersForThisPlace;
//    }

    public String getTermsAndConditions() {
        return termsAndConditions != null ? termsAndConditions.getEn() : "";
    }
//
//    public void setTermsAndConditions(PlaceDescriptionModel termsAndConditions) {
//        this.termsAndConditions = termsAndConditions;
//    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof OffersModel) {
            if (((OffersModel) obj).get_id().equalsIgnoreCase(get_id()))
                return true;
        }
        return false;
    }

    public int getPrice() {
        return Math.round(price);
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isSponsored() {
        return sponsored;
    }

    public void setSponsored(boolean sponsored) {
        this.sponsored = sponsored;
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }

    public String getMessage() {
        return message != null ? message.getEn() : "";
    }

    public void setMessage(PlaceDescriptionModel message) {
        this.message = message;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<Sections> getSections() {
        return sections;
    }

    public void setSections(List<Sections> sections) {
        this.sections = sections;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public float getBeforePrice() {
        return Math.round(beforePrice);
    }

    public void setBeforePrice(int beforePrice) {
        this.beforePrice = beforePrice;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public boolean isDiscountOnMenu() {
        return isDiscountOnMenu;
    }

    public void setDiscountOnMenu(boolean discountOnMenu) {
        isDiscountOnMenu = discountOnMenu;
    }

    public int getDeliveryFees() {
        return deliveryFees;
    }

    public void setDeliveryFees(int deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    public boolean isHasSections() {
        return hasSections;
    }

    public void setHasSections(boolean hasSections) {
        this.hasSections = hasSections;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
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

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public PlaceDescriptionModel getOpeningTimes() {
        return openingTimes;
    }

    public void setOpeningTimes(PlaceDescriptionModel openingTimes) {
        this.openingTimes = openingTimes;
    }

    public boolean isOfferAvailable() {
        return isOfferAvailable;
    }

    public void setOfferAvailable(boolean offerAvailable) {
        isOfferAvailable = offerAvailable;
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

    public List<SectionsGroup> getSectionGroups() {
        return sectionGroups;
    }

    public void setSectionGroups(List<SectionsGroup> sectionGroups) {
        this.sectionGroups = sectionGroups;
    }

    public PlaceDescriptionModel getAvailability() {
        return availability;
    }

    public void setAvailability(PlaceDescriptionModel availability) {
        this.availability = availability;
    }

    public boolean isModuleAvailable() {
        return isModuleAvailable;
    }

    public void setModuleAvailable(boolean moduleAvailable) {
        isModuleAvailable = moduleAvailable;
    }

    public CommentModel getRecentComment() {
        return recentComment;
    }

    public void setRecentComment(CommentModel recentComment) {
        this.recentComment = recentComment;
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

    public List<CommentModel> getComments() {
        return comments == null ? new ArrayList<>() : comments;
    }
}
