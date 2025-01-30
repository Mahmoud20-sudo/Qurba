package com.qurba.android.network.models;

import android.content.Context;

import com.qurba.android.R;
import com.qurba.android.network.models.google_places_models.Place;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.SharedPreferencesManager;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PlaceModel implements Serializable {

    private PlaceDescriptionModel name;
    private PlaceDescriptionModel branchName;
    private String uniqueName;
    private String _id;
    private List<PlaceCategoryModel> categories;
    private String placeProfilePictureUrl;//
    private String PlaceProfilePicture;//
    private String mobileNumber;//
    private String hotLineNumber;//
    private String phoneNumber;
    private String telephoneNumber;
    private List<String> placeGalleryPicturesUrls;
    private PlaceDescriptionModel availability;
    private CommentModel recentComment;
    private String deepLink;
    private String placeProfileCoverPictureUrl;

    private boolean isDeliveringToArea;
    private boolean openNow;
    private boolean isLikedByUser;
    private boolean hasOpeningTimes;
    private PlaceDescriptionModel message;
    private boolean isPlaceOpen;

    private int likesCount;
    private int clicksCount;
    private int ordersCount;
    private int sharesCount;
    private int commentsCount;

    private int deliveryTime;
    private boolean isPlaceBusy;

    private float deliveryFees;
    private boolean orderable;

    private double VAT;
    private boolean isVATIncluded;

    private List<SubCategory> subCategories;

    public PlaceDescriptionModel getName() {
        return name == null ? new PlaceDescriptionModel() : name;
    }

    public void setName(PlaceDescriptionModel name) {
        this.name = name;
    }

    public PlaceDescriptionModel getBranchName() {
        return branchName == null ? new PlaceDescriptionModel() : branchName;
    }

    public void setBranchName(PlaceDescriptionModel branchName) {
        this.branchName = branchName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<PlaceCategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(List<PlaceCategoryModel> categories) {
        this.categories = categories;
    }

    public String getPlaceProfilePictureUrl() {
        return placeProfilePictureUrl;
    }

    public void setPlaceProfilePictureUrl(String placeProfilePictureUrl) {
        this.placeProfilePictureUrl = placeProfilePictureUrl;
    }

    public String getLocalizedLikesCount(BaseActivity activity) {
        if (likesCount == 1 || likesCount > 10)
            return NumberFormat.getInstance().format(likesCount) + " " + activity.getString(R.string.like_hint);
        else
            return NumberFormat.getInstance().format(likesCount) + " " + activity.getString(R.string.likes);
    }

    public String getLocalizedCommentsCount() {
        return NumberFormat.getInstance().format(commentsCount);
    }

    public String getLocalizedSharesCount() {
        return NumberFormat.getInstance().format(sharesCount);
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public String getPlaceProfilePicture() {
        return PlaceProfilePicture;
    }

    public void setPlaceProfilePicture(String placeProfilePicture) {
        PlaceProfilePicture = placeProfilePicture;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getHotLineNumber() {
        return hotLineNumber;
    }

    public void setHotLineNumber(String hotLineNumber) {
        this.hotLineNumber = hotLineNumber;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public boolean isHasOpeningTimes() {
        return hasOpeningTimes;
    }

    public void setHasOpeningTimes(boolean hasOpeningTimes) {
        this.hasOpeningTimes = hasOpeningTimes;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public PlaceDescriptionModel getMessage() {
        return message;
    }

    public void setMessage(PlaceDescriptionModel message) {
        this.message = message;
    }

    public List<String> getPlaceGalleryPicturesUrls() {
        return placeGalleryPicturesUrls;
    }

    public void setPlaceGalleryPicturesUrls(List<String> placeGalleryPicturesUrls) {
        this.placeGalleryPicturesUrls = placeGalleryPicturesUrls;
    }

    public PlaceDescriptionModel getAvailability() {
        return availability;
    }

    public void setAvailability(PlaceDescriptionModel availability) {
        this.availability = availability;
    }

    public boolean isLikedByUser() {
        return isLikedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        isLikedByUser = likedByUser;
    }

    public boolean isDeliveringToArea() {
        return isDeliveringToArea;
    }

    public void setDeliveringToArea(boolean deliveringToArea) {
        isDeliveringToArea = deliveringToArea;
    }

    public CommentModel getRecentComment() {
        return recentComment;
    }

    public void setRecentComment(CommentModel recentComment) {
        this.recentComment = recentComment;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
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

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public boolean isPlaceOpen() {
        return isPlaceOpen;
    }

    public void setPlaceOpen(boolean placeOpen) {
        isPlaceOpen = placeOpen;
    }

    public String getPlaceProfileCoverPictureUrl() {
        return placeProfileCoverPictureUrl;
    }

    public void setPlaceProfileCoverPictureUrl(String placeProfileCoverPictureUrl) {
        this.placeProfileCoverPictureUrl = placeProfileCoverPictureUrl;
    }

    public String getDeliveryTime() {
        return NumberFormat.getInstance().format(deliveryTime >= 60 ? deliveryTime / 60 : deliveryTime);
    }

    private boolean isMoreThanHour() {
        return deliveryTime / 60 > 1 ? true : false;
    }

    public String getTimeLabel(Context context) {
        return getDeliveryTime() + " " + (deliveryTime < 60 ? context.getString(R.string.minutes) : (isMoreThanHour() ? context.getString(R.string.hours) : context.getString(R.string.hour)));
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public boolean isPlaceBusy() {
        return isPlaceBusy;
    }

    public void setPlaceBusy(boolean placeBusy) {
        isPlaceBusy = placeBusy;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PlaceModel) {
            return this._id.equalsIgnoreCase(((PlaceModel) o).get_id());
        } else return false;
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    public int getDeliveryFees() {
        return Math.round(deliveryFees);
    }

    public String getDeliveryFees(Context activity) {
        return SharedPreferencesManager.getInstance().getLanguage().equals("ar") ?
                NumberFormat.getInstance().format(getDeliveryFees()) + " " + activity.getString(R.string.currency) :
                activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(getDeliveryFees());
    }


    public void setDeliveryFees(float deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }

    public boolean isVATIncluded() {
        return isVATIncluded;
    }

    public void setVATIncluded(boolean VATIncluded) {
        isVATIncluded = VATIncluded;
    }

    public double getVAT() {
        return VAT;
    }

    public void setVAT(double VAT) {
        this.VAT = VAT;
    }

    public int getCalculatedVAT(int subTotal) {
        return (int) (VAT * subTotal);
    }

    public String getLocalizedVat(BaseActivity activity, int subTotal) {
        return SharedPreferencesManager.getInstance()
                .getLanguage().equalsIgnoreCase("ar") ? NumberFormat.getInstance().format(getCalculatedVAT(subTotal)) + " " + activity.getString(R.string.currency) : activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(getCalculatedVAT(subTotal));
    }
}
