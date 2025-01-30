package com.qurba.android.utils.extenstions

import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.mazenrashed.logdnaandroidclient.models.Line
import com.qurba.android.BuildConfig
import com.qurba.android.R
import com.qurba.android.network.QurbaLogger
import com.qurba.android.network.models.*
import com.qurba.android.network.models.request_models.CreateOrdersPayload
import com.qurba.android.network.models.request_models.CreateOrdersRequestModel
import com.qurba.android.network.models.response_models.AppSettingsModel
import com.qurba.android.utils.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

fun BaseActivity.changeStatusBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= 21) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, color)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val decorView = window.decorView
        val wic = WindowInsetsControllerCompat(window, decorView)
        wic.isAppearanceLightStatusBars = true // true or false as desired.

        // And then you can set any background color to the status bar.
        window.statusBarColor = Color.WHITE
    }
}

fun delayFunction(function: () -> Unit, delay: Long) {
    Handler().postDelayed(function, delay)
}

fun Fragment.shortToast(text: String) {
    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
}

fun Fragment.longToast(text: String) {
    Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
}

fun FragmentActivity.shortToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun FragmentActivity.showToastMsg(msg: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

fun showErrorMsg(responseCallback: String, eventName: String, eventMsg: String) {
    var jObjError: JSONObject? = null
    var errorMsg: String? = null

    try {
        val error: String = responseCallback
        if (error.contains("error", false)) {
            jObjError = JSONObject(error)

            when (SharedPreferencesManager.getInstance().language) {
                "ar" -> when {
                    jObjError.has("ar") -> errorMsg = jObjError.get("ar").toString()
                    jObjError.has("en") -> errorMsg = jObjError.get("en").toString()
                }
                else -> when {
                    jObjError.has("en") -> errorMsg = jObjError.get("en").toString()
                    jObjError.has("ar") -> errorMsg = jObjError.get("ar").toString()
                }
            }
        } else
            errorMsg = error

        QurbaLogger.logging(
            QurbaApplication.getContext(),
            eventName,
            Line.LEVEL_ERROR, eventMsg, errorMsg
        )
    } catch (ex: Exception) {
        QurbaLogger.logging(
            QurbaApplication.getContext(),
            eventName,
            Line.LEVEL_ERROR,
            eventMsg,
            errorMsg ?: ex.stackTraceToString()
        )
    } finally {
        Toast.makeText(
            QurbaApplication.getContext(),
            errorMsg ?: QurbaApplication.getContext()
                .getString(R.string.something_wrong), Toast.LENGTH_SHORT
        ).show()
    }
}

fun Fragment.addBottomSheetFragment(fragment: BottomSheetDialogFragment, fragmentTag: String) {
    if (!fragment!!.isAdded) {
        if (fragment != null && fragment!!.isAdded)
            return;
        if (!fragment!!.isVisible) fragment!!.show(activity!!.supportFragmentManager, fragmentTag)
    }
}

fun setGradientTextColor(textView: AppCompatTextView) {
//    val paint = textView.paint
//    val width = paint.measureText(textView.text.toString())
//    val textShader: Shader = LinearGradient(0f, 0f, width, textView.textSize, intArrayOf(
//        Color.parseColor("#fa6400"),
//        Color.parseColor("#db1f30")
//    ), null, Shader.TileMode.CLAMP)
//
//    textView.paint.shader = textShader

    val paint: TextPaint = textView.paint
    val width = paint.measureText(textView.text.toString())
    val textShader: Shader = LinearGradient(
        0f, 0f, width, textView.textSize, intArrayOf(
            Color.parseColor("#fa6400"), Color.parseColor("#db1f30")
        ), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
    )
    textView.paint.shader = textShader
    textView.setTextColor(Color.parseColor("#fa6400"))
}

fun addOfferRequestData(offersModel: OffersModel): CreateOrdersRequestModel {
    val payload = CreateOrdersPayload()
    payload.offer = offersModel._id
    payload.place = offersModel.placeId._id
    payload.area = SharedPreferencesManager.getInstance().selectedCachedArea.area?._id

    val sectionsList = ArrayList<OfferIngradients>()
    val sectionsGroupList = ArrayList<SectionsGroupRequests>()
    if (!offersModel.sections.isNullOrEmpty()) {
        for (section in offersModel.sections) {
            if (section.sectionsGroup == null) {
                val sec = OfferIngradients()
                sec.section = section._id
                val itemsList = ArrayList<String>()
                for (item in section.items) {
                    itemsList.add(item._id)
                }
                sec.itemsIds = itemsList
                sectionsList.add(sec)
            } else {
                val group = SectionsGroupRequests()
                group.sectionGroup = section.sectionsGroup._id
                val sectionsGradient = OfferIngradients()
                sectionsGradient.section = section._id
                val itemsList = ArrayList<String>()
                for (item in section.items) {
                    itemsList.add(item._id)
                }
                sectionsGradient.itemsIds = itemsList
                group.sections.add(sectionsGradient)
                sectionsGroupList.add(group)
            }
        }

        payload.sectionGroups = sectionsGroupList
        payload.sections = sectionsList
    }

    val requestModel = CreateOrdersRequestModel()
    requestModel.payload = payload
    return requestModel
}

fun addProductRequestData(productData: ProductData): CreateOrdersRequestModel {
    val payload = CreateOrdersPayload()
    payload.product = productData._id
    payload.place = productData.placeInfo._id
    payload.area = SharedPreferencesManager.getInstance().selectedCachedArea.area?._id

    val sectionsList = ArrayList<OfferIngradients>()
    val sectionsGroupList = ArrayList<SectionsGroupRequests>()

    if (!productData.sections.isNullOrEmpty()) {
        for (section in productData.sections) {
            if (section.sectionsGroup == null) {
                val sec = OfferIngradients()
                sec.section = section._id
                val itemsList = ArrayList<String>()
                for (item in section.items) {
                    itemsList.add(item._id)
                }
                sec.itemsIds = itemsList
                sectionsList.add(sec)
            } else {
                val group = SectionsGroupRequests()
                group.sectionGroup = section.sectionsGroup._id
                val sectionsGradient = OfferIngradients()
                sectionsGradient.section = section._id
                val itemsList = ArrayList<String>()
                for (item in section.items) {
                    itemsList.add(item._id)
                }
                sectionsGradient.itemsIds = itemsList
                group.sections.add(sectionsGradient)
                sectionsGroupList.add(group)
            }
        }

        payload.sectionGroups = sectionsGroupList
        payload.sections = sectionsList
    }

    val requestModel = CreateOrdersRequestModel()
    requestModel.payload = payload
    return requestModel
}

fun SearchView.SearchAutoComplete.addRxTextWatcher(): rx.Observable<String?> {

    return rx.Observable.unsafeCreate<String?> {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                it.onNext(s?.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
        })
    }
}


fun BaseActivity.checkVersionUpdate(appSettingsModel: AppSettingsModel?) {
    // Creates instance of the manager.
    val appUpdateManager = AppUpdateManagerFactory.create(this)
    // Returns an intent object that you use to check for an update.
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo
    val updateType =
        if (appSettingsModel?.isMandatoryUpdate == true) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE

    // Checks that the platform will allow the specified type of update.
    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
        // For a flexible update, use AppUpdateType.FLEXIBLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
        {
            // Request the update.
            appUpdateManager.startUpdateFlowForResult(
                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                appUpdateInfo,
                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                updateType,
                // The current activity making the update request.
                this,
                // Include a request code to later monitor this update request.
                Constants.RC_UPDATE_REQUEST
            )
        } else
            checkAppVersion(appSettingsModel)
    }
}

private fun BaseActivity.checkAppVersion(appSettingsModel: AppSettingsModel?) {
    if (BuildConfig.DEBUG) return
    val versionChecker = VersionChecker()
    try {
        val versionName = BuildConfig.VERSION_NAME
        QurbaLogger.logging(
            QurbaApplication.getContext(),
            Constants.USER_VERSION_CHECKING_ATTEMPT, Line.LEVEL_INFO,
            "User current version is: $versionName", versionName
        )
        val latestVersion = versionChecker.execute().get()
        if (latestVersion != null && latestVersion.isNotEmpty()) {
            QurbaLogger.logging(
                QurbaApplication.getContext(),
                Constants.USER_VERSION_CHECKING_SUCCESS, Line.LEVEL_INFO,
                "User current version is: $versionName and latest version is $latestVersion", ""
            )

            if (latestVersion != versionName) {
                showUpdateDialog(appSettingsModel)
            }
        } else
            QurbaLogger.logging(
                QurbaApplication.getContext(),
                Constants.USER_VERSION_CHECKING_FAIL, Line.LEVEL_ERROR,
                "User failed to retrieve latest version", "latest version is null"
            )
    } catch (e: Exception) {
        e.printStackTrace()
        QurbaLogger.logging(
            QurbaApplication.getContext(),
            Constants.USER_VERSION_CHECKING_FAIL, Line.LEVEL_ERROR,
            "User failed to retrieve latest version", Log.getStackTraceString(e)
        )
    }
}

/**
 * add fragments for navigation
 * @param fragment is the new fragment to be added
 */
//fun FragmentActivity.addFragment(fragment: Fragment) {
//    supportFragmentManager.beginTransaction()
//            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
//            .add(R.id.container, fragment)
//            .commit()
//}

fun BaseActivity.addFragment(fragment: Fragment, resId: Int) {
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        .add(resId, fragment)
        .addToBackStack(fragment.javaClass.name)
        .commit()
}

fun BaseActivity.addFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        .add(R.id.container, fragment)
        .addToBackStack(fragment.javaClass.name)
        .commit()
}

fun BaseActivity.addFragmentWithotBackStack(fragment: Fragment, resId: Int) {
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        .add(resId, fragment)
        .commit()
}

/**
 * replace fragments for navigation
 * @param fragment is the new fragment to be replaced
 */
fun BaseActivity.replaceFragment(fragment: Fragment, resId: Int) {
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        .replace(resId, fragment)
        .commit()
}

fun BaseActivity.replaceFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .replace(R.id.container, fragment)
        .commit()
}

/**
 * add fragments for navigation
 * @param fragment is the new fragment to be added
 */
fun Fragment.addFragment(resId: Int, fragment: Fragment) {
    parentFragment!!.childFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        .add(resId, fragment)
        .addToBackStack(fragment.javaClass.name)
        .commit()
}

fun Fragment.replaceFragmentWithoutAnime(resId: Int, fragment: Fragment) {
    childFragmentManager!!.beginTransaction()
        .add(resId, fragment)
        .addToBackStack(null)
        .commit()
}

fun BaseActivity.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

/**
 * open soft keyboard and add focus on EditText
 * @param editText to ad focus on
 */
fun FragmentActivity.openKeyboard(editText: EditText) {
    val inputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInputFromWindow(
        editText.applicationWindowToken,
        InputMethodManager.SHOW_FORCED,
        0
    )
    editText.requestFocus()
}

/**
 * close current fragment
 */
fun Fragment.closeFragment() {
    activity?.onBackPressed()
}

/**
 * set new title on toolbar
 * @param title is the new title to be added
 */
//fun Fragment.setTitle(title: String) {
//    (activity as FragmentActivity..supportActionBar!!.title = title
//    Singleton.instance.titles!!.push(title)
//}

/**
 * Format double variables to a number of after floating point
 */
fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun BaseActivity.setInentResult(intent: Intent){
    try {
        setResult(-1, intent)
    }catch (e:Exception){
        e.message?.let { Log.e(packageName, it) }
    }
}
