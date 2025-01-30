/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.qurba.android.network.models.ProductCommentModel
 *  java.lang.Object
 */
package com.qurba.android.utils;

import com.qurba.android.network.models.CommentModel;

public interface CommentsCallBack {
    public void onCommentAdded(CommentModel var1, boolean isReply);
    public void onCommentUpdated(CommentModel var1, boolean isReply);
    public void onCommentDeleted(boolean isReply, int deletedCommentsount);

}

