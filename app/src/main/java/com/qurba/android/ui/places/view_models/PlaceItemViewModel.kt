package com.qurba.android.ui.places.view_models

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableField
import androidx.databinding.PropertyChangeRegistry
import com.google.gson.Gson
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.APICalls.Companion.getInstance
import com.qurba.android.network.QurbaLogger
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.*
import com.qurba.android.network.models.request_models.CommetnsPayload
import com.qurba.android.network.models.request_models.CommetnsRequest
import com.qurba.android.network.models.response_models.StringOnlyResponse
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment.CommentActionEvents
import com.qurba.android.ui.comments.views.OverlayFragment
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity
import com.qurba.android.utils.*
import org.json.JSONObject
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.Exception

class PlaceItemViewModel(private val activity: BaseActivity, private val placeModel: PlaceModel) :
    BaseItemViewModel(activity) {

    private var addFavoriteSubscriber: Subscriber<Response<StringOnlyResponse>>? = null
    var isFavourite = ObservableField<Boolean>()
    var countsTxt = ObservableField<String>()
    var likeCount: ObservableField<String?> = ObservableField<String?>("")
    var commentsCount: ObservableField<String?> = ObservableField<String?>("")
    var sharesCount: ObservableField<String?> = ObservableField<String?>("")
    var isHaveLikesCount: ObservableField<Boolean?> = ObservableField<Boolean?>(false)
    var isHaveCommentsCount: ObservableField<Boolean?> = ObservableField<Boolean?>(false)
    var isHaveSharesCount: ObservableField<Boolean?> = ObservableField<Boolean?>(false)
    var commentText: ObservableField<String?> = ObservableField<String?>("")
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var commentActionEvents: CommentActionEvents? = null
    private var addCommentCallBack: CommentsCallBack? = null
    private var isEdit = false
    private var commentId: String? = null

    @get:Bindable
    var isProgress = false
        set(progress) {
            field = progress
            notifyDataChanged()
        }

    fun setCommentActionEvents(commentActionEvents: CommentActionEvents?) {
        this.commentActionEvents = commentActionEvents
    }

    fun setAddCommentCallBack(addCommentCallBack: CommentsCallBack?) {
        this.addCommentCallBack = addCommentCallBack
    }

    @get:Bindable
    val isDeliveringToArea: Boolean
        get() = placeModel.isDeliveringToArea

    @get:Bindable
    val isPlaceOpen: Boolean
        get() = placeModel.isPlaceOpen

    @get:Bindable
    val isHaveComments: Boolean
        get() = placeModel.recentComment == null || placeModel.recentComment._id == null

    @get:Bindable
    val availability: String
        get() = if (placeModel.availability != null) placeModel.availability.en else ""

    @get:Bindable
    val placeCategory: String
        get() {
            var categoriesTxt = ""
            if (placeModel.categories != null)
                for (category in placeModel.categories)
                    categoriesTxt += "${category.name.en}, "

            return categoriesTxt.trim().dropLast(1)
        }

    @get:Bindable
    val placeAndBranchName: String
        get() = placeModel.name.en + " - " + placeModel.branchName.en

    @get:Bindable
    val placeName: String
        get() = placeModel.name.en

    private fun setCountsTxt() {
        var count = ""
        if (placeModel.likesCount > 0) count += placeModel.likesCount.toString() + " " + activity.getString(
            R.string.likes
        )
        if (placeModel.commentsCount > 0) count += (if (!count.isEmpty()) " • " else "") + placeModel.commentsCount + " " + activity.getString(
            R.string.comments
        )
        if (placeModel.sharesCount > 0) count += (if (!count.isEmpty()) " • " else "") + placeModel.sharesCount + " " + activity.getString(
            R.string.shares
        )
        countsTxt.set(count)
    }

    fun likePlaceClick(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            v.requestFocusFromTouch()
            v.isEnabled = false
            if (sharedPref.token == null || sharedPref.isGuest) {
                activity.showLoginDialog()
            } else {
                if (!placeModel.isLikedByUser) {
                    likePlace(placeModel._id, v)
                } else {
                    unLikePlace(placeModel._id, v)
                }
            }
        }
    }

    fun commentClick(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            val commentsPopupFragment = OverlayFragment()
            val bundle = Bundle()
            bundle.putString("place_id", placeModel._id)
            bundle.putString("place_name", placeModel.name.en)
            bundle.putInt("like_count", placeModel.likesCount)
            bundle.putInt("share_count", placeModel.sharesCount)
            commentsPopupFragment.setAddCommentCallBack(addCommentCallBack)
            commentsPopupFragment.arguments = bundle
            commentsPopupFragment.show(
                (v.context as BaseActivity).supportFragmentManager,
                "CommentsPopupFragment"
            )
        }
    }

    fun commentLongPress(): OnLongClickListener {
        return OnLongClickListener { v: View ->
            if (sharedPref.user != null && placeModel.recentComment.user._id
                    .equals(sharedPref.user._id, ignoreCase = true)
            ) {
                val overlayFragment = EditDeleteOverlayFragment()
                overlayFragment.setCommentActionEvents(commentActionEvents!!)
                val bundle = Bundle()
                bundle.putSerializable(
                    Constants.COMMENTS,
                    placeModel.recentComment
                )
                overlayFragment.arguments = bundle
                overlayFragment.show(
                    (v.context as BaseActivity).supportFragmentManager,
                    "EditDeleteOverlayFragment"
                )
            }
            false
        }
    }

    fun replyClickListener(): OnTouchListener {
        return label@ OnTouchListener { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                replyClick(placeModel.recentComment)
                true
            }
            false
        }
    }

    fun replyClick(commentModel: CommentModel) {
        val commentsPopupFragment = OverlayFragment()
        val bundle = Bundle()
        bundle.putString("place_id", placeModel._id)
        bundle.putString("place_name", placeModel.name.en)
        bundle.putInt("like_count", placeModel.likesCount)
        bundle.putInt("share_count", placeModel.sharesCount)
        bundle.putSerializable(Constants.COMMENTS, commentModel)
        bundle.putString(Constants.COMMENT_ID, commentModel._id)
        bundle.putBoolean(Constants.IS_REPLY, true)
        commentsPopupFragment.setAddCommentCallBack(addCommentCallBack)
        commentsPopupFragment.arguments = bundle
        commentsPopupFragment.show(activity.supportFragmentManager, "CommentsPopupFragment")
    }

    fun sharePlace(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            sharePlace(placeModel._id)
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, placeModel.name.en)
            sendIntent.putExtra(Intent.EXTRA_TEXT, placeModel.deepLink)
            activity.startActivity(sendIntent)
        }
    }

    fun likePlace(id: String?, v: View) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            placeModel.isLikedByUser = true
            placeModel.likesCount = placeModel.likesCount + 1
            likeCount.set(placeModel.getLocalizedLikesCount(activity))
            isHaveLikesCount.set(placeModel.likesCount > 0)
            isFavourite.set(true)
            val call = getInstance().likePlace(id)
            val folloPlaceSubscriber: Subscriber<Response<StringOnlyResponse>> =
                object : Subscriber<Response<StringOnlyResponse>>() {
                    override fun onCompleted() {
                        v.isEnabled = true
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onNext(response: Response<StringOnlyResponse>) {
                        if (response.code() == 200) {
                            Log.e("message", response.message())
                        } else {
                            placeModel.isLikedByUser = false
                            placeModel.likesCount = placeModel.likesCount - 1
                            likeCount.set(placeModel.getLocalizedLikesCount(activity))
                            isHaveLikesCount.set(placeModel.likesCount > 0)
                            isFavourite.set(false)
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
                .subscribe(folloPlaceSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                QurbaApplication.getContext().getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun unLikePlace(id: String?, v: View) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            placeModel.isLikedByUser = false
            placeModel.likesCount = placeModel.likesCount - 1
            likeCount.set(placeModel.getLocalizedLikesCount(activity))
            isHaveLikesCount.set(placeModel.likesCount > 0)
            isFavourite.set(false)
            val call = getInstance().disLikePlace(id)
            val unFolloPlaceSubscriber: Subscriber<Response<StringOnlyResponse>> =
                object : Subscriber<Response<StringOnlyResponse>>() {
                    override fun onCompleted() {
                        v.isEnabled = true
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onNext(response: Response<StringOnlyResponse>) {
                        if (response.code() == 200) {
                            Log.e("message", response.message())
                        } else {
                            placeModel.isLikedByUser = true
                            placeModel.likesCount = placeModel.likesCount + 1
                            likeCount.set(placeModel.getLocalizedLikesCount(activity))
                            isHaveLikesCount.set(placeModel.likesCount > 0)
                            isFavourite.set(true)
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
                .subscribe(unFolloPlaceSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                QurbaApplication.getContext().getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun sharePlace(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = getInstance().sharePlace(
                id!!
            )
            placeModel.sharesCount = placeModel.sharesCount + 1
            isHaveSharesCount.set(placeModel.sharesCount > 0)
            sharesCount.set(placeModel.localizedSharesCount + " " + activity.getString(R.string.share))
            addFavoriteSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    placeModel.sharesCount = placeModel.sharesCount - 1
                    isHaveSharesCount.set(placeModel.sharesCount > 0)
                    sharesCount.set(placeModel.localizedSharesCount + " " + activity.getString(R.string.share))
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        //notifyItemChanged(position);
                    } else {
                        //Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        placeModel.sharesCount = placeModel.sharesCount - 1
                        isHaveSharesCount.set(placeModel.sharesCount > 0)
                        sharesCount.set(placeModel.localizedSharesCount + " " + activity.getString(R.string.share))
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addFavoriteSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                activity.getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun openPlaceDetails(position: Int): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            val intent = Intent(activity, PlaceDetailsActivity::class.java)
            intent.putExtra("place_name", placeModel.name.en)
            intent.putExtra("unique_name", placeModel.uniqueName)
            intent.putExtra("place_id", placeModel._id)
            intent.putExtra(Constants.PLACE, placeModel)
            intent.putExtra(
                Constants.ORDER_TYPE,
                Constants.OFFERS
            )
            intent.putExtra(Constants.PLACE, Gson().toJson(placeModel))
            sharedPref.placePosition = position
            activity.startActivityForResult(intent, 1087)
        }
    }

    fun addComment(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            if (sharedPref.isGuest) activity.showLoginDialog() else {
                isProgress = true
                if (isEdit) {
                    updatePlaceComment()
                } else {
                    addPlaceComment()
                }
            }
        }
    }

    fun addPlaceComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            if (!checkInputsValidation()) return
            logging(
                QurbaApplication.getContext(),
                Constants.USER_ADD_OFFER_COMMENT_ATTEMPT,
                LEVEL_INFO, "User attempt to add offer comment", ""
            )
            val commetnsPayload = CommetnsPayload()
            commetnsPayload.comment = commentText.get()!!.trim { it <= ' ' }
            val commetnsRequest = CommetnsRequest(commetnsPayload)
            getInstance().addPlaceComment(placeModel._id, commetnsRequest)
                .subscribeOn(Schedulers.newThread()).observeOn(
                    AndroidSchedulers.mainThread()
                ).subscribe(object : Subscriber<Response<AddCommentPayload>>() {
                    override fun onCompleted() {
                        isProgress = false
                    }

                    override fun onError(throwable: Throwable) {
                        throwable.printStackTrace()
                        Toast.makeText(
                            activity,
                            QurbaApplication.getContext().getString(R.string.something_wrong),
                            Toast.LENGTH_LONG
                        ).show()
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_ADD_OFFER_COMMENT_FAIL,
                            LEVEL_ERROR,
                            "Failed to add offer comment",
                            throwable.stackTrace.toString()
                        )
                    }

                    override fun onNext(response: Response<AddCommentPayload>) {
                        if (response.code() == 200 || response.code() == 201) {
                            addCommentCallBack!!.onCommentAdded(
                                response.body()!!.payload[response.body()!!.payload.size - 1], false
                            )
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_UPDATE_OFFER_COMMENT_SUCCESS,
                                LEVEL_INFO, "Successfully add offer comment", ""
                            )
                            return
                        }
                        var errorMsg: String? = null
                        try {
                            val error = response.errorBody()!!.string()
                            val jObjError = JSONObject(error)
                            errorMsg = jObjError.getJSONObject("error")["en"].toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_ADD_OFFER_COMMENT_FAIL,
                                LEVEL_ERROR, "Failed to add offer comment", errorMsg
                            )
                        } catch (e: Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_ADD_OFFER_COMMENT_FAIL,
                                LEVEL_ERROR, "Failed to add offer comment", e.stackTrace.toString()
                            )
                        } finally {
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                errorMsg ?: QurbaApplication.getContext()
                                    .getString(R.string.something_wrong), Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            return
        }
        Toast.makeText(
            activity,
            QurbaApplication.getContext().getString(R.string.no_connection),
            Toast.LENGTH_LONG
        ).show()
    }

    fun updatePlaceComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            if (!checkInputsValidation()) return
            logging(
                QurbaApplication.getContext(),
                Constants.USER_UPDATE_PLACE_COMMENT_ATTEMPT,
                LEVEL_ERROR, "User attempt to update place comment", ""
            )
            val commetnsPayload = CommetnsPayload()
            commetnsPayload.comment = commentText.get()!!.trim { it <= ' ' }
            val commetnsRequest = CommetnsRequest(commetnsPayload)
            getInstance().updatePlaceComments(
                placeModel._id,
                commentId!!, commetnsRequest
            ).subscribeOn(Schedulers.newThread()).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribe(object : Subscriber<Response<UpdateCommentPayload>>() {
                override fun onCompleted() {
                    isProgress = false
                }

                override fun onError(throwable: Throwable) {
                    throwable.printStackTrace()
                    Toast.makeText(
                        activity,
                        QurbaApplication.getContext().getString(R.string.something_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                    logging(
                        QurbaApplication.getContext(),
                        Constants.USER_UPDATE_PLACE_COMMENT_FAIL,
                        LEVEL_ERROR,
                        "Failed to update place comment",
                        throwable.stackTrace.toString()
                    )
                }

                override fun onNext(response: Response<UpdateCommentPayload>) {
                    if (response.code() == 200 || response.code() == 201) {
                        addCommentCallBack!!.onCommentUpdated(null, false)
                        setCommentId("")
                        setEdit(false)
                        commentText.set("")
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_UPDATE_OFFER_COMMENT_SUCCESS,
                            LEVEL_INFO, "Successfully update offer comment", ""
                        )
                        return
                    }
                    var errorMsg: String? = null
                    try {
                        val error = response.errorBody()!!.string()
                        val jObjError = JSONObject(error)
                        errorMsg = jObjError.getJSONObject("error")["en"].toString()
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_UPDATE_PLACE_COMMENT_FAIL,
                            LEVEL_ERROR, "Failed to update place comment", errorMsg
                        )
                    } catch (e: Exception) {
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_UPDATE_PLACE_COMMENT_FAIL,
                            LEVEL_ERROR, "Failed to update place comment", e.stackTrace.toString()
                        )
                    } finally {
                        Toast.makeText(
                            QurbaApplication.getContext(),
                            errorMsg ?: QurbaApplication.getContext()
                                .getString(R.string.something_wrong), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
            return
        }
        Toast.makeText(
            activity,
            QurbaApplication.getContext().getString(R.string.no_connection),
            Toast.LENGTH_LONG
        ).show()
    }

    fun deletePLaceComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            logging(
                QurbaApplication.getContext(),
                Constants.USER_DELETE_OFFER_COMMENT_ATTEMPT,
                LEVEL_ERROR, "User attempt to delete offer comment", ""
            )
            getInstance().deletePlaceComment(placeModel._id, commentId!!)
                .subscribeOn(Schedulers.newThread()).observeOn(
                    AndroidSchedulers.mainThread()
                ).subscribe(object : Subscriber<Response<UpdateCommentPayload>>() {
                    override fun onCompleted() {}
                    override fun onError(throwable: Throwable) {
                        throwable.printStackTrace()
                        Toast.makeText(
                            activity,
                            QurbaApplication.getContext().getString(R.string.something_wrong),
                            Toast.LENGTH_LONG
                        ).show()
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_DELETE_PLACE_COMMENT_FAIL,
                            LEVEL_ERROR,
                            "Failed to delete place comment",
                            throwable.stackTrace.toString()
                        )
                    }

                    override fun onNext(response: Response<UpdateCommentPayload>) {
                        if (response.code() == 200 || response.code() == 204) {
                            addCommentCallBack!!.onCommentDeleted(false, 1)
                            setCommentId("")
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_DELETE_PLACE_COMMENT_SUCCESS,
                                LEVEL_INFO, "Successfully delete place comment", ""
                            )
                            return
                        }
                        var errorMsg: String? = null
                        try {
                            val error = response.errorBody()!!.string()
                            val jObjError = JSONObject(error)
                            errorMsg = jObjError.getJSONObject("error")["en"].toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_DELETE_PLACE_COMMENT_FAIL,
                                LEVEL_ERROR, "Failed to delete place comment", errorMsg
                            )
                        } catch (e: Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_DELETE_PLACE_COMMENT_FAIL,
                                LEVEL_ERROR,
                                "Failed to delete place comment",
                                e.stackTrace.toString()
                            )
                        } finally {
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                errorMsg ?: QurbaApplication.getContext()
                                    .getString(R.string.something_wrong), Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            return
        }
        Toast.makeText(
            activity,
            QurbaApplication.getContext().getString(R.string.no_connection),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun checkInputsValidation(): Boolean {
        if (commentText.get() == null || commentText.get()!!.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(
                activity,
                activity.getString(R.string.add_comment_warning),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    fun setEdit(edit: Boolean) {
        isEdit = edit
    }

    fun setCommentId(commentId: String?) {
        this.commentId = commentId
    }

    init {
        isFavourite.set(placeModel.isLikedByUser)
        likeCount.set(placeModel.getLocalizedLikesCount(activity))
        commentsCount.set(placeModel.localizedCommentsCount + " " + activity.getString(R.string.comments))
        sharesCount.set(placeModel.localizedSharesCount + " " + activity.getString(R.string.share))
        isHaveLikesCount.set(placeModel.likesCount > 0)
        isHaveCommentsCount.set(placeModel.commentsCount > 0)
        isHaveSharesCount.set(placeModel.sharesCount > 0)
        setCountsTxt()
    }
}