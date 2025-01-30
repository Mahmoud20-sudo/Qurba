package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.BaseModel;
import com.qurba.android.network.models.CommentModel;
import com.qurba.android.network.models.OrdersModel;

import java.util.ArrayList;
import java.util.List;

public class CommentsListResponse extends BaseModel {

    private List<CommentModel> docs;
    public List<CommentModel> getDocs() {
        return docs;
    }
    public void setDocs(List<CommentModel> docs) {
        this.docs = docs;
    }
}
