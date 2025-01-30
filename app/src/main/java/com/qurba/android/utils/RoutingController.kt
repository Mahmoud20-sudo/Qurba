package com.qurba.android.utils

import android.content.Intent
import android.util.Log
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.ui.address_component.views.AddressActivity
import com.qurba.android.ui.home.views.HomeActivityKotlin
import com.qurba.android.ui.my_orders.views.MyOrderActivity
import com.qurba.android.ui.offers.views.OfferDetailsActivity
import com.qurba.android.ui.on_boarding.views.OnBoardingActivity
import com.qurba.android.ui.order_status.views.OrderStatusActivity
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity
import com.qurba.android.ui.products.views.ProductDetailsActivity
import com.qurba.android.utils.SystemUtils.containsDigit
import org.json.JSONException
import org.json.JSONObject


class RoutingController(private val activity: BaseActivity) {
    private var referringParams = JSONObject()
    private var intent: Intent? = null
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()
    fun setReferringParams(referringParams: JSONObject) {
        this.referringParams = referringParams
    }

    fun routeAppWithoutBranch(url: String) {
        when {
            url.replace("/", "").contains("order_now") -> {
                intent = Intent(activity, HomeActivityKotlin::class.java)
                intent!!.putExtra(Constants.SELECTED_TAB, Constants.ORDER_NOW)
            }
            url.contains("orders") -> {
                if (containsDigit(url)) {
                    intent = Intent(activity, OrderStatusActivity::class.java)
                    intent?.putExtra(
                        Constants.ORDER_ID,
                        url?.split("/orders/".toRegex())?.toTypedArray()[1]
                    )
                } else
                    intent = Intent(activity, MyOrderActivity::class.java)

            }
            url.contains("comments") -> {
                if (url.contains("offers")) {
                    intent = Intent(activity, OfferDetailsActivity::class.java)
                    intent!!.putExtra(
                        "offer_id",
                        url?.split("/offers/".toRegex())
                            ?.toTypedArray()[1]?.split("/comments/".toRegex())?.toTypedArray()[0]
                    )
//                    intent!!.putExtra("comments-page", url?.substring(url?.trim().length - 1))
                    intent!!.putExtra("includeDelimiter", true)

                    if (url.contains("replies")) {
                        intent!!.putExtra(
                            "reply-id",
                            url?.split("/comments/".toRegex())
                                ?.toTypedArray()[1]?.split("/replies/".toRegex())
                                ?.toTypedArray()[1]?.split("/?page".toRegex())
                                ?.toTypedArray()[0]?.replace("/", "")
                        )
                        intent!!.putExtra(
                            "comment-id",
                            url?.split("/offers/".toRegex())
                                ?.toTypedArray()[1]?.split("/comments/".toRegex())
                                ?.toTypedArray()[1]?.split("/replies/".toRegex())
                                ?.toTypedArray()[0]?.replace("/", "")
                        )
                    } else
                        intent!!.putExtra(
                            "comment-id",
                            url?.split("/offers/".toRegex())
                                ?.toTypedArray()[1]?.split("/comments/".toRegex())
                                ?.toTypedArray()[1]?.split("/?page".toRegex())
                                ?.toTypedArray()[0]?.replace("/", "")
                        )
                }
                if (url.contains("places")) {
                    intent = Intent(activity, PlaceDetailsActivity::class.java)
                    intent!!.putExtra(
                        "place_id",
                        url?.split("/places/".toRegex())
                            ?.toTypedArray()[1]?.split("/comments/".toRegex())?.toTypedArray()[0]
                    )
//                    intent!!.putExtra("comments-page", url?.substring(url?.trim().length - 1))
                    intent!!.putExtra("includeDelimiter", true)

                    if (url.contains("replies")) {
                        intent!!.putExtra(
                            "reply-id",
                            url?.split("/comments/".toRegex())
                                ?.toTypedArray()[1]?.split("/replies/".toRegex())
                                ?.toTypedArray()[1]?.split("/?page".toRegex())
                                ?.toTypedArray()[0]?.replace("/", "")
                        )
                        intent!!.putExtra(
                            "comment-id",
                            url?.split("/places/".toRegex())
                                ?.toTypedArray()[1]?.split("/comments/".toRegex())
                                ?.toTypedArray()[1]?.split("/replies/".toRegex())
                                ?.toTypedArray()[0]?.replace("/", "")
                        )
                    } else
                        intent!!.putExtra(
                            "comment-id",
                            url?.split("/places/".toRegex())
                                ?.toTypedArray()[1]?.split("/comments/".toRegex())
                                ?.toTypedArray()[1]?.split("/?page".toRegex())
                                ?.toTypedArray()[0]?.replace("/", "")
                        )
                }
                if (url.contains("products")) {
                    intent = Intent(activity, ProductDetailsActivity::class.java)
                    intent!!.putExtra(
                        Constants.PRODUCT_ID,
                        url?.split("/products/".toRegex())
                            ?.toTypedArray()[1]?.split("/comments/".toRegex())?.toTypedArray()[0]
                    )
//                    intent!!.putExtra("comments-page", url?.substring(url?.trim().length - 1))
                    intent!!.putExtra("includeDelimiter", true)

                    if (url.contains("replies")) {
                        intent!!.putExtra(
                            "reply-id",
                            url?.split("/comments/".toRegex())
                                ?.toTypedArray()[1]?.split("/replies/".toRegex())
                                ?.toTypedArray()[1]?.split("/?page".toRegex())
                                ?.toTypedArray()[0]?.replace("/", "")
                        )
                        intent!!.putExtra(
                            "comment-id",
                            url?.split("/products/".toRegex())
                                ?.toTypedArray()[1]?.split("/comments/".toRegex())
                                ?.toTypedArray()[1]?.split("/replies/".toRegex())
                                ?.toTypedArray()[0]?.replace("/", "")
                        )
                    } else
                        intent!!.putExtra(
                            "comment-id",
                            url?.split("/products/".toRegex())
                                ?.toTypedArray()[1]?.split("/comments/".toRegex())
                                ?.toTypedArray()[1]?.split("/?page".toRegex())
                                ?.toTypedArray()[0]?.replace("/", "")
                        )
                }
            }
            url.contains("places") -> { //all places case
                if (url.replace("/", "").endsWith("places")) {
                    intent = Intent(activity, HomeActivityKotlin::class.java)
                    intent!!.putExtra(Constants.SELECTED_TAB, Constants.PLACES)

                } else {
                    intent = Intent(activity, PlaceDetailsActivity::class.java)
                    when {
                        url.endsWith("offers") -> {
                            intent!!.putExtra(Constants.ORDER_TYPE, Constants.OFFERS)
                            intent!!.putExtra(
                                "place_id",
                                url?.split("/places/".toRegex())
                                    ?.toTypedArray()[1]?.split("/".toRegex())?.toTypedArray()[0]
                            )
                        }
                        url.endsWith("products") -> {
                            intent!!.putExtra(Constants.ORDER_TYPE, Constants.PRODUCTS)
                            intent!!.putExtra(
                                "place_id",
                                url?.split("/places/".toRegex())
                                    ?.toTypedArray()[1]?.split("/".toRegex())?.toTypedArray()[0]
                            )
                        }
                        else -> intent!!.putExtra(
                            "place_id",
                            url?.split("/places/".toRegex())?.toTypedArray()[1]?.replace("/", "")
                        )
                    }
                }
            }
            url.contains("offers") -> {
                if (url.replace("/", "").endsWith("offers")) {
                    intent = Intent(activity, HomeActivityKotlin::class.java)
                    intent!!.putExtra(Constants.SELECTED_TAB, Constants.OFFERS)
                } else {
                    intent = Intent(activity, OfferDetailsActivity::class.java)
                    intent!!.putExtra(
                        "offer_id",
                        url?.split("/offers/".toRegex())?.toTypedArray()[1]?.replace("/", "")
                    )
                }
            }
            url.contains("products") -> {
                intent = Intent(activity, ProductDetailsActivity::class.java)
                intent!!.putExtra(
                    Constants.PRODUCT_ID,
                    url?.split("/products/".toRegex())?.toTypedArray()[1]?.replace("/", "")
                )
            }
        }
        intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
    }

    fun routeApp() {
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (sharedPreferencesManager.isFirstTimeRunning) {
            intent = Intent(activity, OnBoardingActivity::class.java)
            intent!!.putExtra(Constants.BRANCH_OBJECT, referringParams.toString())
            activity.startActivity(intent)
            activity.finish()
            return
        }
        if (sharedPreferencesManager.selectedCachedArea == null) {
            intent = Intent(activity, AddressActivity::class.java)
            intent!!.putExtra(Constants.BRANCH_OBJECT, referringParams.toString())
            activity.startActivity(intent)
            activity.finish()
            return
        }
        try {
            val tapName = referringParams.getString("appTabName")
            when {
                tapName.equals(Constants.PLACES, false) -> {
                    if (!referringParams.has("model")) {
                        intent = Intent(activity, HomeActivityKotlin::class.java)
                        intent?.putExtra(Constants.SELECTED_TAB, Constants.PLACES)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    } else {
                        intent = Intent(activity, PlaceDetailsActivity::class.java)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent?.putExtra("id", referringParams.getString("id"))
                        intent?.putExtra(
                            "place_id",
                            if (referringParams.has("id")) referringParams.getString("id") else ""
                        )
                        if (referringParams.has("modelTabName")) {
                            intent?.putExtra(
                                Constants.ORDER_TYPE,
                                if (referringParams.getString("modelTabName")
                                        .equals(Constants.OFFERS, ignoreCase = true)
                                ) Constants.OFFERS else Constants.PRODUCTS
                            ) //reviews tab will be added later
                        }
                    }
                }
                tapName.equals(Constants.OFFERS, false) -> {
                    if (!referringParams.has("model")) {
                        intent = Intent(activity, HomeActivityKotlin::class.java)
                        intent?.putExtra(Constants.SELECTED_TAB, Constants.OFFERS)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    } else {
                        intent = Intent(activity, OfferDetailsActivity::class.java)
                        intent?.putExtra("offer_id", referringParams.getString("id"))
                    }
                }
                tapName.equals(Constants.PRODUCTS, false) -> {
                    intent = Intent(activity, ProductDetailsActivity::class.java)
                    intent?.putExtra(Constants.PRODUCT_ID, referringParams.getString("id"))
                }
                tapName.equals(Constants.ORDERS, false) -> {
                    intent = Intent(activity, OrderStatusActivity::class.java)
                    intent?.putExtra(Constants.ORDER_ID, referringParams.getString("id"))
                }
                tapName.equals(Constants.ORDER_NOW, false) -> {
                    intent = Intent(activity, HomeActivityKotlin::class.java)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent?.putExtra(Constants.SELECTED_TAB, Constants.ORDER_NOW)
                }
                else -> {
                    intent = Intent(activity, HomeActivityKotlin::class.java)
                }
            }

            activity.startActivity(intent)
            logging(
                QurbaApplication.getContext(),
                Constants.USER_BRANCH_DEEP_LINK_SUCCESS,
                LEVEL_INFO, "Branch link is contain json and routing is worked and all is good" +
                        " ", referringParams.toString()
            )
        } catch (e: JSONException) {
            Log.e("Message", e.localizedMessage)
            intent = Intent(activity, HomeActivityKotlin::class.java)
            activity.startActivity(intent)
            logging(
                QurbaApplication.getContext(),
                Constants.USER_BRANCH_DEEP_LINK_FAIL,
                LEVEL_ERROR, "Branch link is crashed and routing is failed" +
                        " " + referringParams.toString(), e.localizedMessage
            )
        }
    }
}