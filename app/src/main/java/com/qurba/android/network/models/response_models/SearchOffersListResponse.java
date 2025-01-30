package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.OffersModel;

import java.util.List;

public class SearchOffersListResponse {

    private List<OffersModel> docs;
    private int total;
    private int limit;
    private int page;
    private int pages;
    private ErrorModel errorModel;
    private String errorType;

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }

    public void setErrorModel(ErrorModel errorModel) {
        this.errorModel = errorModel;
    }

    public List<OffersModel> getDocs() {
        return docs;
    }

    public void setDocs(List<OffersModel> docs) {
        this.docs = docs;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
