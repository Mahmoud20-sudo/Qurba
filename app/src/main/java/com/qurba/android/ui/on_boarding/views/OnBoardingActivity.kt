package com.qurba.android.ui.on_boarding.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.facebook.appevents.AppEventsConstants
import com.mazenrashed.logdnaandroidclient.models.Line
import com.qurba.android.R
import com.qurba.android.databinding.ActivityOnboardingBinding
import com.qurba.android.network.QurbaLogger
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.QurbaLogger.Companion.standardFacebookEventsLogging
import com.qurba.android.ui.address_component.views.AddressActivity
import com.qurba.android.utils.BaseActivity
import com.qurba.android.utils.Constants
import com.qurba.android.utils.MyPagerAdapter
import com.qurba.android.utils.SharedPreferencesManager


class OnBoardingActivity : BaseActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        initialize()
        initListeners()
    }

    private fun initialize() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)
        setViewPagerPager()
        logging(application.applicationContext,
                Constants.USER_ONBOARDING_GET_STARTED_SUCCESS, Line.LEVEL_INFO,
                "User starting onboarding screen", "")
    }

    private fun initListeners() {
        binding.getStartedBtn.setOnClickListener {

            val params = Bundle()
            params.putInt(AppEventsConstants.EVENT_PARAM_SUCCESS, 1)
            standardFacebookEventsLogging(this, AppEventsConstants.EVENT_NAME_COMPLETED_TUTORIAL, params)
            SharedPreferencesManager.getInstance().isFirstTimeRunning = false

            val intent = Intent(this, AddressActivity::class.java)
            intent.putExtra(Constants.BRANCH_OBJECT, getIntent()?.extras?.getString(Constants.BRANCH_OBJECT))
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun setViewPagerPager() {
        val mViewPagerAdapter = MyPagerAdapter(supportFragmentManager!!)
        binding.viewPager.adapter = mViewPagerAdapter
        binding.pageIndicator?.setViewPager(binding.viewPager)

        binding.viewPager.setScrollDurationFactor(2.0)
        binding.viewPager.offscreenPageLimit = 3

        val listIndex = intArrayOf(0)
        val handler = Handler(Looper.getMainLooper())
        val timer = object : Thread() {
            override fun run() {
                try {
                    if (listIndex[0] == 3) {
                        listIndex[0] = 0
                        binding.viewPager.setCurrentItem(listIndex[0], false)
                    } else {
                        binding.viewPager.setCurrentItem(listIndex[0], true)
                        listIndex[0]++
                    }

                    handler.postDelayed(this, 3000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        timer.start()
    }
}