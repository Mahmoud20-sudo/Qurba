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
import com.qurba.android.ui.cart.views.CartActivity
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.addOfferRequestData
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.NumberFormat

class CustomizOffersViewModel(application: Application) : CustomizationViewModel(application) {

    private var response: retrofit2.Response<BaseModel>? = null
    private var offersModel = OffersModel()
    private var isFromDetails = false
    private var activity: BaseActivity? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
    fun setActivity(activity: BaseActivity?) {
        this.activity = activity
    }

    @get:Bindable
    val title: String
        get() = if (offersModel.title != null) offersModel.title.en else ""

    @get:Bindable
    val offerType: String
        get() {
            if (offersModel.id == null) return ""
            return if (offersModel.type == Constants.OFFER_TYPE_DISCOUNT) {
                activity!!.getString(R.string.discount)
            } else if (offersModel.type == Constants.OFFER_TYPE_FREE_ITEMS) {
                activity!!.getString(R.string.items)
            } else if (offersModel.type.equals(
                    Constants.OFFER_TYPE_DISCOUNT_ON_ITEMS,
                    ignoreCase = true
                )
            ) activity!!.getString(R.string.on_items) else if (offersModel.type.equals(
                    Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU,
                    ignoreCase = true
                )
            ) activity!!.getString(R.string.on_menu) else activity!!.getString(R.string.delivery)
        }


    @get:Bindable
    val offerValue: String
        get() {
            if (offersModel.type == null) return ""
            return if ((offersModel.type == Constants.OFFER_TYPE_DISCOUNT) || (offersModel.type == Constants.OFFER_TYPE_DISCOUNT_ON_ITEMS)) {
                NumberUtils.getFinaleDiscountValue(offersModel.discountRatio)
            } else {
                activity!!.getString(R.string.free)
            }
        }

    @get:Bindable
    var offerPrice: String
        get() = if (sharedPref.language.equals(
                "ar",
                ignoreCase = true
            )
        ) (NumberFormat.getInstance()
            .format(if (offerPrice > 0) offerPrice else offersModel.basePrice.toDouble())
                + " " + activity!!.getString(R.string.currency)) else activity!!.getString(R.string.currency) + " " + NumberFormat.getInstance()
            .format(if (offerPrice > 0) offerPrice else offersModel.basePrice.toDouble())
        set(offerPrice) {
            super.offerPrice = offerPrice.toFloat()
        }

    @get:Bindable
    val isItemSelected: Boolean
        get() = offerPrice > 0 || offersModel.basePrice > 0

    @get:Bindable
    val startPrice: String
        get() {
            return if (sharedPref.language.equals(
                    "ar",
                    ignoreCase = true
                )
            ) (NumberFormat.getInstance().format(offersModel.price.toLong())
                    + " " + activity!!.getString(R.string.currency)) else activity!!.getString(R.string.currency) + " " + NumberFormat.getInstance()
                .format(offersModel.price.toLong())
        }

    @Bindable
    override fun isVariablePrice(): Boolean {
        return offersModel.pricing == null || offersModel.pricing.equals("fixed", ignoreCase = true)
    }

    @Bindable
    fun isReady(): Boolean {
        return isReady
    }

    @get:Bindable
    val offerDescription: String
        get() = offersModel.description

    fun setOffersModel(offersModel: OffersModel) {
        this.offersModel = offersModel
        setOfferPrice()
        checkIfHaveRequiredSections(offersModel.sectionGroups, offersModel.sections)
        notifyDataChanged()
        isVariablePrice = isVariablePrice
    }

    fun openCartScreen(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            //items has no price
            if (offerPrice == 0f) offerPrice = offersModel.price.toFloat()
            //            if (offersModel.getBasePrice() > 0)
//                offerPrice += offersModel.getBasePrice();

            v.isEnabled = false
            //generate hash-key
            generateHAshKey()
            offersModel.price = offerPrice.toInt()
            offersModel.sections = sectionItems
            if(!sharedPref.checkIfLimitQtyReached(activity, offersModel._id)) return@OnClickListener
            addOfferToCart(offersModel, v)
            if (isFromDetails) {
                val intent: Intent = Intent(v.context, CartActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                QurbaApplication.getContext().startActivity(intent)
            }
            logging(
                v.context, Constants.USER_OFFERS_CUSTOMIZATION_SELECT_SUCCESS, LEVEL_INFO,
                "User selecting offer customization action success", ""
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
                    if (offersModel.hashKey == null) sec.items[i]._id + sec.items[i].title.en else offersModel.hashKey + sec.items[i]._id + sec.items[i].title.en
                offersModel.hashKey = newHash
            }
        }
    }

    fun setFromDetails(fromDetails: Boolean) {
        isFromDetails = fromDetails
    }

    private fun cacheOffer(){
        for (sections in offersModel.sections) {
            sections.backupItems = null
            sections.setSelectedItems(null)
        }
        for (group in offersModel.sectionGroups) {
            group.sections.clear()
        }
        sharedPref.copyOrUpdate(offersModel)
    }

    private fun addOfferToCart(offersModel: OffersModel, v:View) {
        if (SystemUtils.isNetworkAvailable(activity)) {
            logging(
                activity!!,
                Constants.USER_ADD_OFFER_TO_CART_ATTEMPT, Line.LEVEL_INFO,
                "User trying to add offer to his cart ",
            )

            //lifecycleScope
            activity?.lifecycleScope?.launchWhenStarted {
                try {
                    response = APICalls.instance.addOffer(addOfferRequestData(offersModel))
                    cacheOffer()
                } catch (exception: Exception) {
                    v.isEnabled = true
                    sharedPref.removeItemFromCart(offersModel._id)
                    exception.message?.let { activity?.showToastMsg(it) }
                    logging(
                        activity!!,
                        Constants.USER_ADD_OFFER_TO_CART_FAIL, Line.LEVEL_ERROR,
                        "User failed to add to his cart ", exception.message
                    )
                }
                //for ui handling
                withContext(Dispatchers.Main) {
                    if (response?.isSuccessful == true)
                        logging(
                            activity!!,
                            Constants.USER_ADD_OFFER_TO_CART_SUCCESS, LEVEL_INFO,
                            "User successfully add offer to his cart ", ""
                        )
                    else {
                        sharedPref.removeItemFromCart(offersModel._id)
                        response?.errorBody()?.string()?.let { showErrorMsg(it,Constants.USER_ADD_OFFER_TO_CART_FAIL ,"User failed to add to his cart ") }
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