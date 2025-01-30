package com.qurba.android.ui.comments.view_models

import android.os.Bundle
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableField
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.models.CommentModel
import com.qurba.android.network.models.response_models.StringOnlyResponse
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment.CommentActionEvents
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.showToastMsg
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class CommentItemViewModel(private val activity: BaseActivity, private val commentModel: CommentModel) : ViewModel(), Observable {
    private val callbacks = PropertyChangeRegistry()
    private var likeSubscriber: Subscriber<Response<StringOnlyResponse>>? = null
    private var unlikeSubscriber: Subscriber<Response<StringOnlyResponse>>? = null
    private var isEditable = false
    var isLiked: ObservableField<Boolean> = ObservableField(false)
    var likeCount: ObservableField<String> = ObservableField("")
    var isHaveLikesCount: ObservableField<Boolean> = ObservableField(false)
    var replyCount: ObservableField<Int> = ObservableField(0)
    var isHaveReplies: ObservableField<Boolean> = ObservableField(false)
    var isHaveMoreReplies: ObservableField<Boolean> = ObservableField(false)
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var addCommentCallBack: CommentsCallBack? = null

    private var commentActionEvents: CommentActionEvents? = null

    override fun addOnPropertyChangedCallback(onPropertyChangedCallback: OnPropertyChangedCallback) {
        callbacks.add(onPropertyChangedCallback)
    }

    fun setAddCommentCallBack(addCommentCallBack: CommentsCallBack) {
        this.addCommentCallBack = addCommentCallBack
    }

    fun setCommentActionEvents(commentActionEvents: CommentActionEvents) {
        this.commentActionEvents = commentActionEvents
    }

    @get:Bindable
    val commentText: String
        get() = commentModel.comment

    @get:Bindable
    val otherReplied: String
        get() {
            var string = ""
            if (commentModel.repliesCount > 1) {
                string += activity.getString(R.string.and_hint) + activity.getString(R.string.others_hint) + " "
            }
            string += activity.getString(R.string.replied_on_this)
            return string
        }

    @get:Bindable
    val whoReplied: String
        get() {
            if (commentModel.recentReply != null) {
                return commentModel.recentReply.user.firstName + " " + commentModel.recentReply.user.lastName
            }
            return ""
        }

    @get:Bindable
    val date: String
        get() = DateUtils.getTimeAgoFromTimeStamp(activity, commentModel.createdAt)

    @get:Bindable
    val userName: String
        get() {
            val stringBuilder = StringBuilder()
            stringBuilder.append(commentModel?.user?.firstName)
            stringBuilder.append(" ")
            stringBuilder.append(commentModel?.user?.lastName)
            return stringBuilder.toString()
        }

    override fun removeOnPropertyChangedCallback(onPropertyChangedCallback: OnPropertyChangedCallback) {
        callbacks.remove(onPropertyChangedCallback)
    }

    fun setEditable(bl: Boolean) {
        isEditable = bl
    }

    fun likeClickListener(): View.OnClickListener {
        return View.OnClickListener {
            if (sharedPref.token == null || sharedPref.isGuest) {
                activity.showLoginDialog()
            } else
                when (commentModel.type) {
                    "offer" -> {
                        if (!commentModel.isLikedByUser) {
                            likeOfferComment(commentModel._id)
                        } else {
                            unlikeOfferComment(commentModel._id)
                        }
                    }
                    "product" -> {
                        if (!commentModel.isLikedByUser) {
                            likeProductComment(commentModel._id)
                        } else {
                            unlikeProductComment(commentModel._id)
                        }
                    }
                    else -> {
                        if (!commentModel.isLikedByUser) {
                            likePlaceComment(commentModel._id)
                        } else {
                            unLikePlaceComment(commentModel._id)
                        }
                    }
                }
        }
    }

    fun commentLongPress(isReply: Boolean, commentPosition: Int): OnLongClickListener {
        return OnLongClickListener { v: View ->
            if (sharedPref.user != null && commentModel.user._id.equals(sharedPref.user._id, ignoreCase = true)) {
                val overlayFragment = EditDeleteOverlayFragment()
                commentActionEvents?.let { overlayFragment.setCommentActionEvents(it) }
                val bundle = Bundle()
                bundle.putBoolean(Constants.IS_REPLY, isReply)
                bundle.putInt(Constants.COMMENT_POSITION, commentPosition)
                bundle.putSerializable(Constants.COMMENTS, commentModel)

                overlayFragment.arguments = bundle
                overlayFragment.show((v.context as BaseActivity).supportFragmentManager, "EditDeleteOverlayFragment")
            }
            false
        }
    }

    private fun likeOfferComment(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = APICalls.Companion.getInstance().likeOfferComment(commentModel.parentId, id)
            commentModel.isLikedByUser = true
            isLiked.set(true)
            commentModel.likesCount = commentModel.likesCount + 1
            likeCount.set(commentModel.localizedLikesCount)
            isHaveLikesCount.set(commentModel.likesCount > 0)
            likeSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    commentModel.isLikedByUser = false
                    isLiked.set(false)
                    commentModel.likesCount = commentModel.likesCount - 1
                    likeCount.set(commentModel.localizedLikesCount)
                    isHaveLikesCount.set(commentModel.likesCount > 0)
                    activity?.showToastMsg(activity.getString(R.string.something_wrong))
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        addCommentCallBack?.onCommentUpdated(commentModel, false)
                        //notifyItemChanged(position);
                    } else {
                        commentModel.isLikedByUser = false
                        isLiked.set(false)
                        commentModel.likesCount = commentModel.likesCount - 1
                        likeCount.set(commentModel.localizedLikesCount)
                        isHaveLikesCount.set(commentModel.likesCount > 0)
                        activity?.showToastMsg(activity.getString(R.string.something_wrong))
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(likeSubscriber)
        } else {
            activity?.showToastMsg(activity.getString(R.string.no_connection))
        }
    }

    private fun unlikeOfferComment(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = APICalls.Companion.getInstance().unLikeOfferComment(commentModel.parentId, id)
            commentModel.isLikedByUser = false
            isLiked.set(false)
            commentModel.likesCount = commentModel.likesCount - 1
            likeCount.set(commentModel.localizedLikesCount.toString() + "")
            isHaveLikesCount.set(commentModel.likesCount > 0)
            unlikeSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    commentModel.isLikedByUser = true
                    isLiked.set(true)
                    commentModel.likesCount = commentModel.likesCount + 1
                    likeCount.set(commentModel.localizedLikesCount)
                    isHaveLikesCount.set(commentModel.likesCount > 0)
                    activity?.showToastMsg(activity.getString(R.string.something_wrong))
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        addCommentCallBack?.onCommentUpdated(commentModel, false)
                        //notifyItemChanged(position);
                    } else {
                        commentModel.isLikedByUser = true
                        isLiked.set(true)
                        commentModel.likesCount = commentModel.likesCount + 1
                        likeCount.set(commentModel.localizedLikesCount)
                        isHaveLikesCount.set(commentModel.likesCount > 0)
                        activity?.showToastMsg(activity.getString(R.string.something_wrong))
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(unlikeSubscriber)
        } else {
            activity?.showToastMsg(activity.getString(R.string.no_connection))
        }
    }

    private fun likeProductComment(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = APICalls.Companion.getInstance().likeProductComment(commentModel.parentId, id)
            commentModel.isLikedByUser = true
            isLiked.set(true)
            commentModel.likesCount = commentModel.likesCount + 1
            likeCount.set(commentModel.localizedLikesCount)
            isHaveLikesCount.set(commentModel.likesCount > 0)
            likeSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    commentModel.isLikedByUser = false
                    isLiked.set(false)
                    commentModel.likesCount = commentModel.likesCount - 1
                    likeCount.set(commentModel.localizedLikesCount)
                    isHaveLikesCount.set(commentModel.likesCount > 0)
                    activity?.showToastMsg(activity.getString(R.string.something_wrong))
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        addCommentCallBack?.onCommentUpdated(commentModel, false)
                        //notifyItemChanged(position);
                    } else {
                        commentModel.isLikedByUser = false
                        isLiked.set(false)
                        commentModel.likesCount = commentModel.likesCount - 1
                        likeCount.set(commentModel.localizedLikesCount)
                        isHaveLikesCount.set(commentModel.likesCount > 0)
                        activity?.showToastMsg( activity.getString(R.string.something_wrong))
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(likeSubscriber)
        } else {
            activity?.showToastMsg(activity.getString(R.string.no_connection))
        }
    }

    private fun unLikePlaceComment(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = APICalls.Companion.getInstance().unLikePlaceComment(commentModel.parentId, id)
            commentModel.isLikedByUser = false
            isLiked.set(false)
            commentModel.likesCount = commentModel.likesCount - 1
            likeCount.set(commentModel.localizedLikesCount)
            isHaveLikesCount.set(commentModel.likesCount > 0)
            unlikeSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    commentModel.isLikedByUser = true
                    isLiked.set(true)
                    commentModel.likesCount = commentModel.likesCount + 1
                    likeCount.set(commentModel.localizedLikesCount)
                    isHaveLikesCount.set(commentModel.likesCount > 0)
                    activity?.showToastMsg(activity.getString(R.string.something_wrong))
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        //notifyItemChanged(position);
                    } else {
                        commentModel.isLikedByUser = true
                        isLiked.set(true)
                        commentModel.likesCount = commentModel.likesCount + 1
                        likeCount.set(commentModel.localizedLikesCount)
                        isHaveLikesCount.set(commentModel.likesCount > 0)
                        activity?.showToastMsg(activity.getString(R.string.something_wrong))
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(unlikeSubscriber)
        } else {
            activity?.showToastMsg(activity.getString(R.string.no_connection))
        }
    }

    private fun likePlaceComment(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = APICalls.Companion.getInstance().likePlaceComment(commentModel.parentId, id)
            commentModel.isLikedByUser = true
            isLiked.set(true)
            commentModel.likesCount = commentModel.likesCount + 1
            likeCount.set(commentModel.localizedLikesCount)
            isHaveLikesCount.set(commentModel.likesCount > 0)
            likeSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    commentModel.isLikedByUser = false
                    isLiked.set(false)
                    commentModel.likesCount = commentModel.likesCount - 1
                    likeCount.set(commentModel.localizedLikesCount)
                    isHaveLikesCount.set(commentModel.likesCount > 0)
                    activity?.showToastMsg(activity.getString(R.string.something_wrong))
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        //notifyItemChanged(position);
                    } else {
                        commentModel.isLikedByUser = false
                        isLiked.set(false)
                        commentModel.likesCount = commentModel.likesCount - 1
                        likeCount.set(commentModel.localizedLikesCount)
                        isHaveLikesCount.set(commentModel.likesCount > 0)
                        activity?.showToastMsg(activity.getString(R.string.something_wrong))
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(likeSubscriber)
        } else {
            activity?.showToastMsg( activity.getString(R.string.no_connection))
        }
    }

    private fun unlikeProductComment(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = APICalls.Companion.getInstance().unLikeProductComment(commentModel.parentId, id)
            commentModel.isLikedByUser = false
            isLiked.set(false)
            commentModel.likesCount = commentModel.likesCount - 1
            likeCount.set(commentModel.localizedLikesCount)
            isHaveLikesCount.set(commentModel.likesCount > 0)
            unlikeSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    commentModel.isLikedByUser = true
                    isLiked.set(true)
                    commentModel.likesCount = commentModel.likesCount + 1
                    likeCount.set(commentModel.localizedLikesCount)
                    isHaveLikesCount.set(commentModel.likesCount > 0)
                    activity?.showToastMsg(activity.getString(R.string.something_wrong))
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        addCommentCallBack?.onCommentUpdated(commentModel, false)
                        //notifyItemChanged(position);
                    } else {
                        commentModel.isLikedByUser = true
                        isLiked.set(true)
                        commentModel.likesCount = commentModel.likesCount + 1
                        likeCount.set(commentModel.localizedLikesCount)
                        isHaveLikesCount.set(commentModel.likesCount > 0)
                        activity?.showToastMsg(activity.getString(R.string.something_wrong))
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(unlikeSubscriber)
        } else {
            activity?.showToastMsg(activity.getString(R.string.no_connection))
        }
    }

    init {
        isLiked.set(commentModel.isLikedByUser)
        likeCount.set(commentModel.localizedLikesCount)
        isHaveLikesCount.set(commentModel.likesCount > 0)
        isHaveReplies.set(commentModel.repliesCount > 0)
        isHaveMoreReplies.set(commentModel.repliesCount > 2)
        replyCount.set(commentModel.repliesCount)
    }
}
