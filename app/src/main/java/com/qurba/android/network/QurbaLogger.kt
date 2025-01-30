package com.qurba.android.network

import android.R.id
import android.content.Context
import android.os.Bundle
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.mazenrashed.logdnaandroidclient.BuildConfig
import com.mazenrashed.logdnaandroidclient.LogDna
import com.mazenrashed.logdnaandroidclient.models.Line
import com.qurba.android.network.models.LogglyModel
import com.qurba.android.utils.SharedPreferencesManager
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*


class QurbaLogger {
    companion object {
        fun logging(context: Context, eventName: String, level: String, message: String, payload: String? = "") {

            val model = LogglyModel()

            val s = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            s.timeZone = TimeZone.getTimeZone("UTC")
            val format: String = s.format(Date())

            model.timestamp = format
            model.version = com.qurba.android.BuildConfig.VERSION_NAME
            model.process = "android"
            model.message = message
            model.model = eventName
            model.level = level
            model.userId = SharedPreferencesManager.getInstance().user?._id

            LogDna.log(
                    Line.Builder().setLine(message)
                            .addCustomField(Line.CustomField("version", BuildConfig.VERSION_NAME))
                            .addCustomField(Line.CustomField("eventName", eventName))
                            .addCustomField(Line.CustomField("payload", payload ?: ""))
                            .addCustomField(Line.CustomField("userId",
                                SharedPreferencesManager.getInstance().user?._id
                                    ?: ""))
                            .setLevel(level.toUpperCase())
                            .setTime(System.currentTimeMillis())
                            .build()
            )

            val bundle = Bundle()
            FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle)

            val logger = AppEventsLogger.newLogger(context)
            logger.logEvent(eventName);
            logger.flush()
        }

        fun standardFacebookEventsLogging(context: Context, eventName: String, parameterValue: Double, bundle: Bundle) {
            val logger = AppEventsLogger.newLogger(context)
            logger.logEvent(eventName, parameterValue, bundle);
            logger.flush()
        }

        fun standardFacebookEventsLogging(context: Context, eventName: String) {
            val logger = AppEventsLogger.newLogger(context)
            logger.logEvent(eventName);
            logger.flush()
        }

        fun standardFacebookEventsLogging(context: Context, eventName: String, bundle: Bundle) {
            val logger = AppEventsLogger.newLogger(context)
            logger.logEvent(eventName, bundle);
            logger.flush()
        }

        fun standardFacebookEventsLoggingPurchase(context: Context, amount: Double, bundle: Bundle) {
            val logger = AppEventsLogger.newLogger(context)
            logger.logPurchase(BigDecimal.valueOf(amount), Currency.getInstance("EGP"), bundle);
            logger.flush()
        }

        fun standardFirebaseAnalyticsEventsLogging(context: Context, eventName: String, bundle: Bundle) {
            FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle)
        }
    }
}