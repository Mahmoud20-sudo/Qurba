package com.qurba.android.ui.auth.view_models;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;

import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SystemUtils;

public class LoginViewModel extends BaseViewModel {
    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public View.OnClickListener hideKeyboard() {
        return v -> {
            SystemUtils.hideKeyBoard(QurbaApplication.getContext(), v);
        };
    }
}
