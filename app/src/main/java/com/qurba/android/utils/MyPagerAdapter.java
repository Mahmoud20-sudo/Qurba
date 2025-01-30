package com.qurba.android.utils;

import android.os.Parcelable;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.qurba.android.ui.on_boarding.views.OnBoardingFragmentOne;
import com.qurba.android.ui.on_boarding.views.OnBoardingFragmentSecond;
import com.qurba.android.ui.on_boarding.views.OnBoardingFragmentThird;
import com.qurba.android.ui.place_details.views.InfoFragment;
import com.qurba.android.ui.place_details.views.PlaceOffersFragment;
import com.qurba.android.ui.place_details.views.PlaceProductsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vihaan on 1/9/15.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private int mCurrentPosition = -1;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
        fragments.add(new OnBoardingFragmentOne());
        fragments.add(new OnBoardingFragmentSecond());
        fragments.add(new OnBoardingFragmentThird());
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (position != mCurrentPosition) {
            Fragment fragment = (Fragment) object;
//            CustomPager pager = (CustomPager) container;
//            if (fragment != null && fragment.getView() != null) {
//                mCurrentPosition = position;
//                pager.measureCurrentView(fragment.getView());
//            }
        }
    }

    public void reloadFragment(int position) {
        //This list contains a 'list' of Fragments available on ViewPager adapter.
        switch (position) {
            case 0:
                ((PlaceOffersFragment) fragments.get(position)).onResume();
                break;
            //iterate for case from 0 to 11
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
