package com.qurba.android.ui.customization.view_models

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.lifecycleScope
import com.facebook.appevents.AppEventsConstants
import com.mazenrashed.logdnaandroidclient.models.Line
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.QurbaLogger.Companion.standardFacebookEventsLogging
import com.qurba.android.network.models.BaseModel
import com.qurba.android.network.models.OffersModel
import com.qurba.android.network.models.ProductData
import com.qurba.android.ui.cart.views.CartActivity
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.addOfferRequestData
import com.qurba.android.utils.extenstions.addProductRequestData
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import java.text.NumberFormat

class CustomizProductsViewModel(application: Application) : CustomizationViewModel(application) {
    private var productData = ProductData()
    private var isFromDetails = false
    private var activity: BaseActivity? = null
    private var response: retrofit2.Response<BaseModel>? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
    fun setActivity(activity: BaseActivity?) {
        this.activity = activity
    }

    @get:Bindable
    val title: String
        get() = if (productData.name != null) productData.name.en else ""

    @get:Bindable
    val offerType: String
        get() = ""


    @get:Bindable
    val isReady: Boolean
        get() = isReady

    @get:Bindable
    val offerValue: String
        get() = ""

    @get:Bindable
    val startPrice: String
        get() {
            var value = ""
            if (!isVariablePrice && offerPrice <= productData.price) value += activity?.getString(R.string.starting_from) + " "
            value += productData.price
            return if (SharedPreferencesManager.getInstance().language.equals(
                    "ar",
                    ignoreCase = true
                )
            ) value + " " + activity!!.getString(R.string.currency) else activity!!.getString(R.string.currency) + " " + value
        }

    @get:Bindable
    val productPrice: String
        get() = if (SharedPreferencesManager.getInstance().language.equals(
                "ar",
                ignoreCase = true
            )
        ) NumberFormat.getInstance()
            .format(if (offerPrice > 0) offerPrice else productData.basePrice.toDouble()) + " " + activity!!.getString(
            R.string.currency
        ) else activity!!.getString(R.string.currency) + " " + java.text.NumberFormat.getInstance()
            .format(if (offerPrice > 0) offerPrice else productData.basePrice.toDouble())

    @get:Bindable
    val isItemSelected: Boolean
        get() = offerPrice > 0 || productData.basePrice > 0

    @Bindable
    override fun isVariablePrice(): Boolean {
        return productData.pricing == null || productData.pricing.equals("fixed", ignoreCase = true)
    }

    @get:Bindable
    val desription: String
        get() = productData?.description?.en ?: ""

    fun setProductData(productData: ProductData) {
        this.productData = productData
        setOfferPrice()
        checkIfHaveRequiredSections(productData.sectionGroups, productData.sections)
    }

    fun openCartScreen(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            //items has no price
            if (offerPrice == 0f) offerPrice = productData.price.toFloat()
            //            if (productData.getBasePrice() > 0)
//                offerPrice += productData.getBasePrice();

            v.isEnabled = false
            //generate hash-key
            generateHAshKey()
            productData.price = offerPrice.toInt()
            productData.sections = sectionItems
            if (!sharedPref.checkIfLimitQtyReached(
                    activity,
                    productData._id
                )
            ) return@OnClickListener
            addProductToCart(productData, v)
            if (isFromDetails) {
                val intent = Intent(v.context, CartActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                QurbaApplication.getContext().startActivity(intent)
            }
            logging(
                v.context, Constants.USER_PRODUCT_CUSTOMIZATION_SELECT_SUCCESS, LEVEL_INFO,
                "User selecting product customization action success", ""
            )
            standardFacebookEventsLogging(
                v.context,
                AppEventsConstants.EVENT_NAME_CUSTOMIZE_PRODUCT
            )
        }
    }

    private fun generateHAshKey() {
        for (sec in sectionItems) {
            for (i in sec.items.indices) {
                val newHash =
                    if (productData.hashKey == null) sec.items[i]._id + sec.items[i].title.en else productData.hashKey + sec.items[i]._id + sec.items[i].title.en
                productData.hashKey = newHash
            }
        }
    }

    fun setFromDetails(fromDetails: Boolean) {
        isFromDetails = fromDetails
    }

    private fun cacheProduct() {
        for (sections in productData.sections) {
            sections.backupItems = null
            sections.setSelectedItems(null)
        }
        if (!productData.sectionGroups.isNullOrEmpty())
            for (group in productData.sectionGroups) {
                group.sections.clear()
            }

        SharedPreferencesManager.getInstance().copyOrUpdate(productData)
    }

    private fun addProductToCart(productData: ProductData, v: View) {
        if (SystemUtils.isNetworkAvailable(activity)) {

            logging(
                activity!!,
                Constants.USER_ADD_PRODUCT_TO_CART_ATTEMPT, Line.LEVEL_INFO,
                "User trying to add product to his cart ",
            )

            //lifecycleScope
            activity?.lifecycleScope?.launchWhenStarted {
                try {
                    response = APICalls.instance.addProduct(addProductRequestData(productData))
                    cacheProduct()
                } catch (exception: Exception) {
                    v.isEnabled = true
                    SharedPreferencesManager.getInstance().removeItemFromCart(productData._id)
                    exception.message?.let { activity?.showToastMsg(it) }
                    logging(
                        activity!!,
                        Constants.USER_ADD_PRODUCT_TO_CART_FAIL, Line.LEVEL_ERROR,
                        "User failed to add product to his cart ", exception.message
                    )
                }
                //for ui handling
                withContext(Dispatchers.Main) {
                    if (response?.isSuccessful == true)
                        logging(
                            activity!!,
                            Constants.USER_ADD_PRODUCT_TO_CART_SUCCESS, LEVEL_INFO,
                            "User successfully add product to his cart ", ""
                        )
                    else {
                        SharedPreferencesManager.getInstance().removeItemFromCart(productData._id)
                        response?.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_ADD_PRODUCT_TO_CART_FAIL,
                                "User failed to add product to his cart "
                            )
                        }
                    }
                    v.isEnabled = true
                    activity!!.finish()
                }
            }
        } else {
            activity?.showToastMsg(activity!!.getString(R.string.no_connection))
        }
    }

}