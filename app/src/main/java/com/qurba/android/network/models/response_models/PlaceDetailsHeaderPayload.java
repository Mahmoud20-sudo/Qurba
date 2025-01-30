package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.CommentModel;
import com.qurba.android.network.models.PlaceCategoryModel;
import com.qurba.android.network.models.PlaceDescriptionModel;
import com.qurba.android.network.models.PlaceLocation;
import com.qurba.android.network.models.PlaceModel;
import java.util.List;

public class PlaceDetailsHeaderPayload {

    private PlaceDescriptionModel name;
    private PlaceDescriptionModel branchName;
    private float placeRating;
    private float priceRating;
    private String _id;
    private String placeProfileCoverPictureUrl;
    private String placeProfilePictureUrl;
    private int followersCount;
    private PlaceLocation location;
    private boolean hasOpeningTimes;
    private int activeOffersCount;
    private boolean openNow;
    private boolean isLikedByUser;

    private CommentModel recentComment;

    private List<PlaceCategoryModel> categories;
    private int productCommentsCount;
    private List<CommentModel> productCommentsSample;
    private String deepLink;

    private int likesCount;
    private int commentsCount;
    private int sharesCount;
    private int numberOfReviews;

    private boolean isPlaceOpen;
    private boolean isDeliveringToArea;
    private boolean isFollowedByUser;
    private PlaceDescriptionModel availability;
    private List<PlaceModel> deliveringBranches;

    public PlaceDescriptionModel getName() {
        return name;
    }

    public void setName(PlaceDescriptionModel name) {
        this.name = name;
    }

    public PlaceDescriptionModel getBranchName() {
        return branchName;
    }

    public void setBranchName(PlaceDescriptionModel branchName) {
        this.branchName = branchName;
    }

    public float getPlaceRating() {
        return placeRating;
    }

    public void setPlaceRating(float placeRating) {
        this.placeRating = placeRating;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPlaceProfileCoverPictureUrl() {
        return placeProfileCoverPictureUrl;
    }

    public void setPlaceProfileCoverPictureUrl(String placeProfileCoverPictureUrl) {
        this.placeProfileCoverPictureUrl = placeProfileCoverPictureUrl;
    }

    public String getPlaceProfilePictureUrl() {
        return placeProfilePictureUrl;
    }

    public void setPlaceProfilePictureUrl(String placeProfilePictureUrl) {
        this.placeProfilePictureUrl = placeProfilePictureUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public void setLikedByUser(boolean likedByUser) {
        isLikedByUser = likedByUser;
    }

    public PlaceLocation getLocation() {
        return location;
    }

    public void setLocation(PlaceLocation location) {
        this.location = location;
    }

    public boolean isHasOpeningTimes() {
        return hasOpeningTimes;
    }

    public void setHasOpeningTimes(boolean hasOpeningTimes) {
        this.hasOpeningTimes = hasOpeningTimes;
    }

    public int getActiveOffersCount() {
        return activeOffersCount;
    }

    public void setActiveOffersCount(int activeOffersCount) {
        this.activeOffersCount = activeOffersCount;
    }

    public boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public boolean isFollowedByUser() {
        return isFollowedByUser;
    }

    public void setFollowedByUser(boolean followedByUser) {
        isFollowedByUser = followedByUser;
    }
    public boolean isLikedByUser() {
        return isLikedByUser;
    }

    public float getPriceRating() {
        return priceRating;
    }

    public void setPriceRating(float priceRating) {
        this.priceRating = priceRating;
    }

    public List<PlaceCategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(List<PlaceCategoryModel> categories) {
        this.categories = categories;
    }

    public List<CommentModel> getProductCommentsSample() {
        return productCommentsSample;
    }

    public void setProductCommentsSample(List<CommentModel> productCommentsSample) {
        this.productCommentsSample = productCommentsSample;
    }

    public int getProductCommentsCount() {
        return productCommentsCount;
    }

    public void setProductCommentsCount(int productCommentsCount) {
        this.productCommentsCount = productCommentsCount;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getNumberOfComments() {
        return commentsCount;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.commentsCount = numberOfComments;
    }

    public int getNumberOfShares() {
        return sharesCount;
    }

    public void setNumberOfShares(int numberOfShares) {
        this.sharesCount = numberOfShares;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public boolean isDeliveringToArea() {
        return isDeliveringToArea;
    }

    public void setIsDeliveringToArea(boolean notDelivering) {
        isDeliveringToArea = notDelivering;
    }

    public boolean isPlaceOpen() {
        return isPlaceOpen;
    }

    public void setPlaceOpen(boolean placeOpen) {
        isPlaceOpen = placeOpen;
    }

    public PlaceDescriptionModel getAvailability() {
        return availability;
    }

    public void setAvailability(PlaceDescriptionModel availability) {
        this.availability = availability;
    }

    public CommentModel getRecentComment() {
        return recentComment;
    }

    public void setRecentComment(CommentModel recentComment) {
        this.recentComment = recentComment;
    }

    public List<PlaceModel> getDeliveringBranches() {
        return deliveringBranches;
    }

    public void setDeliveringBranches(List<PlaceModel> deliveringBranches) {
        this.deliveringBranches = deliveringBranches;
    }
}
