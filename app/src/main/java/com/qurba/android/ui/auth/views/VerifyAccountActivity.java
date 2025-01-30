package com.qurba.android.ui.auth.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.qurba.android.R;
import com.qurba.android.databinding.ActivityVerifyAccountBinding;
import com.qurba.android.ui.auth.view_models.VerifyAccountViewModel;
import com.qurba.android.ui.profile.views.EditProfileActivity;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;

import java.text.NumberFormat;
import java.util.Locale;

public class VerifyAccountActivity extends BaseActivity {

    private VerifyAccountViewModel viewModel;
    private ActivityVerifyAccountBinding binding;
    private int time = 60;
    private String email = null;
    private String phone = null;
    private boolean isForgotPassword;
    private boolean isOrdering;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
    }

    private void initialization() {
        viewModel = new ViewModelProvider(this).get(VerifyAccountViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_account);
        binding.setVerifyAccountVM(viewModel);
        viewModel.setActivity(this);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("email")) {
                email = getIntent().getStringExtra("email");
                binding.verifyAccountEmailPhone.setText(getIntent().getStringExtra("email"));
            } else if (getIntent().getExtras().containsKey("phone")) {
                phone = getIntent().getStringExtra("phone");
                binding.verifyAccountEmailPhone.setText(getString(R.string.zero) +
                        String.format(new Locale(sharedPref.getLanguage()), "%d", Long.parseLong(
                                phone.replace("+", "0"))));
            }
            if (getIntent().getExtras().containsKey("forgot-password")) {
                isForgotPassword = getIntent().getBooleanExtra("forgot-password", false);
            }
            if (getIntent().getExtras().containsKey(Constants.IS_ORDERING)) {
                viewModel.setIsOrdering(getIntent().getBooleanExtra(Constants.IS_ORDERING, false));
            }
            if (getIntent().getExtras().containsKey(Constants.IS_PROFILE_EDITING)) {
                viewModel.setIsEditing(getIntent().getBooleanExtra(Constants.IS_PROFILE_EDITING, false));
            }
        }

        isOrdering = SharedPreferencesManager.getInstance().isORdering();

        binding.verifyAccountChangeEmailPhone.setOnClickListener(view -> {
//            if (getIntent().getExtras().containsKey(Constants.IS_PROFILE_EDITING)){
//                onBackPressed();
//                return;
//            }
            Intent intent = new Intent(getApplication(), getIntent().getExtras().containsKey(Constants.IS_PROFILE_EDITING)
                    ? EditProfileActivity.class : SignUpActivity.class);
            intent.putExtra("phone", getIntent().getExtras().getString("phone"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            QurbaApplication.getContext().startActivity(intent);
            finish();
        });

        initToolbar();
        startTimer(null);
        handleButton();

//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.RECEIVE_SMS},
//                111);
    }

    private void initializeObservables() {
        viewModel.getResendObservable().observe(this, this::startTimer);
    }

    private void initToolbar() {
        binding.animToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void handleButton() {
        binding.pinView.setCursorVisible(true);
        binding.pinView.setCursorColor(getResources().getColor(R.color.main_red_color));
        binding.pinView.setCursorWidth(10);
        binding.pinView.requestFocus();
        binding.pinView.setLineColor(getResources().getColor(R.color.background_grey));

        binding.pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.pinView.getText().length() == 6) {
                    binding.verifyScreenSendCodeTv.setClickable(true);
                    binding.verifyScreenSendCodeTv.setEnabled(true);
                    binding.verifyScreenSendCodeTv.setBackgroundResource(R.drawable.red_oval);
                    viewModel.verifyOrderingPhoneCodeRequest(phone, binding.pinView.getText().toString());

                    binding.verifyScreenSendCodeTv.setOnClickListener(view -> {
                        viewModel.verifyOrderingPhoneCodeRequest(phone, binding.pinView.getText().toString());
//
                    });

                } else {
                    binding.verifyScreenSendCodeTv.setClickable(false);
                    binding.verifyScreenSendCodeTv.setEnabled(false);
                    binding.verifyScreenSendCodeTv.setBackgroundResource(R.drawable.inactive_red_oval);
                }
            }
        });
    }


    private void startTimer(String result) {
        binding.verifyAccountRetryTv.setVisibility(View.VISIBLE);

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.verifyAccountTimerTv.setText("");
                binding.verifyAccountTimerTv.setTextColor(getResources().getColor(R.color.black));
                binding.verifyAccountTimerTv.setText(NumberFormat.getInstance().format(0) +
                        ":" + checkDigit(time));
                binding.verifyAccountTimerTv.setOnClickListener(null);
                time--;
                if (time == 0) {
                    time = 60;
                }
            }

            public void onFinish() {
                binding.verifyAccountTimerTv.setText(getString(R.string.resend_code));
                binding.verifyAccountTimerTv.setTextColor(getResources().getColor(R.color.main_red_color));
                binding.verifyScreenSendCodeTv.setClickable(false);
                binding.verifyScreenSendCodeTv.setEnabled(false);
                binding.verifyScreenSendCodeTv.setBackgroundResource(R.drawable.inactive_red_oval);
                binding.verifyAccountRetryTv.setVisibility(View.GONE);

                binding.verifyAccountTimerTv.setOnClickListener(view -> {
                    initializeObservables();
                    binding.pinView.setText("");
                    binding.verifyScreenSendCodeTv.setText("");
                    viewModel.resendOrderingPhoneCodeRequest(phone, binding.pinView.getText().toString());
                });
            }

        }.start();
    }

    public String checkDigit(int number) {
        return number <= 9 ? NumberFormat.getInstance().format(0) + NumberFormat.getInstance().format(number)
                : NumberFormat.getInstance().format(number);
    }

    @Override
    protected void onPause() {
        super.onPause();
        QurbaApplication.setCurrentActivity(null);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        QurbaApplication.setCurrentActivity(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.REFRESH_ORDER_STATUS));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            binding.pinView.setText(intent.getStringExtra("body"));
//            viewModel.verifyOrderingPhoneCodeRequest(phone, intent.getStringExtra("body"));
        }
    };

}
