package com.qurba.android.ui.splash.views


import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.databinding.ActivitySplashBinding
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.response_models.GuestLoginResponseModel
import com.qurba.android.ui.splash.view_models.SplashViewModel
import com.qurba.android.utils.*
import io.branch.referral.Branch
import io.branch.referral.Branch.BranchReferralInitListener
import io.branch.referral.BranchError
import io.branch.referral.validators.IntegrationValidator
import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.identity.Registration
import org.json.JSONObject
import java.util.*


class SplashActivity : BaseActivity() {
    private var viewModel: SplashViewModel? = null
    private var binding: ActivitySplashBinding? = null
    private var rounteController: RoutingController? = null
    private var isBranshStarted = false
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var mTimer: Timer? = null

    //change bsee
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        initialization()
        initObservables()
        setFullScreen()
    }

    private fun setFullScreen(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            if (window.insetsController != null)
                window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun initObservables() {
        viewModel!!.getGuestLoginObservable()!!
            .observe(this@SplashActivity, { guestData: GuestLoginResponseModel.GuestModel? ->
                switchToHome(guestData)
            })
    }

    private fun initialization() {
        rounteController = RoutingController(this)
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        viewModel?.setActivity(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding?.splashVM = viewModel
//        binding?.root?.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        //SystemUtils.setLocalization();
        SystemUtils.resetData()
        if (sharedPref.version == null) sharedPref.version = "3"
        viewModel!!.getPlaceCategories()
        if (SharedPreferencesManager.getInstance().user == null) Intercom.client()
            .registerUnidentifiedUser() else {
            val registration =
                Registration.create().withUserId(SharedPreferencesManager.getInstance().user._id)
            Intercom.client().registerIdentifiedUser(registration)
        }
        Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE)
        viewModel?.getProfile(activity = this) {}
    }

    private fun switchToHome(guestData: GuestLoginResponseModel.GuestModel?) {
        //        sharedPref.isGuest = guestData != null
        FirebaseAnalytics.getInstance(QurbaApplication.getContext())
            .setUserId(if (sharedPref.user == null) guestData?.id else sharedPref.user._id)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Random().nextInt().toString() + "")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "SplashActivity")
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)

        intent?.extras?.get("link")?.also { a ->
            rounteController!!.routeAppWithoutBranch(a.toString())
        } ?: run {
            rounteController!!.routeApp()
            intent.extras?.let {
                if (it.get("consumed_by_intercom") != null) {
                    Intercom.client().displayMessenger()
                }
            }
        }

//        viewModel?.getCart()
//        intent?.data?.also { a ->
//            rounteController!!.routeAppWithoutBranch(a.toString())
//        } ?: run {
//            rounteController!!.routeApp()
//        }
    }

    override fun onStop() {
        super.onStop()
        mTimer = null
    }

    public override fun onStart() {
        super.onStart()
        sharedPref.clearCart()
        //        IntegrationValidator.validate(SplashActivity.this);
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener)
            .withData(if (intent != null) intent.data else null).init()
        startAppWithoutBranch()
    }

    private fun startAppWithoutBranch() {
        //Declare the timer to start app in case of branch failed to start after 10 secs
        mTimer = Timer()
        mTimer!!.schedule(
            object : TimerTask() {
                override fun run() {
                    if (!isBranshStarted) {
                        isBranshStarted = true
                        if (sharedPref.token == null) viewModel!!.loginGuest() else switchToHome(
                            sharedPref.guestModel
                        )
                        mTimer?.cancel()
                    }
                }
            },
            5000
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
//        Branch.getInstance().reInitSession(this, branchReferralInitListener);
        IntegrationValidator.validate(this@SplashActivity)
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit()
    }

    private val branchReferralInitListener =
        BranchReferralInitListener { linkProperties, error -> // do stuff with deep link data (nav to page, display content, etc)
            if (isBranshStarted) // app is already started from the timer
                return@BranchReferralInitListener

            try {
                if (linkProperties != null && linkProperties.has("appTabName")) {
                    rounteController!!.setReferringParams(linkProperties)
                    logging(
                        QurbaApplication.getContext(),
                        Constants.USER_BRANCH_IO_SUCCESS,
                        LEVEL_INFO, "Branch io parsing successfully done", ""
                    )
                }
                logBranhcError(linkProperties, error)
                if (linkProperties != null && linkProperties.has("is-from-fcm")) logging(
                    QurbaApplication.getContext(),
                    Constants.USER_NOTIFICATIONS_CLICK_SUCCESS,
                    LEVEL_INFO, "User notification routing to matching screen successfully done", ""
                )

                isBranshStarted = true

                if (sharedPref.token == null) {
                    viewModel!!.loginGuest()
                } else {
                    switchToHome(sharedPref.guestModel)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                logging(
                    QurbaApplication.getContext(),
                    Constants.USER_BRANCH_IO_FAILED,
                    LEVEL_ERROR, "Branch io parsing failed ", Log.getStackTraceString(e)
                )
            }
        }

    private fun logBranhcError(linkProperties: JSONObject?, error: BranchError?) {
        if (error != null && error.message.isNotEmpty()) {
            if (linkProperties!!.has("is-from-fcm")) logging(
                QurbaApplication.getContext(),
                Constants.USER_NOTIFICATIONS_CLICK_FAIL,
                LEVEL_ERROR, "User notification routing to matching screen failed ", error.message
            )
            logging(
                QurbaApplication.getContext(),
                Constants.USER_BRANCH_IO_FAILED,
                LEVEL_ERROR, "Branch io parsing failed ", error.message
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer = null
    }

}