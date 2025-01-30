package com.qurba.android.ui.home.views;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.qurba.android.R;
import com.qurba.android.databinding.ActivityHomeBinding;
import com.qurba.android.ui.home.view_models.HomeViewModel;

public class HomeActivity extends FragmentActivity {

    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
    }

    private void initialization() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.setHomeVM(viewModel);
        initializeBottomBar();
    }

    private void initializeBottomBar() {
        binding.homeBottomNavigationBar.add(new MeowBottomNavigation.Model(1, R.drawable.ic_tabbar_trending_normal));
        binding.homeBottomNavigationBar.add(new MeowBottomNavigation.Model(2, R.drawable.ic_tabbar_nearby_normal));
        binding.homeBottomNavigationBar.add(new MeowBottomNavigation.Model(3, R.drawable.ic_tabbar_offers_normal));
        binding.homeBottomNavigationBar.add(new MeowBottomNavigation.Model(4, R.drawable.ic_tabbar_profile_normal));
        binding.homeBottomNavigationBar.getCellById(1).setCircleColor(getResources().getColor(R.color.main_red_color));
        binding.homeBottomNavigationBar.getCellById(1).enableCell(true);
        binding.homeBottomNavigationBar.getCellById(2).setCircleColor(getResources().getColor(R.color.main_red_color));
        binding.homeBottomNavigationBar.getCellById(3).setCircleColor(getResources().getColor(R.color.main_red_color));
        binding.homeBottomNavigationBar.getCellById(4).setCircleColor(getResources().getColor(R.color.main_red_color));
    }
}
