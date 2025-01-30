package com.qurba.android.utils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.FacebookSdk;
import com.qurba.android.R;
import com.qurba.android.databinding.DialogClearCartBinding;
import com.qurba.android.databinding.DialogUpdateBinding;
import com.qurba.android.databinding.PlaceNotSupportAreaViewBinding;
import com.qurba.android.network.CognitoClient;
import com.qurba.android.network.models.response_models.AppSettingsModel;
import com.qurba.android.network.models.response_models.LoginResponseModel;
import com.qurba.android.ui.address_component.views.AddressActivity;
import com.qurba.android.ui.auth.views.LoginPopupFragment;
import com.qurba.android.ui.order_status.view_models.OrderConfirmationViewModel;
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity;

import java.util.Locale;

import retrofit2.Response;
import rx.Subscriber;

public class BaseActivity extends AppCompatActivity {

    private Subscriber<Response<LoginResponseModel>> subscriber;
    private SocialLoginCallBack onLoginFinished;
    private SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance();
    private BaseViewModel viewModel;

    private void setLocale(String lang, Context context) {
        Configuration config = context.getResources().getConfiguration();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            getApplicationContext().getResources().updateConfiguration(config, null);
        } else {
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            config.setLayoutDirection(locale);
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            context.createConfigurationContext(config);
        } else {
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setLocale(sharedPreferencesManager.getLanguage(), this);
        viewModel = new ViewModelProvider(this).get(BaseViewModel.class);
        FacebookSdk.fullyInitialize();
    }

    public void showLoginDialog(SocialLoginCallBack... onLoginFinished) {
        LoginPopupFragment sheet = new LoginPopupFragment();
        sheet.setSocialLoginCallBack(onLoginFinished.length == 0 ? null : onLoginFinished[0]);
        sheet.show(getSupportFragmentManager(), "LoginFragment");
    }

    public void showAvailableBranchDialog(String placeID, boolean isOffer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        DialogUpdateBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_update, null, false);

        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();

        binding.titleTv.setText(getString(R.string.this_branch) + " " + getString(R.string.not_deliver_hint) + " " + SharedPreferencesManager.getInstance().getSelectedCachedArea().getArea().getName().getEn());
        binding.contentTv.setText(getString(R.string.another_branch_hint));
        binding.updateNowTv.setText(getString(R.string.ok));

        binding.notNowTv.setOnClickListener(view -> {
            dialog.dismiss();
        });

        binding.updateNowTv.setOnClickListener(view -> {
            Intent intent = new Intent(this, PlaceDetailsActivity.class);
            intent.putExtra("place_id", placeID);
            intent.putExtra(Constants.ORDER_TYPE, isOffer ? Constants.OFFERS : Constants.PRODUCTS);
            startActivity(intent);
            dialog.dismiss();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();
    }

    public void showCancelDialog(OrderConfirmationViewModel orderConfirmationViewModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        DialogUpdateBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_update, null, false);

        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();

        binding.notNowTv.setVisibility(View.VISIBLE);
        binding.updateNowTv.setText(getString(R.string.call_customer_support));

        binding.contentTv.setText(getString(R.string.cancel_order_breif));
        binding.titleTv.setText(getString(R.string.cancel_order));

        binding.notNowTv.setOnClickListener(view -> {
            dialog.dismiss();
        });

        binding.updateNowTv.setOnClickListener(view -> {
            orderConfirmationViewModel.callPlace();
        });

        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public AlertDialog showUpdateDialog(AppSettingsModel appSettingsModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        DialogUpdateBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_update, null, false);

        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();

        binding.notNowTv.setVisibility(View.GONE);

        binding.notNowTv.setOnClickListener(view -> {
            dialog.dismiss();
        });

        binding.updateNowTv.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.qurba.android")));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.qurba.android")));
            }
            dialog.dismiss();
        });

        dialog.setCancelable(!appSettingsModel.isMandatoryUpdate());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(!appSettingsModel.isMandatoryUpdate());
        dialog.show();
        return dialog;
    }
    
    public AlertDialog showConfirmDialog(boolean isHaveReplies, boolean isComment, ClearCartCallback clearCartCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        DialogUpdateBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_update, null, false);

        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();

        binding.notNowTv.setVisibility(View.VISIBLE);
        binding.notNowTv.setText(getString(R.string.cancel));
        binding.updateNowTv.setText(getString(R.string.dleet_hint));

        binding.contentTv.setText(isHaveReplies ? getString(R.string.delete_comment_with_replies_msg)
                : getString(R.string.delete_comment_msg));
        binding.titleTv.setText(isComment ? getString(R.string.delete_comment_title) : getString(R.string.delete_reply_title));

        binding.notNowTv.setOnClickListener(view -> {
            dialog.dismiss();
        });

        binding.updateNowTv.setOnClickListener(v -> {
            clearCartCallback.clearCart();
            dialog.dismiss();
        });

        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }

    public AlertDialog showClearCartDialog(String placeName, ClearCartCallback clearCartCallback, String... msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        DialogClearCartBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_clear_cart, null, false);

        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();

        binding.cancelTv.setOnClickListener(view -> {
            dialog.dismiss();
        });

        binding.startTv.setOnClickListener(v -> viewModel.clearCart(BaseActivity.this, success -> {
            if (success) {
                clearCartCallback.clearCart();
                viewModel.getCart(this, cartModel -> null);
                dialog.dismiss();
            }
            return null;
        }));

        binding.messageTv.setText(msg.length != 0 ? msg[0] :
                getString(R.string.already_have_items) + " \"" + placeName + "\" ," + getString(R.string.clear_cart));

        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }

    public AlertDialog showUnSupportedAreaDialog(ClearCartCallback clearCartCallback, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        PlaceNotSupportAreaViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.place_not_support_area_view
                , null, false);

        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();

        binding.cancelTv.setOnClickListener(view -> {
            dialog.dismiss();
            clearCartCallback.clearCart();
        });

        binding.editLocationTv.setOnClickListener(view -> {
            startActivityForResult(new Intent(this, AddressActivity.class), 11901);
            dialog.dismiss();
        });

        binding.notDeliverTitleTv.setText(msg);

        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }

    public void clearCart() {
        viewModel.clearCart(this, aBoolean -> null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CognitoClient.stopService();
    }
}
