package com.qurba.android.ui.profile.views;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.qurba.android.R;
import com.qurba.android.databinding.ActivitySettingsBinding;
import com.qurba.android.network.models.response_models.GuestLoginResponseModel;
import com.qurba.android.ui.profile.view_models.SettingsViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.SharedPreferencesManager;

import static com.qurba.android.utils.SystemUtils.restartApp;

import io.intercom.android.sdk.Intercom;

public class SettingsActivity extends BaseActivity {

    private ActivitySettingsBinding binding;
    private SettingsViewModel viewModel;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
    }

    private void initialization() {
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setSettingsVM(viewModel);
        binding.toggleSwitches.setCheckedTogglePosition(sharedPref.getLanguage().equalsIgnoreCase("ar") ? 0 : 1);
        Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE);
        initListeners();
    }

    private void initListeners() {
        binding.animToolbar.setNavigationOnClickListener(view -> onBackPressed());
        binding.toggleSwitches.setOnToggleSwitchChangeListener((position, isChecked) -> viewModel.saveSettingsDialog(this, position == 0 ? "ar" : "en").setOnDismissListener(dialog -> binding.toggleSwitches.setCheckedTogglePosition(position == 0 ? 1 : 0, false)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}

