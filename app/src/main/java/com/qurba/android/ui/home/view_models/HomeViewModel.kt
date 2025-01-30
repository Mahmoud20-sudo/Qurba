package com.qurba.android.ui.home.view_models

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.databinding.ActivityHomeBinding
import com.qurba.android.network.APICalls
import com.qurba.android.network.APICalls.Companion.getInstance
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.*
import com.qurba.android.network.models.response_models.AppSettingsModel
import com.qurba.android.ui.address_component.views.AddressActivity
import com.qurba.android.ui.cart.views.CartActivity
import com.qurba.android.ui.home.views.CachedDeliveryAreasFragment
import com.qurba.android.ui.home.views.HomeActivityKotlin
import com.qurba.android.ui.offers.views.OffersFragment
import com.qurba.android.ui.order_now.views.OrderNowFragment
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.text.NumberFormat
import java.util.*

class HomeViewModel(application: Application) : BaseViewModel(application), SelectAddressCallBack {
    private var showCart = true
    private var qty = 0
    private var totalPrice = 0
    private var isHaveCart = false
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var activity: BaseActivity? = null
    private var appSettignsSuscriber: Subscriber<Response<AppSettingsPayload>>? = null
    var deliveryAddress: ObservableField<String> = ObservableField<String>("")
    private var binding: ActivityHomeBinding? = null

    var ordersObservable: MutableLiveData<ArrayList<OrdersModel>>? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set

    var appSettingsObservable: MutableLiveData<AppSettingsModel>? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set

    var sheet: CachedDeliveryAreasFragment? = null
        private set

    var popup: PopupWindow? = null
        private set

    var ordersObservalble: MutableLiveData<List<OrdersModel>>? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set

    fun setBinding(binding: ActivityHomeBinding?) {
        this.binding = binding
    }

    fun updateCartQuantity() {
        setHaveCart()
        if (sharedPref.cart == null) return
        var qty = 0
        var totalPrice = 0
        val cartItems = sharedPref.cart.cartItems
        if (cartItems != null) for (cartItem in cartItems) {
            qty += cartItem.quantity
            totalPrice += cartItem.price * cartItem.quantity
        }
//        if (qty == 0) {
//            isHaveCart = false
//            notifyDataChanged()
//        }
        this.qty = qty
        this.totalPrice = totalPrice
    }

    @get:Bindable
    val quantity: String
        get() = NumberFormat.getInstance().format(qty.toLong())

    @Bindable
    fun getTotalPrice(): String {
        return if (SharedPreferencesManager.getInstance().language.equals(
                "ar",
                ignoreCase = true
            )
        ) NumberFormat.getInstance()
            .format(totalPrice.toLong()) + " " + activity!!.getString(R.string.currency) else activity!!.getString(
            R.string.currency
        ) + " " + NumberFormat.getInstance().format(totalPrice.toLong())
    }

    @Bindable
    fun isHaveCart(): Boolean {
        return isHaveCart
    }

    fun setShowCart(showCart: Boolean) {
        this.showCart = showCart
        setHaveCart()
    }

    fun fillAddressData() {
        val addAddressModel = sharedPref.selectedCachedArea
        deliveryAddress.set(
            (if (addAddressModel.label != null) addAddressModel.getLabel(activity!!) else addAddressModel.city!!.name.en
                    + ", " + addAddressModel.area!!.name.en)
        )
        Handler(Looper.getMainLooper()).postDelayed({ showPopUp() }, 100)
        notifyDataChanged()
    }

    private fun showPopUp() {
        popup!!.dismiss()
        if (sharedPref.selectedCachedArea != null && sharedPref.selectedCachedArea.case > 1 && (activity as HomeActivityKotlin?)!!.getSelectedId() != 3 && !sharedPref.isAreaVoted) {
            popup!!.showAsDropDown(
                binding!!.toolbar,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    90f,
                    QurbaApplication.getContext().resources.displayMetrics
                ).toInt(),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    -16f,
                    QurbaApplication.getContext().resources.displayMetrics
                ).toInt()
            )
            //            sharedPref.setIsAppRunning(true);
            voteArea(null)
        }
    }

    fun setPopup() {
        val inflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.unsupported_area_window, null)
        popup = PopupWindow(popupView)
        popup!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popup!!.elevation = 10f
        popup!!.width = ViewGroup.LayoutParams.MATCH_PARENT
        popup!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
        popupView.bringToFront()
        (popupView.findViewById<View>(R.id.not_deliver_title_tv) as TextView).text =
            (activity?.getString(R.string.not_deliver_main_hint) + " "
                    + sharedPref.selectedCachedArea.area!!.name.en
                    + " " + activity!!.getString(R.string.util_now))
        popupView.findViewById<View>(R.id.edit_location_tv).setOnClickListener { v: View? ->
            activity!!.startActivityForResult(Intent(activity, AddressActivity::class.java), 11901)
            popup!!.dismiss()
        }
        popupView.findViewById<View>(R.id.cancel_tv)
            .setOnClickListener { v: View? -> popup!!.dismiss() }
    }

    fun viewCartActivity(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            val intent = Intent(QurbaApplication.getContext(), CartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            QurbaApplication.getContext().startActivity(intent)
        }
    }

    val appSettings: Unit
        get() {
            if (SystemUtils.isNetworkAvailable(getApplication())) {
                val call = getInstance().appSettings
                appSettignsSuscriber = object : Subscriber<Response<AppSettingsPayload>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onNext(response: Response<AppSettingsPayload>) {
                        response.raw().body
                        val appSettingsPayload = response.body()
                        if (response.code() == 200) {
                            appSettingsObservable!!.postValue(appSettingsPayload!!.payload)
                        } else {
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                QurbaApplication.getContext().getString(R.string.something_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(appSettignsSuscriber)
            } else {
                Log.d("Guestttttttttt", "noc")
                Toast.makeText(
                    QurbaApplication.getContext(),
                    QurbaApplication.getContext().getString(R.string.no_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
    val activeOrders: Unit
        get() {
            if (SystemUtils.isNetworkAvailable(getApplication())) {
                logging(
                    QurbaApplication.getContext(),
                    Constants.USER_GET_ACTIVE_ORDERS_ATTEMPT,
                    LEVEL_INFO,
                    "User trying to retrieve active orders ",
                    ""
                )
                val call = getInstance().getActiveOrders(1)
                val subscriber: Subscriber<Response<OrdersPayload>> =
                    object : Subscriber<Response<OrdersPayload>>() {
                        override fun onCompleted() {}
                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            // Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_GET_ACTIVE_ORDERS_FAIL,
                                LEVEL_ERROR,
                                "Failed to retrieve active orders ",
                                e.stackTrace.toString()
                            )
                        }

                        override fun onNext(response: Response<OrdersPayload>) {
                            if (response.code() == 200) {
                                ordersObservalble!!.postValue(response.body()!!.payload.docs)
                                logging(
                                    QurbaApplication.getContext(),
                                    Constants.USER_GET_ACTIVE_ORDERS_SUCCESS,
                                    LEVEL_INFO, "Active orders has been successfully returned", ""
                                )
                            } else {
                                assert(response.errorBody() != null)
                                logging(
                                    QurbaApplication.getContext(),
                                    Constants.USER_GET_ACTIVE_ORDERS_FAIL,
                                    LEVEL_ERROR, "Failed to retrieve active orders",
                                    response.errorBody()!!.string()
                                )
                            }
                        }
                    }
                call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber)
            } else {
                Toast.makeText(
                    QurbaApplication.getContext(),
                    QurbaApplication.getContext().getString(R.string.no_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    fun changeDeliverAddressClick(): View.OnClickListener {
        return View.OnClickListener { v: View? -> showAddressSheet() }
    }

    private fun showAddressSheet() {
        sheet = CachedDeliveryAreasFragment()
        sheet!!.setSelectAddressCallBack(this)
        sheet!!.show(activity!!.supportFragmentManager, "CachedDeliveryAreasFragment")
    }

    fun setActivity(activity: BaseActivity?) {
        this.activity = activity
        setPopup()
    }

    override fun onSelect(deliveryResponse: AddAddressModel) {
        if (deliveryResponse == null) return
        sharedPref.setAreaVoting(false)
        showPopUp()
        setHaveCart()
        deliveryAddress.set((if (deliveryResponse.label != null) deliveryResponse?.getLabel(activity!!) else deliveryResponse?.city?.name?.en + ", " + deliveryResponse?.area?.name?.en))
        //update all tabs with new locations
        (activity as HomeActivityKotlin)?.refreshTabs()
    }

    fun setHaveCart() {
        isHaveCart = showCart && !sharedPref.cart?.cartItems.isNullOrEmpty()
        notifyDataChanged()
    }
}