package com.qurba.android.ui.on_boarding.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.qurba.android.R
import com.qurba.android.databinding.FragmentOnboardingFirstBinding
import com.qurba.android.databinding.FragmentOnboardingThirdBinding
import com.qurba.android.databinding.FragmentOnboardingThirdBindingImpl

public class OnBoardingFragmentThird : Fragment(){

    private lateinit var binding: FragmentOnboardingThirdBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding_third, container, false)
        return binding.root
    }
}