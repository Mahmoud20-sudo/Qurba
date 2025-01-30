package com.qurba.android.ui.auth.views;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.qurba.android.R;
import com.qurba.android.databinding.FragmentSignupPhoneBinding;
import com.qurba.android.ui.auth.view_models.SignUpPhoneViewModel;
import com.qurba.android.utils.BaseActivity;

import static com.qurba.android.utils.extenstions.ExtesionsKt.showKeyboard;

public class SignUpPhoneFragment extends Fragment {

    private SignUpPhoneViewModel viewModel;
    private FragmentSignupPhoneBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_phone, container, false);
        viewModel = new ViewModelProvider(this).get(SignUpPhoneViewModel.class);
        binding.setSignUpPhoneVM(viewModel);
        initialization();
        return binding.getRoot();
    }

    private void initialization() {
        showKeyboard((BaseActivity) getActivity());
//        viewModel.setPhoneNumber(getArguments().getString("phone"));
        viewModel.setActivity((BaseActivity) getActivity());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //for ultimating the tabs ui performance
            binding.signUpPhonePhoneEdt.requestFocus();
            binding.signUpPhonePhoneEdt.setSelection(binding.signUpPhonePhoneEdt.getText().length());
        }, 50);

        binding.signUpPhonePhoneEdt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);

    }

    public static SignUpPhoneFragment getInstance() {
        return new SignUpPhoneFragment();
    }
}
