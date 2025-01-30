package com.qurba.android.ui.auth.view_models;

import android.app.Application;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.QurbaApplication;

public class BottomSheetViewModel  extends BaseViewModel {
    public BottomSheetViewModel(@NonNull Application application) {
        super(application);
    }

    public View.OnClickListener Login() {
        return v -> {
            Toast.makeText(QurbaApplication.getContext(), "Login", Toast.LENGTH_SHORT).show();
        };
    }
}
