package com.qurba.android.ui.place_details.view_models

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.facebook.appevents.AppEventsConstants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.network.APICalls.Companion.getInstance
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.QurbaLogger.Companion.standardFacebookEventsLogging
import com.qurba.android.network.models.CommentModel
import com.qurba.android.network.models.PlaceModel
import com.qurba.android.network.models.SimilarPlacesPayload
import com.qurba.android.network.models.response_models.DeliveryValidationPayload
import com.qurba.android.network.models.response_models.PlaceDetailsHeaderPayload
import com.qurba.android.network.models.response_models.PlaceDetailsHeaderResponse
import com.qurba.android.network.models.response_models.StringOnlyResponse
import com.qurba.android.ui.cart.views.CartActivity
import com.qurba.android.ui.comments.views.OverlayFragment
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.showErrorMsg
import io.nlopez.smartlocation.SmartLocation
import org.json.JSONObject
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.NumberFormat

class PlaceDetailsBrandNewViewModel(application: Application) : BaseViewModel(application), SocialLoginCallBack, CommentsCallBack {
    private var subscriber: Subscriber<Response<PlaceDetailsHeaderResponse>>? = null
    private var folloPlaceSubscriber: Subscriber<Response<StringOnlyResponse>>? = null
    private var unFolloPlaceSubscriber: Subscriber<Response<StringOnlyResponse>>? = null
    private var similarPlacesPayloadSubscriber: Subscriber<Response<SimilarPlacesPayload>>? = null
    private var deliveryAreaPayObservable: MutableLiveData<DeliveryValidationPayload>? = null
    private var placeDetailsObservable: MutableLiveData<PlaceDetailsHeaderPayload>? = null
    private var similarPlacesObservable: MutableLiveData<List<PlaceModel>>? = null
    private var activity: BaseActivity? = null
    private var isLoading = false

    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()
    var placeDetailsModel = PlaceDetailsHeaderPayload()
        private set

    @get:Bindable
    var isLocationEnabled = SystemUtils.isGPSActive(QurbaApplication.getContext())
        get() = field && distance.isEmpty()
        set(locationEnabled) {
            field = locationEnabled
            notifyDataChanged()
        }
    private var distance = calculateDistance()

    fun setActivity(activity: BaseActivity?) {
        this.activity = activity
    }

    @Bindable
    fun getDistance(): String {
        return distance
    }

    @Bindable
    override fun isLoading(): Boolean {
        return isLoading
    }

    fun setLoading(loading: Boolean) {
        isLoading = loading
        notifyDataChanged()
    }

    @get:Bindable
    val ratersCount: String
        get() = if (placeDetailsModel.numberOfReviews > 0) "(" + placeDetailsModel.numberOfReviews + ")" else ""

    @get:Bindable
    val isOpenNow: Boolean
        get() = placeDetailsModel.isOpenNow

    @get:Bindable
    val isPlaceOpen: Boolean
        get() = placeDetailsModel.isPlaceOpen

    @get:Bindable
    val isDeliveringToArea: Boolean
        get() = placeDetailsModel.isDeliveringToArea

    @get:Bindable
    val availability: String
        get() = if (placeDetailsModel.availability != null) placeDetailsModel.availability.en else ""

    @get:Bindable
    val priceRating: String
        get() = when (placeDetailsModel.priceRating.toInt()) {
            0 -> ""
            1 -> "$"
            2 -> "$$"
            3 -> "$$$"
            4 -> "$$$$"
            else -> "$$$$$"
        }

    @get:Bindable
    val isLikedByUser: Boolean
        get() = placeDetailsModel.isLikedByUser

    @get:Bindable
    val isHaveRatings: Boolean
        get() = placeDetailsModel.placeRating > 0

    @get:Bindable
    val isHaveSocial: Boolean
        get() = isHaveLikeCounts || isHaveCommtsCounts || isHaveShareCounts

    @get:Bindable
    val isHaveLikeCounts: Boolean
        get() = placeDetailsModel.likesCount > 0

    @get:Bindable
    val isHaveShareCounts: Boolean
        get() = placeDetailsModel.numberOfShares > 0

    @get:Bindable
    val isHaveCommtsCounts: Boolean
        get() = placeDetailsModel.numberOfComments > 0

    @get:Bindable
    val commentsCounts: String
        get() = NumberFormat.getInstance().format(placeDetailsModel.numberOfComments.toLong())

    @get:Bindable
    val shareCounts: String
        get() = NumberFormat.getInstance().format(placeDetailsModel.numberOfShares.toLong())

    @get:Bindable
    val likeCounts: String
        get() = NumberFormat.getInstance().format(placeDetailsModel.likesCount.toLong())

    fun setDistance(distance: String) {
        this.distance = distance
        notifyDataChanged()
    }

    fun getSimilarPlaces(_id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
//            setLoading(true);
            logging(QurbaApplication.getContext(),
                    Constants.USER_GET_SIMILAR_PLACES_ATTEMPT,
                    LEVEL_INFO, "User trying to retrieve similar places", "")

//            except[]=5c7e8e92618a650017ef3236'
            val call = getInstance().getSimilarPlaces(_id!!)
            similarPlacesPayloadSubscriber = object : Subscriber<Response<SimilarPlacesPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
                    logging(QurbaApplication.getContext(),
                            Constants.USER_GET_SIMILAR_PLACES_FAIL,
                            LEVEL_ERROR, "Failed to retrieve similar places", e.stackTrace.toString())
                }

                override fun onNext(response: Response<SimilarPlacesPayload>) {
                    if (response.code() == 200) {
                        similarPlacesObservable!!.postValue(response.body()!!.payload.docs)
                        logging(QurbaApplication.getContext(),
                                Constants.USER_GET_SIMILAR_PLACES_SUCCESS,
                                LEVEL_INFO, "Successfully retrieve similar places", "")
                    } else {
                        response.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_GET_SIMILAR_PLACES_FAIL,
                                "Failed to retrieve similar places"
                            )
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(similarPlacesPayloadSubscriber)
        } else {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
        }
    }

    fun getPlaceDetails(_id: String?) {
        setLoading(true)

        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            logging(QurbaApplication.getContext(),
                    Constants.USER_RETRIEVE_PLACE_DETAILS_ATTEMPT,
                    LEVEL_INFO, "User trying to retrieve place details", "")

            val call = getInstance().getPlaceDetailsHeader(_id ?: "")
            subscriber = object : Subscriber<Response<PlaceDetailsHeaderResponse>>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
                    logging(QurbaApplication.getContext(),
                            Constants.USER_RETRIEVE_PLACE_DETAILS_FAIL,
                            LEVEL_ERROR, "Failed to retrieve place details", e.stackTrace.toString())
                }

                override fun onNext(response: Response<PlaceDetailsHeaderResponse>) {
                    if (response.code() == 200 && response.body()?.payload?._id != null) {
                        placeDetailsModel = response.body()!!.payload
                        detailsObservable?.postValue(placeDetailsModel)

                        calculateDistance()
                        notifyDataChanged()
                        logViewContentEvent(placeDetailsModel)
                        logging(QurbaApplication.getContext(),
                                Constants.USER_RETRIEVE_PLACE_DETAILS_SUCCESS,
                                LEVEL_INFO, "Successfully retrieve place details", "")

                        setLoading(false)
                    } else {
                        response?.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_RETRIEVE_PLACE_DETAILS_FAIL,
                                "Failed to retrieve place details "
                            )
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber)
        } else {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
        }
    }

    fun calculateDistance(): String {
        if (ContextCompat.checkSelfPermission(QurbaApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return ""
        if (placeDetailsModel.location == null) return ""
        SmartLocation.with(QurbaApplication.getContext()).location()
                .oneFix()
                .start { location: Location ->
                    setDistance(LocationUtils.calculateDistance(location.latitude, location.longitude,
                            placeDetailsModel.location.coordinates[1].toDouble(),
                            placeDetailsModel.location.coordinates[0].toDouble()) + " " + activity!!.getString(R.string.km))
                }
        return ""
    }

    fun likeDisLikePlace(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            if (sharedPreferencesManager.token == null || sharedPreferencesManager.isGuest) {
                activity!!.showLoginDialog(SocialLoginCallBack { onLoginFinished() })
            } else {
                setActions()
            }
            (v as FloatingActionButton).hide()
            v.show()
            v.invalidate()
            v.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    private fun setActions() {
        if (!placeDetailsModel.isLikedByUser) {
            likePlace(placeDetailsModel._id)
        } else {
            unLikePlace(placeDetailsModel._id)
        }
    }

    val detailsObservable: MutableLiveData<PlaceDetailsHeaderPayload>?
        get() {
            if (placeDetailsObservable == null) {
                placeDetailsObservable = MutableLiveData()
            }
            return placeDetailsObservable!!
        }

    fun getSimilarPlacesObservable(): MutableLiveData<List<PlaceModel>>? {
        if (similarPlacesObservable == null) {
            similarPlacesObservable = MutableLiveData()
        }
        return similarPlacesObservable
    }

    val deliveryAreaObservable: MutableLiveData<DeliveryValidationPayload>
        get() {
            if (deliveryAreaPayObservable == null) {
                deliveryAreaPayObservable = MutableLiveData()
            }
            return deliveryAreaPayObservable!!
        }

    fun likePlace(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            placeDetailsModel.isLikedByUser = true
            placeDetailsModel.likesCount = placeDetailsModel.likesCount + 1
            notifyDataChanged()
            val call = getInstance().likePlace(id)
            folloPlaceSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        Log.e("message", response.message())
                    } else {
                        placeDetailsModel.isLikedByUser = false
                        placeDetailsModel.likesCount = placeDetailsModel.likesCount - 1
                        notifyDataChanged()
                        Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(folloPlaceSubscriber)
        } else {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
        }
    }

    fun unLikePlace(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            placeDetailsModel.isLikedByUser = false
            placeDetailsModel.likesCount = placeDetailsModel.likesCount - 1
            notifyDataChanged()
            val call = getInstance().disLikePlace(id)
            unFolloPlaceSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        Log.e("message", response.message())
                    } else {
                        placeDetailsModel.isLikedByUser = true
                        placeDetailsModel.likesCount = placeDetailsModel.likesCount + 1
                        notifyDataChanged()
                        Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(unFolloPlaceSubscriber)
        } else {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
        }
    }

    fun commentClick(): View.OnClickListener {
        return View.OnClickListener { v: View? -> openCommentsOverlay(false, String()) }
    }

    fun openCommentsOverlay(isReply : Boolean, vararg commentsId: String?) {
        val commentsPopupFragment = OverlayFragment()
        val bundle = Bundle()
        bundle.putString("place_id", placeDetailsModel._id)
        bundle.putString("place_name", placeDetailsModel.name.en)
        bundle.putInt("like_count", placeDetailsModel.likesCount)
        bundle.putInt("share_count", placeDetailsModel.numberOfShares)
        bundle.putString(Constants.COMMENT_ID, if (commentsId.isNotEmpty()) commentsId[0] else "")
        bundle.putBoolean(Constants.IS_REPLY, isReply)
        bundle.putString("includeDelimiter", if (commentsId.size > 1) commentsId[1] else "")
        commentsPopupFragment.setAddCommentCallBack(this)
        commentsPopupFragment.arguments = bundle
        commentsPopupFragment.show(activity!!.supportFragmentManager, "CommentsPopupFragment")
    }

    fun shareProduct(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            sharePlace(placeDetailsModel._id)
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    placeDetailsModel.deepLink)
            sendIntent.type = "text/plain"
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity!!.startActivity(sendIntent)
        }
    }

    fun sharePlace(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = getInstance().sharePlace(id!!)
            placeDetailsModel.numberOfShares = placeDetailsModel.numberOfShares + 1
            notifyDataChanged()
            val sharePlaceSubscriber: Subscriber<Response<StringOnlyResponse>> = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    placeDetailsModel.numberOfShares = placeDetailsModel.numberOfShares - 1
                    notifyDataChanged()
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        //notifyItemChanged(position);
                    } else {
                        //Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        placeDetailsModel.numberOfShares = placeDetailsModel.numberOfShares - 1
                        notifyDataChanged()
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(sharePlaceSubscriber)
        } else {
            Toast.makeText(QurbaApplication.getContext(), activity!!.getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
        }
    }

    fun showDistance(): View.OnClickListener {
        return View.OnClickListener { v: View? -> LocationUtils.showOpenLocationDialog(activity) }
    }

    override fun onLoginFinished() {
        setActions()
    }

    override fun onCommentAdded(commentModel: CommentModel, isReply: Boolean) {
        if (!isReply) {
            placeDetailsModel.recentComment = commentModel
            placeDetailsModel.numberOfComments = placeDetailsModel.numberOfComments + 1
        } else if (placeDetailsModel.recentComment != null
                && commentModel._id.equals(placeDetailsModel.recentComment._id, ignoreCase = true)) placeDetailsModel.recentComment.recentReply = commentModel

//        commentsCount.set(offersModel.getCommentsCount() + "");
        notifyDataChanged()
        //  commentsAdapter.addLastComment(commentModel);
    }

    override fun onCommentUpdated(commentModel: CommentModel, isReply: Boolean) {
        //in case of updating other comment rather than recent comment
        if (placeDetailsModel.recentComment == null || !commentModel._id.equals(placeDetailsModel
                        .recentComment._id, ignoreCase = true)) return
        if (isReply) {
            placeDetailsModel.recentComment.recentReply.comment = commentModel.comment
        } else {
            placeDetailsModel.recentComment.comment = commentModel.comment
        }
        notifyDataChanged()
        // commentsAdapter.updateLastComment(commentModel);
    }

    override fun onCommentDeleted(isReply: Boolean, deletedCommentsCount: Int) {
        if (isReply) placeDetailsModel.recentComment.recentReply = null else {
            placeDetailsModel.recentComment = null
            placeDetailsModel.numberOfComments = placeDetailsModel.numberOfComments - 1
        }
        //        commentsCount.set(offersModel.getCommentsCount() + "");
        notifyDataChanged()
        //commentsAdapter.deleteComment(null);
    }

    fun logViewContentEvent(placeDetailsHeaderPayload: PlaceDetailsHeaderPayload) {
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, Constants.PLACE)
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, placeDetailsHeaderPayload._id)
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "EGP")
        standardFacebookEventsLogging(activity!!, AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, 0.0, params)
    }
}