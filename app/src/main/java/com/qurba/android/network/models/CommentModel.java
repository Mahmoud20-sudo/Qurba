/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  io.realm.RealmObject
 *  io.realm.com_qurba_android_network_models_ProductCommentModelRealmProxyInterface
 *  io.realm.internal.RealmObjectProxy
 *  java.lang.String
 */
package com.qurba.android.network.models;

import com.qurba.android.network.models.UserDataModel;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.Transient;

public class CommentModel
        implements Serializable {
    private String comment;
    private String createdAt;
    private UserDataModel user;
    private String _id;
    private int likesCount;
    private int repliesCount;
    private boolean isLikedByUser;
    private List<CommentModel> replies;
    private CommentModel recentReply;
    private String type;

    @Transient
    private String parentId; //comment, offer or place id

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    public String getComment() {
        return this.realmGet$comment();
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public UserDataModel getUser() {
        return this.realmGet$user();
    }

    public String realmGet$comment() {
        return this.comment;
    }

    public String realmGet$createdAt() {
        return this.createdAt;
    }

    public UserDataModel realmGet$user() {
        return this.user;
    }

    public void realmSet$comment(String string2) {
        this.comment = string2;
    }

    public void realmSet$createdAt(String string2) {
        this.createdAt = string2;
    }

    public void realmSet$user(UserDataModel userDataModel) {
        this.user = userDataModel;
    }

    public void setComment(String string2) {
        this.realmSet$comment(string2);
    }

    public void setCreatedAt(String string2) {
        this.realmSet$createdAt(string2);
    }

    public void setUser(UserDataModel userDataModel) {
        this.realmSet$user(userDataModel);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<CommentModel> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentModel> replies) {
        this.replies = replies;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public String getLocalizedLikesCount() {
        return NumberFormat.getInstance().format(likesCount);
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(int repliesCount) {
        this.repliesCount = repliesCount;
    }

    public boolean isLikedByUser() {
        return isLikedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        isLikedByUser = likedByUser;
    }

    public CommentModel getRecentReply() {
        return recentReply;
    }

    public void setRecentReply(CommentModel recentReply) {
        this.recentReply = recentReply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (_id == null) return false;
        if (o == null || getClass() != o.getClass()) return false;
        return this._id.equalsIgnoreCase(((CommentModel) o).get_id());
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
