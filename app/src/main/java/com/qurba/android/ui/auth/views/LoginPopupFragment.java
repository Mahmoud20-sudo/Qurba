package com.qurba.android.ui.auth.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.databinding.LoginDialogV2Binding;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.ui.auth.view_models.LoginPopUpViewModel;
import com.qurba.android.ui.home.views.HomeActivityKotlin;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.ScreenUtils;
import com.qurba.android.utils.SharedPreferencesManager;
import com.qurba.android.utils.SocialLoginCallBack;
import java.io.IOException;
import java.util.Collections;

public class LoginPopupFragment extends SuperBottomSheetFragment {

    private LoginDialogV2Binding binding;
    private LoginPopUpViewModel viewModel;
    private SocialLoginCallBack socialLoginCallBack;
    private CallbackManager callbackManager;

    GoogleSignInOptions gso ;
    private GoogleSignInClient mGoogleSignInClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(
                inflater, R.layout.login_dialog_v2, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(LoginPopUpViewModel.class);
        binding.setViewModel(viewModel);
        initialization();
        initListeners();
        return view;
    }

    private void initialization() {
        viewModel.setSheetFragment(this);
        viewModel.setSocialLoginCallBack(socialLoginCallBack);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void initListeners() {
        binding.optionsCloseIv.setOnClickListener(view -> dismiss());

        callbackManager = CallbackManager.Factory.create();

        binding.facebook.setOnClickListener(v -> {
            LoginManager.getInstance().logOut();
            LoginManager mLoginMgr = LoginManager.getInstance();
            mLoginMgr.logInWithReadPermissions(LoginPopupFragment.this,
                    Collections.singletonList("public_profile"));

            mLoginMgr.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            viewModel.sendLogInRequest(requireActivity(), loginResult.getAccessToken().getToken());

                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_FACEBOOK_LOGIN_SUCCESS,
                                    Line.LEVEL_INFO, "User login using Facebook account has returned a success response from Facebook with access token ", loginResult.getAccessToken().getToken());
                        }

                        @Override
                        public void onCancel() {
                            // App code
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_FACEBOOK_LOGIN_CANCELED,
                                    Line.LEVEL_ERROR, "User cancelled login using Facebook account" , "");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_FACEBOOK_LOGIN_ERROR,
                                    Line.LEVEL_ERROR, "User login using Facebook account has returned a error response from Facebook " , exception.getMessage());

                        }
                    });
        });

        binding.google.setOnClickListener(v -> signIn());

    }

    private void signIn() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    @Override
    public int getPeekHeight() {
        ViewGroup.LayoutParams params = binding.parentRl.getLayoutParams();
        params.height = new ScreenUtils(requireContext()).getHeight();
        binding.parentRl.setLayoutParams(params);
        binding.parentRl.requestLayout();

        return new ScreenUtils(requireContext()).getHeight();
    }

    public void setSocialLoginCallBack(SocialLoginCallBack socialLoginCallBack) {
        this.socialLoginCallBack = socialLoginCallBack;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof HomeActivityKotlin && !SharedPreferencesManager.getInstance().isGuest())
            ((HomeActivityKotlin) getActivity()).refreshTabs();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == Constants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            assert result != null;
            if (result.isSuccess()) {
                final GoogleSignInAccount account = result.getSignInAccount();

                Runnable runnable = () -> {
                    try {
                        String scope = "oauth2:"+ Scopes.EMAIL+" "+ Scopes.PROFILE;
                        assert account != null;
                        assert account.getAccount() != null;
                        String accessToken = GoogleAuthUtil.getToken(requireActivity(), account.getAccount(), scope, new Bundle());
                        viewModel.sendGooogleLogInRequest(requireActivity(), accessToken);

                    } catch (IOException | GoogleAuthException e) {
                        e.printStackTrace();
                    }
                };
                AsyncTask.execute(runnable);
            }
        }
    }
}
