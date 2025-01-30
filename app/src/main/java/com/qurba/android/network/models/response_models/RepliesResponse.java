package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.CommentModel;

public class RepliesResponse {

    private CommentsListResponse replies;
    private CommentModel parentComment;

    public CommentsListResponse getReplies() {
        return replies;
    }
    public void setReplies(CommentsListResponse docs) {
        this.replies = docs;
    }

    public CommentModel getParentComment() {
        return parentComment;
    }

    public void setParentComment(CommentModel parentComment) {
        this.parentComment = parentComment;
    }
}
