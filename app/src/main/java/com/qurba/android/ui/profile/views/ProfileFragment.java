package com.qurba.android.ui.profile.views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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
import com.qurba.android.BuildConfig;
import com.qurba.android.R;
import com.qurba.android.databinding.FragmentProfileBinding;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.ui.auth.view_models.LoginPopUpViewModel;
import com.qurba.android.ui.home.views.HomeActivityKotlin;
import com.qurba.android.ui.profile.view_models.ProfileViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;
import com.qurba.android.utils.SocialLoginCallBack;

import java.io.IOException;
import java.util.Arrays;

import io.intercom.android.sdk.Intercom;


public class ProfileFragment extends Fragment implements Toolbar.OnMenuItemClickListener, SocialLoginCallBack {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private LoginPopUpViewModel loginPopViewModel;
    private CallbackManager callbackManager;
    private SharedPreferencesManager sharedPRef = SharedPreferencesManager.getInstance();

    GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean isJustLogin;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        loginPopViewModel = new ViewModelProvider(this).get(LoginPopUpViewModel.class);
        loginPopViewModel.setSocialLoginCallBack(this);

        binding.setViewModel(loginPopViewModel);
        binding.setProfileVM(viewModel);
        setHasOptionsMenu(true);
        initListeners();
        return binding.getRoot();
    }

    public void initialization() {
        binding.versionTv.setText(getString(R.string.version_title) + " " + BuildConfig.VERSION_NAME);
        binding.buildTypeTv.setText(BuildConfig.ENV);
        if (sharedPRef.getUser() != null) {
            //binding.toolbar.inflateMenu(R.menu.menu_profile);
            //  binding.toolbar.setOnMenuItemClickListener(this);

            Glide.with(this.getActivity()).load(SharedPreferencesManager.getInstance().
                    getUser().getProfilePictureUrl()).into(this.binding.profileAvatarIv);
            binding.loggedParentLl.setVisibility(View.VISIBLE);
            binding.guestParentLl.setVisibility(View.GONE);
            viewModel.setPhone(sharedPRef.getUser().getMobileNumber().isEmpty() ? sharedPRef.getUser().getEmail() :
                    sharedPRef.getUser().getMobileNumber());
        }

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        isJustLogin = false;
    }

    private void initListeners() {
        callbackManager = CallbackManager.Factory.create();

        binding.loginFbRl.setOnClickListener(v -> {
            LoginManager.getInstance().logOut();
            isJustLogin = true;

            LoginManager mLoginMgr = LoginManager.getInstance();
            mLoginMgr.logInWithReadPermissions(ProfileFragment.this,
                    Arrays.asList("public_profile"));

            mLoginMgr.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            loginPopViewModel.sendLogInRequest(v.getContext(), loginResult.getAccessToken().getToken());

                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_FACEBOOK_LOGIN_SUCCESS,
                                    Line.LEVEL_INFO, "User login using Facebook account has returned a success response from Facebook with access token "
                                            , loginResult.getAccessToken().getToken());

                            if (loginPopViewModel.getSheetFragment() != null)
                                loginPopViewModel.getSheetFragment().dismissAllowingStateLoss();
                        }

                        @Override
                        public void onCancel() {
                            // App code
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_FACEBOOK_LOGIN_CANCELED,
                                    Line.LEVEL_ERROR, "User cancelled login using Facebook account", "");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_FACEBOOK_LOGIN_ERROR,
                                    Line.LEVEL_ERROR, "User login using Facebook account has returned a error response from Facebook ", exception.getStackTrace().toString());

                        }
                    });
        });

        binding.google.setOnClickListener(v -> signIn());
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(getString(R.string.settings))) {
            Intent intent = new Intent(requireActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        } else {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.settings))) {
            Intent intent = new Intent(requireActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        } else {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isJustLogin)
            initialization();
    }

    private void signIn() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    @Override
    public void onLoginFinished() {
        initialization();
        ((HomeActivityKotlin) getActivity()).refreshTabs();
        ((HomeActivityKotlin) getActivity()).getToolBar().findViewById(R.id.logout_iv).setVisibility(View.VISIBLE);
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
            if (result.isSuccess()) {
                isJustLogin = true;
                final GoogleSignInAccount account = result.getSignInAccount();
                ((HomeActivityKotlin) getActivity()).getToolBar().findViewById(R.id.logout_iv).setVisibility(View.VISIBLE);

                Runnable runnable = () -> {
                    try {
                        String scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE;
                        String accessToken = GoogleAuthUtil.getToken(getActivity(), account.getAccount(), scope, new Bundle());
                        loginPopViewModel.sendGooogleLogInRequest(getActivity(), accessToken);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (GoogleAuthException e) {
                        e.printStackTrace();
                    }
                };
                AsyncTask.execute(runnable);

            } else {
            }
        }
    }
}