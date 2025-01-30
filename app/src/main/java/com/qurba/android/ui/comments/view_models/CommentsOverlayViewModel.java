/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  android.app.Application
 *  android.content.Context
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.widget.Toast
 *  androidx.databinding.Bindable
 *  androidx.databinding.ObservableField
 *  androidx.lifecycle.MutableLiveData
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  org.json.JSONObject
 *  retrofit2.Response
 *  rx.Observable
 *  rx.Scheduler
 *  rx.Subscriber
 *  rx.Subscription
 *  rx.android.schedulers.AndroidSchedulers
 *  rx.schedulers.Schedulers
 */
package com.qurba.android.ui.comments.view_models;

import android.app.Application;

import androidx.databinding.ObservableField;

import com.qurba.android.utils.BaseViewModel;

public class CommentsOverlayViewModel extends BaseViewModel {
    public ObservableField<String> likeCount = new ObservableField("");
    public ObservableField<String> shareCount = new ObservableField("");
    public ObservableField<String> overlayTitle = new ObservableField("");

    public CommentsOverlayViewModel(Application application) {
        super(application);
    }
}

