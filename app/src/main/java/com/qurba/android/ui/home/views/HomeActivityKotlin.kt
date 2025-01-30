@file : JvmName("MainKotlin")

package com.qurba.android.ui.home.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.arlib.floatingsearchview.util.Util.dpToPx
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.kwai.koom.javaoom.KOOM
import com.kwai.koom.javaoom.KOOMProgressListener
import com.kwai.koom.javaoom.KOOMProgressListener.Progress
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.qurba.android.R
import com.qurba.android.databinding.ActivityHomeBinding
import com.qurba.android.network.QurbaLogger
import com.qurba.android.network.models.AddAddressModel
import com.qurba.android.network.models.OrdersModel
import com.qurba.android.network.models.response_models.AppSettingsModel
import com.qurba.android.network.models.response_models.DeliveryValidationPayload
import com.qurba.android.ui.add_edit_address.view_models.AddAddressViewModel
import com.qurba.android.ui.home.view_models.HomeViewModel
import com.qurba.android.ui.offers.views.OffersFragment
import com.qurba.android.ui.order_now.views.OrderNowFragment
import com.qurba.android.ui.places.views.PlacesFragment
import com.qurba.android.ui.profile.views.ProfileFragment
import com.qurba.android.ui.profile.views.SettingsActivity
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.checkVersionUpdate
import io.intercom.android.sdk.Intercom
import java.util.*

class HomeActivityKotlin : BaseActivity() {

    private var adddressFromComponent: AddAddressModel? = null
    private var selectedId: Int = 1
    private var binding: ActivityHomeBinding? = null
    private var viewModel: HomeViewModel? = null
    private val orderNowFragment = OrderNowFragment.newInstance()
    private val offersFragment = OffersFragment.newInstance()
    private val profileFragment = ProfileFragment.newInstance()
    private val placesFragment = PlacesFragment.newInstance()
    private var active: Fragment = orderNowFragment
    private lateinit var addAddressViewModel: AddAddressViewModel
    private var sharePref = SharedPreferencesManager.getInstance()
    private var appSettingsModel: AppSettingsModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialization()
        initObservables()
//        getActiveOrders()
        viewModel!!.fillAddressData()
        testReport();
    }

    private var mainHandler: Handler? = null
    private val TAG = "KOOM"

    fun testReport() {
        mainHandler = Handler(Looper.getMainLooper())
        mainHandler!!.postDelayed({
            KOOM.getInstance().manualTrigger()
            KOOM.getInstance().setProgressListener(this::changeStatusText);
        }, 1500)
    }

    private fun changeStatusText(progress: Progress) {
        mainHandler!!.post { chanStatusTextInMain(progress) }
    }


    private fun chanStatusTextInMain(progress: KOOMProgressListener.Progress) {
        var text = ""
        when (progress) {
            Progress.HEAP_DUMP_START -> text = "heap dump started"
            Progress.HEAP_DUMPED -> text = "heap dump ended"
            Progress.HEAP_DUMP_FAILED -> text = "heap dump failed"
            Progress.HEAP_ANALYSIS_START -> text = "heap analysis start"
            Progress.HEAP_ANALYSIS_DONE -> text =
                "heap analysis done, please check report in " + KOOM.getInstance().reportDir
            Progress.HEAP_ANALYSIS_FAILED -> text = "heap analysis failed"
            else -> {
            }
        }
//        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        QurbaApplication.setCurrentActivity(this)
        getCart()
        viewModel?.setShowCart(selectedId != 4)
        Intercom.client()
            .setLauncherVisibility(if (selectedId == 4) Intercom.Visibility.VISIBLE else Intercom.Visibility.GONE)
//        if (sharePref.isORdering) {//for refreshing active order list after login and ordering only
//            getActiveOrders()
//            sharePref.setOrdering(false)
//        }
    }

    override fun onPause() {
        super.onPause()
        QurbaApplication.setCurrentActivity(null)
    }

    private fun initObservables() {
        viewModel!!.ordersObservalble?.observe(
            this,
            { ordersModels: List<OrdersModel?>? -> publishOrders(ordersModels) })
        addAddressViewModel.deliveryAreaObservable?.observe(
            this,
            { deliveryAreaPayload: DeliveryValidationPayload? ->
                this.updateAddress(deliveryAreaPayload)
            })
        viewModel!!.appSettingsObservable?.observe(this, { appSettings: AppSettingsModel ->
            this.appSettingsModel = appSettingsModel
            checkVersionUpdate(appSettings)
        })
    }

    private fun publishOrders(ordersModels: List<OrdersModel?>?) {
        orderNowFragment.publishOrders(ordersModels)
    }

    fun getToolBar(): Toolbar? {
        return binding?.toolbar
    }

    fun getViewModel(): HomeViewModel? {
        return viewModel
    }

    fun updateCart() {
        viewModel?.updateCartQuantity()

        if (supportFragmentManager.findFragmentById(R.id.home_fragment_holder_fl) is OrderNowFragment)
            (supportFragmentManager.findFragmentById(R.id.home_fragment_holder_fl) as OrderNowFragment).updateQuantity();
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
    }

    fun getActiveOrders() {
        viewModel!!.activeOrders
    }

    @SuppressLint("StringFormatInvalid")
    private fun initialization() {
        AppEventsLogger.activateApp(application)
        Intercom.client().setBottomPadding(dpToPx(120))

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        addAddressViewModel = ViewModelProvider(this).get(AddAddressViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding!!.homeVM = viewModel

        initializeBottomBar()

        viewModel!!.setBinding(binding)
        viewModel!!.setActivity(this)
        viewModel!!.appSettings

        binding?.logoutIv?.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action === MotionEvent.ACTION_DOWN) {
                var intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, Constants.RC_LOGOUT)
                return@setOnTouchListener true
            }
            false
        }

        supportFragmentManager.beginTransaction().add(R.id.home_fragment_holder_fl, profileFragment)
            .commit();
        supportFragmentManager.beginTransaction().add(R.id.home_fragment_holder_fl, placesFragment)
            .commit();
        supportFragmentManager.beginTransaction().add(R.id.home_fragment_holder_fl, offersFragment)
            .commit();
        supportFragmentManager.beginTransaction()
            .add(R.id.home_fragment_holder_fl, orderNowFragment).commit();
        setFireBaseAnalytics()
    }

    private fun setFireBaseAnalytics() {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Random().nextInt().toString() + "")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "HomeActivityKotlin")
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    private fun initializeBottomBar() {
        binding!!.homeBottomNavigationBar.add(
            MeowBottomNavigation.Model(
                1,
                R.drawable.ic_tabbar_order_normal
            )
        )
        binding!!.homeBottomNavigationBar.add(
            MeowBottomNavigation.Model(
                2,
                R.drawable.ic_tabbar_offers_normal
            )
        )
        binding!!.homeBottomNavigationBar.add(
            MeowBottomNavigation.Model(
                3,
                R.drawable.ic_tabbar_nearby_normal
            )
        )
        binding!!.homeBottomNavigationBar.add(
            MeowBottomNavigation.Model(
                4,
                R.drawable.ic_tabbar_profile_normal
            )
        )
//        binding!!.homeBottomNavigationBar.add(MeowBottomNavigation.Model(4, R.drawable.ic_tabbar_offers_normal))
//        binding!!.homeBottomNavigationBar.add(MeowBottomNavigation.Model(5, R.drawable.ic_tabbar_profile_normal))
//        binding!!.homeBottomNavigationBar.show(1, true)

        if (intent?.extras?.get(Constants.SELECTED_TAB) != null) {
            when (intent?.extras?.get(Constants.SELECTED_TAB)) {
                Constants.ORDER_NOW -> {
                    selectOrderNow()
                    setFragment(1)
                }
                Constants.OFFERS -> {
                    selectOffers()
                    setFragment(2)
                }
                Constants.PLACES -> {
                    selectPlace()
                    setFragment(3)
                }
            }
        } else {
            selectOrderNow()
            setFragment(1)
        }

        binding!!.homeBottomNavigationBar.getCellById(1)!!.circleColor =
            ContextCompat.getColor(this, R.color.main_red_color)
        binding!!.homeBottomNavigationBar.getCellById(2)!!.circleColor =
            ContextCompat.getColor(this, R.color.main_red_color)
        binding!!.homeBottomNavigationBar.getCellById(3)!!.circleColor =
            ContextCompat.getColor(this, R.color.main_red_color)
        binding!!.homeBottomNavigationBar.getCellById(4)!!.circleColor =
            ContextCompat.getColor(this, R.color.main_red_color)
//        binding!!.homeBottomNavigationBar.getCellById(5)!!.circleColor = ContextCompat.getColor(this, R.color.main_red_color)

        binding!!.homeBottomNavigationBar.setOnClickMenuListener {
            when (it.id) {
                1 -> {
                    selectOrderNow()
                    setFragment(1)
                }
                2 -> {
                    selectOffers()
                    setFragment(2)
                }
                3 -> {
                    selectPlace()
                    setFragment(3)
                }
                4 -> {
                    selectProfile()
                    setFragment(4)
                }
            }
        }

        binding!!.homeSelectedItemProfileTv.setOnClickListener {
            setFragment(4)
            selectProfile()
        }

        binding!!.homeSelectedItemPlacesTv.setOnClickListener {
            setFragment(3)
            selectPlace()
        }

        binding!!.homeSelectedItemOffersTv.setOnClickListener {
            setFragment(2)
            selectOffers()
        }

        binding!!.homeSelectedItemOrderNowTv.setOnClickListener {
            setFragment(1)
            selectOrderNow()
        }
    }

    //
    fun selectOffers() {
        binding!!.homeSelectedItemOffersTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.main_red_color
            )
        )
        binding!!.homeSelectedItemPlacesTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )
        binding!!.homeSelectedItemProfileTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )
        binding!!.homeSelectedItemOrderNowTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )


        Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE)
        binding!!.homeBottomNavigationBar.show(2, true)
        binding!!.logoutIv.visibility = View.GONE
        binding!!.deliveryAreaIv.visibility = View.VISIBLE
        binding!!.deliveryAreaTv.visibility = View.VISIBLE
        binding!!.profileTv.visibility = View.GONE

        selectedId = 2
    }

    fun selectOrderNow() {//homeBottomNavigationBar
        binding!!.homeSelectedItemOrderNowTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.main_red_color
            )
        )
        binding!!.homeSelectedItemPlacesTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )
        binding!!.homeSelectedItemProfileTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )
        binding!!.homeSelectedItemOffersTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )

        binding!!.homeBottomNavigationBar.show(1, true)
        binding!!.logoutIv.visibility = View.GONE
        binding!!.deliveryAreaIv.visibility = View.VISIBLE
        binding!!.deliveryAreaTv.visibility = View.VISIBLE
        binding!!.profileTv.visibility = View.GONE
        Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE)

        selectedId = 1
    }

    fun getSelectedId(): Int {
        return selectedId
    }

    fun selectProfile() {
        binding!!.homeSelectedItemProfileTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.main_red_color
            )
        )
        binding!!.homeSelectedItemPlacesTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )
        binding!!.homeSelectedItemOffersTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )
        binding!!.homeSelectedItemOrderNowTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )

        binding!!.homeBottomNavigationBar.show(4, true)
        binding!!.logoutIv.visibility = View.VISIBLE
        binding!!.deliveryAreaIv.visibility = View.GONE
        binding!!.deliveryAreaTv.visibility = View.GONE
        binding!!.profileTv.visibility = View.VISIBLE
        Intercom.client().setLauncherVisibility(Intercom.Visibility.VISIBLE)

        viewModel!!.popup?.dismiss()
        selectedId = 4
    }

    fun selectPlace() {
        binding!!.homeSelectedItemPlacesTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.main_red_color
            )
        )
        binding!!.homeSelectedItemProfileTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )
        binding!!.homeSelectedItemOffersTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )
        binding!!.homeSelectedItemOrderNowTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.navigation_icon_color
            )
        )

        binding!!.homeBottomNavigationBar.show(3, true)
        binding!!.logoutIv.visibility = View.GONE
        binding!!.deliveryAreaIv.visibility = View.VISIBLE
        binding!!.deliveryAreaTv.visibility = View.VISIBLE
        binding!!.profileTv.visibility = View.GONE
        Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE)

        selectedId = 3
    }

    private fun setFragment(id: Int) {
        viewModel?.setShowCart(id != 4)
        when (id) {
            1 -> {
                supportFragmentManager.beginTransaction().hide(active).show(orderNowFragment)
                    .commit();
                active = orderNowFragment;
            }
            2 -> {
                supportFragmentManager.beginTransaction().hide(active).show(offersFragment)
                    .commit();
                active = offersFragment;
            }
            3 -> {
                supportFragmentManager.beginTransaction().hide(offersFragment).hide(active)
                    .show(placesFragment).commit()
                placesFragment.onResume()
                active = placesFragment;
            }
            4 -> {
                supportFragmentManager.beginTransaction().hide(active).hide(placesFragment)
                    .hide(offersFragment).show(profileFragment).commit()
                profileFragment.onResume()
                active = profileFragment;
            }
        }
    }

    override fun onRestart() {
        super.onRestart();
        if (SharedPreferencesManager.getInstance().isORdering) {//refresh page after filling address or selected area
            SharedPreferencesManager.getInstance().setOrdering(false)
            viewModel!!.fillAddressData()
            getActiveOrders()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }

        adddressFromComponent =
            Gson().fromJson(data?.getStringExtra(Constants.ADDRESS), AddAddressModel::class.java)

        if (requestCode == 11901 && resultCode == -1) {
            //for address change form overalay
            if (sharePref.cart != null) {
                if (adddressFromComponent?.area?._id == null || adddressFromComponent?.case!! > 1)
                    showClearCartDialog(sharePref.cart.placeModel?.name?.en, {
//                        viewModel!!.clearCart(this@HomeActivityKotlin, {})
                        viewModel?.setHaveCart()
                        updateAddress()
                    })
                else
                    addAddressViewModel.checkIfNotDelivering(
                        sharePref.cart.id,
                        adddressFromComponent?.area?._id
                    )
            } else
                updateAddress()
        }

        if (requestCode == Constants.RC_UPDATE_REQUEST) {
            if (resultCode != RESULT_OK) {
                QurbaLogger.logging(
                    this,
                    LEVEL_ERROR,
                    Constants.USER_VERSION_CHECKING_FAIL,
                    "Update flow failed! Result code: $resultCode"
                )
                // If the update is cancelled or fails,
                // you can request to start the update again.
                if (appSettingsModel?.isMandatoryUpdate == true)
                    checkVersionUpdate(null)
            }
        }

        if (requestCode == Constants.EDIT_PROFILE && resultCode == RESULT_OK) {
            refreshTabs()
            profileFragment.initialization()
        }

        if (requestCode == 34131 && resultCode == -1)
            adddressFromComponent ?: null?.let { viewModel?.onSelect(it) }

        if (requestCode == 1000 && adddressFromComponent == null)
            adddressFromComponent ?: null?.let { viewModel?.onSelect(it) }
    }

    private fun updateAddress(deliveryResponse: DeliveryValidationPayload?) {
        if (deliveryResponse == null) {
            showClearCartDialog(sharePref.cart.placeModel?.name?.en, {
//                viewModel?.clearCart(this, {})
                viewModel?.setHaveCart()
                updateAddress()
            })
        } else
            updateAddress()
    }

    private fun updateAddress() {
        val savedDeliveryAddressList: ArrayList<AddAddressModel> =
            SharedPreferencesManager.getInstance().savedDeliveryAddress

        if (!savedDeliveryAddressList.contains(adddressFromComponent))
            savedDeliveryAddressList.add(adddressFromComponent!!)
        SharedPreferencesManager.getInstance().setSavedDeliveryAddress(savedDeliveryAddressList)
        SharedPreferencesManager.getInstance().selectedCachedArea = adddressFromComponent!!

        viewModel?.sheet?.dismiss()
        viewModel?.fillAddressData()
        refreshTabs()
    }

    private fun getCart() {
        if(sharePref.cart != null)
            updateCart()

        viewModel!!.getCart(this) {
            updateCart()
        }
    }

    fun refreshTabs() {
        orderNowFragment.filterOrders()
        offersFragment.filterOffers()
        offersFragment.featuredPlaces
        placesFragment.filterPlaces()
        getCart()
    }
}
