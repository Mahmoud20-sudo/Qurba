package com.qurba.android.ui.auth.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import com.qurba.android.R;
import com.qurba.android.databinding.ActivitySignUpBinding;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.QurbaApplication;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends BaseActivity {

    private ActivitySignUpBinding binding;
    private Intent intent;
    private String prviousPhoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
    }

    private void initialization() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        intent = getIntent();

        initViewPager(binding.signUpViewPager);
        initToolbar();
        initTabs();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initViewPager(ViewPager viewPager) {
        if (intent.getExtras() != null && intent.getExtras().getString("phone") != null && intent.getExtras().getString("phone").length() != 0){
//            binding.signUpTabs.setVisibility(View.GONE);
//            binding.toolbar.setTitle(getString(R.string.create_account));
//            isOrdering = true;
            prviousPhoneNumber = intent.getExtras().getString("phone").substring(2);
        }

        Bundle bundle = new Bundle();
        bundle.putString("phone", prviousPhoneNumber);

        SignUpPhoneFragment signUpPhoneFragment = SignUpPhoneFragment.getInstance();
        signUpPhoneFragment.setArguments(bundle);
        //SignUpEmailFragment signUpEmailFragment = SignUpEmailFragment.getInstance();

        signUpPhoneFragment.setArguments(bundle);
        //signUpEmailFragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(signUpPhoneFragment, getString(R.string.phone));
        //adapter.addFragment(signUpEmailFragment, getString(R.string.email));
        viewPager.setAdapter(adapter);
    }

    private void initTabs() {
        binding.signUpTabs.setupWithViewPager(binding.signUpViewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        QurbaApplication.setCurrentActivity(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        QurbaApplication.setCurrentActivity(this);

    }
}
