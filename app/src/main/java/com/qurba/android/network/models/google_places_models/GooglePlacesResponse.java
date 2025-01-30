package com.qurba.android.network.models.google_places_models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GooglePlacesResponse {

    @SerializedName("html_attributions")
    private HTMLAttributions[] htmlAttributions;

    @SerializedName("next_page_token")
    private String nextPageToken;

    @SerializedName("results")
    private List<Place> results;

    @SerializedName("predictions")
    private List<Place> predictions;

    @SerializedName("result")
    private Place result;

    public HTMLAttributions[] getHtmlAttributions () {
        return htmlAttributions;
    }

    public void setHtmlAttributions (HTMLAttributions[] htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public String getNextPageToken () {
        return nextPageToken;
    }

    public void setNextPageToken (String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<Place> getResults () {
        return results;
    }

    public void setResults (List<Place> results) {
        this.results = results;
    }

    public List<Place> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Place> predictions) {
        this.predictions = predictions;
    }

    public Place getResult() {
        return result;
    }

    public void setResult(Place result) {
        this.result = result;
    }
}
