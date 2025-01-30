package com.qurba.android.network.models.response_models;

import com.qurba.android.utils.SharedPreferencesManager;

public class CognitoPayload {

    private String _id;
    private String cognitoIdentityId;
    private String cognitoToken;
    private String cognitoIdentityPoolId;
    private String cognitoAWSRegion;
    private String cognitoDeveloperProviderName;

    public String getCognitoDeveloperProviderName() {
        return cognitoDeveloperProviderName;
    }

    public void setCognitoDeveloperProviderName(String cognitoDeveloperProviderName) {
        this.cognitoDeveloperProviderName = cognitoDeveloperProviderName;
    }

    public String getCognitoAWSRegion() {
        return cognitoAWSRegion;
    }

    public void setCognitoAWSRegion(String cognitoAWSRegion) {
        this.cognitoAWSRegion = cognitoAWSRegion;
    }

    public String getCognitoIdentityPoolId() {
        return cognitoIdentityPoolId;
    }

    public void setCognitoIdentityPoolId(String cognitoIdentityPoolId) {
        this.cognitoIdentityPoolId = cognitoIdentityPoolId;
    }

    public String getCognitoToken() {
        return cognitoToken;
    }

    public void setCognitoToken(String cognitoToken) {
        this.cognitoToken = cognitoToken;
    }

    public String getCognitoIdentityId() {
        return cognitoIdentityId;
    }

    public void setCognitoIdentityId(String cognitoIdentityId) {
        this.cognitoIdentityId = cognitoIdentityId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
