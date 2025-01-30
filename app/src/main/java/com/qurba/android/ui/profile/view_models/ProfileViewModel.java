package com.qurba.android.ui.profile.view_models;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import com.qurba.android.ui.my_orders.views.MyOrderActivity;
import com.qurba.android.ui.profile.views.EditProfileActivity;
import com.qurba.android.ui.profile.views.UserMealsActivity;
import com.qurba.android.ui.profile.views.UserOffersActivity;
import com.qurba.android.ui.profile.views.UserPlacesActivity;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;

import io.intercom.android.sdk.Intercom;

public class ProfileViewModel extends BaseViewModel {

    @Bindable
    private String phone;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
    }

    @Bindable
    public String getUsername() {
        if (SharedPreferencesManager.getInstance().getUser() != null) {
            return SharedPreferencesManager.getInstance().getUser().getFirstName() + " " + SharedPreferencesManager.getInstance().getUser().getLastName();
        } else {
            return "";
        }
    }

    @Bindable
    public String getUserPhone() {
        if (phone != null) {
            return phone;
        } else {
            return "";
        }
    }

    @Bindable
    public boolean isGuest() {
        return SharedPreferencesManager.getInstance().getUser() != null;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyDataChanged();
    }

    public View.OnClickListener openLikedMeals() {
        return v -> {
            Intent intent = new Intent(QurbaApplication.getContext(), UserMealsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE);
            QurbaApplication.getContext().startActivity(intent);
        };
    }

    public View.OnClickListener openEditProfile() {
        return v -> {
            Intent intent = new Intent(QurbaApplication.getContext(), EditProfileActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE);
            ((BaseActivity)v.getContext()).startActivityForResult(intent, Constants.EDIT_PROFILE);
        };
    }

    public View.OnClickListener openFollowedPlaces() {
        return v -> {
            Intent intent = new Intent(QurbaApplication.getContext(), UserPlacesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE);
            QurbaApplication.getContext().startActivity(intent);
        };
    }

    public View.OnClickListener openLikedffers() {
        return v -> {
            Intent intent = new Intent(QurbaApplication.getContext(), UserOffersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE);
            QurbaApplication.getContext().startActivity(intent);
        };
    }

    public View.OnClickListener openMyOrder() {
        return v -> {
            Intent intent = new Intent(QurbaApplication.getContext(), MyOrderActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE);
            QurbaApplication.getContext().startActivity(intent);
        };
    }

}
