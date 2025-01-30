/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  android.app.Application
 *  android.content.Context
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.widget.Toast
 *  androidx.databinding.Bindable
 *  androidx.databinding.ObservableField
 *  androidx.lifecycle.MutableLiveData
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  org.json.JSONObject
 *  retrofit2.Response
 *  rx.Observable
 *  rx.Scheduler
 *  rx.Subscriber
 *  rx.Subscription
 *  rx.android.schedulers.AndroidSchedulers
 *  rx.schedulers.Schedulers
 */
package com.qurba.android.ui.comments.view_models;


import static com.qurba.android.utils.extenstions.ExtesionsKt.showErrorMsg;

import android.app.Application;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.network.APICalls;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.AddCommentPayload;
import com.qurba.android.network.models.CommentModel;
import com.qurba.android.network.models.RepliesPayload;
import com.qurba.android.network.models.UpdateCommentPayload;
import com.qurba.android.network.models.request_models.CommetnsPayload;
import com.qurba.android.network.models.request_models.CommetnsRequest;
import com.qurba.android.network.models.response_models.RepliesResponse;
import com.qurba.android.network.models.response_models.StringOnlyResponse;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.CommentsCallBack;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.DateUtils;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;
import com.qurba.android.utils.SystemUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepliesViewModel extends BaseViewModel {

    private CommentsCallBack addCommentCallBack;
    private MutableLiveData<RepliesResponse> commentsObservable;
    private MutableLiveData<List<CommentModel>> addCommentsObservable;
    private MutableLiveData<CommentModel> updateCommentsObservable;
    private MutableLiveData<CommentModel> deleteCommentsObservable;

    private boolean isDataFound = true;
    private boolean isLoading;

    private Subscriber<Response<StringOnlyResponse>> likeSubscriber;
    private Subscriber<Response<StringOnlyResponse>> unlikeSubscriber;

    private String _id;
    private String commentError;

    public ObservableField<String> replyText = new ObservableField("");
    private ObservableField<String> likeCount = new ObservableField("");
    private ObservableField<Boolean> isLiked = new ObservableField(false);

    private CommentModel parentCommentModel = new CommentModel();
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();
    private BaseActivity activity;

    private boolean isEdit;
    private CommentModel replyModel;
    private String commentId;
    private boolean isProgress;
    private boolean isHasPrevious;

    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> commentText = new ObservableField<>();
    public ObservableField<String> commentDate = new ObservableField<>();
    private String type;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setProgress(boolean progress) {
        isProgress = progress;
        notifyDataChanged();
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    @Bindable
    public boolean isProgress() {
        return isProgress;
    }

    @Bindable
    public boolean isHasPrevious() {
        return isHasPrevious;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public void setHasPrevious(boolean isHasPrevious) {
        this.isHasPrevious = isHasPrevious;
        notifyDataChanged();
    }

    public void setLikeCount(String likeCount) {
        this.likeCount.set(likeCount);
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked.set(isLiked);
    }

    public ObservableField<Boolean> getIsLiked() {
        return isLiked;
    }

    public ObservableField<String> getLikeCount() {
        return likeCount;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setReplyModel(CommentModel replyModel) {
        this.replyModel = replyModel;
    }

    public RepliesViewModel(Application application) {
        super(application);
    }

    private boolean checkInputsValidation() {
        if (replyText.get() == null || replyText.get().trim().isEmpty()) {
            Toast.makeText(getApplication().getApplicationContext(), getApplication().getString(R.string.add_reply_warning), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

//    @Bindable
//    public String getUserName() {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(this.parentCommentModel.getUser().getFirstName());
//        stringBuilder.append(" ");
//        stringBuilder.append(this.parentCommentModel.getUser().getLastName());
//        return stringBuilder.toString();
//    }
//
//    @Bindable
//    public String getCommentText() {
//        return this.parentCommentModel.getComment();
//    }

//    @Bindable
//    public String getDate() {
//        return DateUtils.getTimeAgoFromTimeStamp(activity, this.parentCommentModel.getCreatedAt());
//    }

    @Bindable
    public String getCommentError() {
        return commentError;
    }

    private void setCommentError(String commentError) {
        this.commentError = commentError;
        notifyDataChanged();
    }

    public void addOfferReply() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_ADD_OFFER_COMMENT_REPLY_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to add offer comment reply"
                    , "");

            this.setDataFound(true);
            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.replyText.get());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    replyOfferComment(_id, commentId, commetnsRequest).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<AddCommentPayload>>() {
                        public void onCompleted() {
                            setProgress(false);
                        }

                        public void onError(Throwable throwable) {
                            throwable.printStackTrace();
                            //Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_ADD_OFFER_COMMENT_REPLY_FAIL,
                                    Line.LEVEL_ERROR, "Failed to add offer comment reply"
                                    , throwable.getMessage());
                        }

                        public void onNext(Response<AddCommentPayload> response) {
                            if (response.code() == 200) {
                                //getOfferReplies(true);
                                assert response.body() != null;
                                CommentModel commentModel = response.body().getPayload().get(response.body().getPayload().size() - 1);
                                commentModel.setParentId(parentCommentModel.get_id());

                                List<CommentModel> replies = parentCommentModel.getReplies();
                                replies.add(0, commentModel);
                                parentCommentModel.setReplies(replies);

                                addCommentsObservable.postValue(response.body().getPayload());
                                replyText.set("");
                                addCommentCallBack
                                        .onCommentAdded(commentModel, true);
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_ADD_OFFER_COMMENT_REPLY_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully add offer comment reply"
                                        , "");
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_ADD_OFFER_COMMENT_REPLY_FAIL,
                                            "Failed to add offer comment reply ");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void updateOfferReply() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_UPDATE_OFFER_COMMENT_REPLY_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to update offer comment reply"
                    , "");

            this.setDataFound(true);
            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.replyText.get());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    updateOfferComment(_id, replyModel.get_id(), commetnsRequest).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<UpdateCommentPayload>>() {
                        public void onCompleted() {
                            setProgress(false);
                        }

                        public void onError(Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_UPDATE_OFFER_COMMENT_REPLY_FAIL,
                                    Line.LEVEL_ERROR, "Failed to update offer comment reply"
                                    , throwable.getMessage());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200) {
//                                getOfferReplies(true);
                                parentCommentModel.getReplies().get(parentCommentModel.getReplies().indexOf(replyModel)).setComment(replyText.get());
                                updateCommentsObservable.postValue(response.body().getPayload());
                                replyText.set("");
                                setEdit(false);
                                setReplyModel(null);
                                addCommentCallBack
                                        .onCommentUpdated(response.body().getPayload(), true);
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_OFFER_COMMENT_REPLY_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully update offer comment reply"
                                        , "");
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_UPDATE_OFFER_COMMENT_REPLY_FAIL,
                                            "Failed to update offer comment reply ");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void deleteOfferReply() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_DELETE_OFFER_COMMENT_REPLY_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to delete offer comment reply"
                    , "");

            APICalls.Companion.getInstance().
                    deleteOfferComment(_id, replyModel.get_id()).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<UpdateCommentPayload>>() {
                        public void onCompleted() {
                        }

                        public void onError(Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_DELETE_OFFER_COMMENT_REPLY_FAIL,
                                    Line.LEVEL_ERROR, "Failed to delete offer comment reply"
                                    , throwable.getMessage());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 204) {
//                                getOfferReplies(true);
                                deleteCommentsObservable.postValue(replyModel);
                                parentCommentModel.getReplies().remove(replyModel);
                                setReplyModel(null);
                                addCommentCallBack.onCommentDeleted(true, parentCommentModel.getRepliesCount());
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_OFFER_COMMENT_REPLY_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully delete offer comment reply"
                                        , "");
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_DELETE_OFFER_COMMENT_REPLY_FAIL,
                                            "Failed to delete offer comment reply ");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    private Map<String, Object> setQueryMap(String delimiter, String direction, boolean includeDelimiter) {
        Map<String, Object> map = new HashMap<>();
        map.put("reversePageOrder", true);
        map.put("includeDelimiter", includeDelimiter);

        if (delimiter != null && delimiter.length() > 0) {
            map.put("delimiter", delimiter);
        }

        if (direction != null && !includeDelimiter) {
            map.put("direction", direction);
        }

        return map;
    }

    public void getOfferReplies(int page, String delimiter, String direction, boolean includeDelimiter) {

        if (page == 1)
            setLoading(true);

        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            this.setDataFound(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_GET_OFFER_COMMENT_REPLIES_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to retrieve offer comment replies"
                    , "");


            APICalls.Companion.getInstance().getOfferCommentReplies(_id, commentId, setQueryMap(delimiter, direction, includeDelimiter)).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response<RepliesPayload>>() {

                public void onCompleted() {
                    setLoading(false);
                }

                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_GET_OFFER_COMMENT_REPLIES_FAIL,
                            Line.LEVEL_ERROR, "Failed to retrieve offer comment replies"
                            , throwable.getMessage());
                }

                public void onNext(Response<RepliesPayload> response) {
                    if (response.code() == 200) {
                        RepliesPayload productCommentPayload = response.body();
                        assert productCommentPayload != null;
                        setParentCommentModel(productCommentPayload.getPayload());
                        setHasPrevious(productCommentPayload.getPayload().getReplies().isHasPrevious() && page == 1);
                        getCommentsObservable().postValue(productCommentPayload.getPayload());
                        setDataFound(productCommentPayload.getPayload().getReplies().getTotal() > 0);
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_GET_OFFER_COMMENT_REPLIES_SUCCESS,
                                Line.LEVEL_INFO, "Successfully retrieve offer comment replies"
                                , "");
                    } else {
                        assert response.errorBody() != null;
                        try {
                            showErrorMsg(response.errorBody().string(),
                                    Constants.USER_GET_OFFER_COMMENT_REPLIES_FAIL,
                                    "Failed to retrieve offer comment replies ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void addProductReply() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_ADD_PRODUCT_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to add product comment"
                    , "");

            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.replyText.get());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    replyProductComment(_id, commentId, commetnsRequest).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<AddCommentPayload>>() {
                        public void onCompleted() {
                            setProgress(false);
                        }

                        public void onError(java.lang.Throwable throwable) {
                            throwable.printStackTrace();
                            //Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_ADD_PRODUCT_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to add product comment"
                                    , throwable.getMessage());
                        }

                        public void onNext(Response<AddCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 204) {
//                                getProductReplies(true);
                                assert response.body() != null;
                                CommentModel commentModel = response.body().getPayload().get(response.body().getPayload().size() - 1);
                                commentModel.setParentId(parentCommentModel.get_id());

                                addCommentsObservable.postValue(response.body().getPayload());
                                replyText.set("");

                                List<CommentModel> replies = parentCommentModel.getReplies();
                                replies.add(0, commentModel);
                                parentCommentModel.setReplies(replies);

                                addCommentCallBack
                                        .onCommentAdded(commentModel, true);

                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_PRODUCT_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully add product comment"
                                        , "");
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_ADD_PRODUCT_COMMENT_FAIL,
                                            "Failed to add product comment ");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void updateProductReply() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_UPDATE_PRODUCT_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to update product comment"
                    , "");

            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.replyText.get());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    updateProductComments(_id, replyModel.get_id(), commetnsRequest).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<UpdateCommentPayload>>() {
                        public void onCompleted() {
                            setProgress(false);
                        }

                        public void onError(java.lang.Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_UPDATE_PRODUCT_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to update product comment"
                                    , throwable.getMessage());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 204) {
//                                getProductReplies(true);
                                parentCommentModel.getReplies().get(parentCommentModel.getReplies().indexOf(replyModel)).setComment(replyText.get());
                                assert response.body() != null;
                                updateCommentsObservable.postValue(response.body().getPayload());
                                replyText.set("");
                                setReplyModel(null);
                                setEdit(false);
                                addCommentCallBack
                                        .onCommentUpdated(response.body().getPayload(), true);

                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_PRODUCT_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully update product comment"
                                        , "");
                                return;
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_UPDATE_PRODUCT_COMMENT_FAIL,
                                            "Failed to update product comment ");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void deleteProductReply() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_DELETE_PRODUCT_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to delete product comment"
                    , "");

            APICalls.Companion.getInstance().
                    deleteProductComment(_id, replyModel.get_id()).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<UpdateCommentPayload>>() {
                        public void onCompleted() {
                        }

                        public void onError(java.lang.Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_DELETE_PRODUCT_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to delete product comment"
                                    , throwable.getMessage());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 204) {
//                                getProductReplies(true);
                                deleteCommentsObservable.postValue(replyModel);
                                parentCommentModel.getReplies().remove(replyModel);
                                addCommentCallBack
                                        .onCommentDeleted(true, parentCommentModel.getRepliesCount());
                                setReplyModel(null);
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_PRODUCT_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully delete product comment"
                                        , "");
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_DELETE_PRODUCT_COMMENT_FAIL,
                                            "Failed to delete product comment ");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void getProductReplies(int page, String delimiter, String direction, boolean includeDelimiter) {
        if (page == 1)
            setLoading(true);

        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            this.setDataFound(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_GET_PRODUCT_COMMENT_REPLIES_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to retrieve product comment replies"
                    , "");

            APICalls.Companion.getInstance().getProductCommentReplies(_id, commentId, setQueryMap(delimiter, direction, includeDelimiter)).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response<RepliesPayload>>() {

                public void onCompleted() {
                    setLoading(false);
                }

                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_GET_PRODUCT_COMMENT_REPLIES_FAIL,
                            Line.LEVEL_ERROR, "Failed to retrieve product comment replies"
                            , throwable.getMessage());
                }

                public void onNext(Response<RepliesPayload> response) {
                    RepliesPayload productCommentPayload = response.body();
                    if (response.code() == 200) {
                        assert productCommentPayload != null;
                        setParentCommentModel(productCommentPayload.getPayload());
                        setHasPrevious(productCommentPayload.getPayload().getReplies().isHasPrevious() && page == 1);
                        getCommentsObservable().postValue(productCommentPayload.getPayload());
                        setDataFound(productCommentPayload.getPayload().getReplies().getTotal() > 0);
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_GET_PRODUCT_COMMENT_REPLIES_SUCCESS,
                                Line.LEVEL_INFO, "Successfully retrieve product comment replies"
                                , "");
                    } else {
                        assert response.errorBody() != null;
                        try {
                            showErrorMsg(response.errorBody().string(),
                                    Constants.USER_GET_PRODUCT_COMMENT_REPLIES_ATTEMPT,
                                    "Failed to retrieve product comment replies ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void addPlaceReply() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_ADD_PLACE_COMMENT_REPLY_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to add place comment reply"
                    , "");

            this.setDataFound(true);
            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.replyText.get());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    replyPlaceComment(_id, commentId, commetnsRequest).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<AddCommentPayload>>() {
                        public void onCompleted() {
                            setProgress(false);
                        }

                        public void onError(Throwable throwable) {
                            throwable.printStackTrace();
                            //Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_ADD_PLACE_COMMENT_REPLY_FAIL,
                                    Line.LEVEL_ERROR, "Failed to add place comment reply"
                                    , throwable.getMessage());
                        }

                        public void onNext(Response<AddCommentPayload> response) {
                            if (response.code() == 200) {
                                //getOfferReplies(true);
                                assert response.body() != null;
                                CommentModel commentModel = response.body().getPayload().get(response.body().getPayload().size() - 1);
                                commentModel.setParentId(parentCommentModel.get_id());

                                addCommentsObservable.postValue(response.body().getPayload());
                                replyText.set("");

                                List<CommentModel> replies = parentCommentModel.getReplies();
                                replies.add(0, commentModel);
                                parentCommentModel.setReplies(replies);

                                addCommentCallBack
                                        .onCommentAdded(commentModel, true);
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_ADD_PLACE_COMMENT_REPLY_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully add place comment reply"
                                        , "");
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_ADD_PLACE_COMMENT_REPLY_FAIL,
                                            "Failed to add place comment reply ");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void updatePlaceReply() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_UPDATE_PLACE_COMMENT_REPLY_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to update place comment reply"
                    , "");

            this.setDataFound(true);
            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.replyText.get());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    updatePlaceComments(_id, replyModel.get_id(), commetnsRequest).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<UpdateCommentPayload>>() {
                        public void onCompleted() {
                            setProgress(false);
                        }

                        public void onError(Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_UPDATE_PLACE_COMMENT_REPLY_FAIL,
                                    Line.LEVEL_ERROR, "Failed to update place comment reply"
                                    , throwable.getMessage());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200) {
//                                getOfferReplies(true);

                                parentCommentModel.getReplies().get(parentCommentModel.getReplies().indexOf(replyModel)).setComment(replyText.get());
                                assert response.body() != null;
                                updateCommentsObservable.postValue(response.body().getPayload());
                                replyText.set("");
                                setEdit(false);
                                setReplyModel(null);
                                addCommentCallBack
                                        .onCommentUpdated(response.body().getPayload(), true);
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_PLACE_COMMENT_REPLY_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully update place comment reply"
                                        , "");
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_UPDATE_PLACE_COMMENT_REPLY_FAIL,
                                            "Failed to update place comment reply ");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void deletePlaceReply() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_DELETE_PLACE_COMMENT_REPLY_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to delete place comment reply"
                    , "");

            APICalls.Companion.getInstance().
                    deletePlaceComment(_id, replyModel.get_id()).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<UpdateCommentPayload>>() {
                        public void onCompleted() {
                        }

                        public void onError(Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_DELETE_PLACE_COMMENT_REPLY_FAIL,
                                    Line.LEVEL_ERROR, "Failed to delete place comment reply"
                                    , throwable.getMessage());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 204) {
//                                getOfferReplies(true);
                                deleteCommentsObservable.postValue(replyModel);
                                parentCommentModel.getReplies().remove(replyModel);
                                addCommentCallBack.onCommentDeleted(true, parentCommentModel.getRepliesCount());
                                setReplyModel(null);
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_PLACE_COMMENT_REPLY_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully delete place comment reply"
                                        , "");
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_DELETE_PLACE_COMMENT_REPLY_FAIL,
                                            "Failed to delete place comment reply ");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void getPlaceReplies(int page, String delimiter, String direction, boolean includeDelimiter) {
        if (page == 1)
            setLoading(true);

        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            this.setDataFound(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_GET_PLACE_COMMENT_REPLIES_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to retrieve place comment replies"
                    , "");

            APICalls.Companion.getInstance().getPlaceCommentReplies(_id, commentId, setQueryMap(delimiter, direction, includeDelimiter)).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response<RepliesPayload>>() {

                public void onCompleted() {
                    setLoading(false);
                }

                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_GET_PLACE_COMMENT_REPLIES_FAIL,
                            Line.LEVEL_ERROR, "Failed to retrieve place comment replies"
                            , throwable.getMessage());
                }

                public void onNext(Response<RepliesPayload> response) {
                    RepliesPayload productCommentPayload = response.body();
                    if (response.code() == 200) {
                        assert productCommentPayload != null;
                        setParentCommentModel(productCommentPayload.getPayload());
                        setHasPrevious(productCommentPayload.getPayload().getReplies().isHasPrevious() && page == 1);
                        getCommentsObservable().postValue(productCommentPayload.getPayload());
                        setDataFound(productCommentPayload.getPayload().getReplies().getTotal() > 0);
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_GET_PLACE_COMMENT_REPLIES_SUCCESS,
                                Line.LEVEL_INFO, "Successfully retrieve place comment replies"
                                , "");
                    } else {
                        assert response.errorBody() != null;
                        try {
                            showErrorMsg(response.errorBody().string(),
                                    Constants.USER_GET_PLACE_COMMENT_REPLIES_FAIL,
                                    "Failed to retrieve place comment replies ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }


    public MutableLiveData<RepliesResponse> getCommentsObservable() {
        if (this.commentsObservable == null) {
            this.commentsObservable = new MutableLiveData();
        }
        return this.commentsObservable;
    }

    public MutableLiveData<List<CommentModel>> addCommentsObservable() {
        if (this.addCommentsObservable == null) {
            this.addCommentsObservable = new MutableLiveData();
        }
        return this.addCommentsObservable;
    }

    public MutableLiveData<CommentModel> updateCommentsObservable() {
        if (this.updateCommentsObservable == null) {
            this.updateCommentsObservable = new MutableLiveData();
        }
        return this.updateCommentsObservable;
    }

    public MutableLiveData<CommentModel> deleteCommentsObservable() {
        if (this.deleteCommentsObservable == null) {
            this.deleteCommentsObservable = new MutableLiveData();
        }
        return this.deleteCommentsObservable;
    }

    @Bindable
    public boolean isDataFound() {
        return this.isDataFound;
    }


    public void setAddCommentCallBack(CommentsCallBack addCommentCallBack) {
        this.addCommentCallBack = addCommentCallBack;
    }

    public void setDataFound(boolean bl) {
        this.isDataFound = bl;
        this.notifyDataChanged();
    }

    public View.OnClickListener likeClickListener() {
        return v -> {
            if (sharedPref.getToken() == null || sharedPref.isGuest()) {
                activity.showLoginDialog();
            } else {
                switch (parentCommentModel.getType()) {
                    case "offer":
                        if (!parentCommentModel.isLikedByUser()) {
                            likeOfferComment(parentCommentModel.get_id());
                        } else {
                            unlikeOfferComment(parentCommentModel.get_id());
                        }
                        break;
                    case "product":
                        if (!parentCommentModel.isLikedByUser()) {
                            likeProductComment(parentCommentModel.get_id());
                        } else {
                            unlikeProductComment(parentCommentModel.get_id());
                        }
                        break;
                    default:
                        if (!parentCommentModel.isLikedByUser()) {
                            likePlaceComment(parentCommentModel.get_id());
                        } else {
                            unlikePlaceComment(parentCommentModel.get_id());
                        }
                }
            }
        };
    }

    public void likeOfferComment(String id) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            rx.Observable<Response<StringOnlyResponse>> call = APICalls.Companion.getInstance().likeOfferComment(_id, id);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_LIKE_OFFER_COMMENT_ATTEMPT,
                    Line.LEVEL_ERROR, "User attempt to like offer comment"
                    , "");

            parentCommentModel.setLikedByUser(true);
            parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() + 1);

            setLikeCount(parentCommentModel.getLocalizedLikesCount());
            setIsLiked(true);

            notifyDataChanged();

            likeSubscriber = new Subscriber<Response<StringOnlyResponse>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    parentCommentModel.setLikedByUser(false);
                    parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() - 1);
                    setLikeCount(parentCommentModel.getLocalizedLikesCount());
                    setIsLiked(true);
                    notifyDataChanged();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_LIKE_OFFER_COMMENT_FAIL,
                            Line.LEVEL_ERROR, "Failed to like offer comment"
                            , e.getStackTrace().toString());

                    Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Response<StringOnlyResponse> response) {
                    if (response.code() == 200) {
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_LIKE_OFFER_COMMENT_SUCCESS,
                                Line.LEVEL_INFO, "User successfully like offer comment"
                                , "");

                    } else {
                        parentCommentModel.setLikedByUser(false);
                        parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() - 1);
                        setLikeCount(parentCommentModel.getLocalizedLikesCount());
                        setIsLiked(false);
                        notifyDataChanged();

                        String errorMsg = null;
                        try {
                            String error = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(error);
                            errorMsg = jObjError.getJSONObject("error").get("en").toString();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_LIKE_OFFER_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to like offer comment"
                                    , errorMsg);
                        } catch (Exception e) {
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_LIKE_OFFER_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to like offer comment"
                                    , e.getStackTrace().toString());
                        } finally {
                            Toast.makeText(QurbaApplication.getContext(),
                                    errorMsg == null ?
                                            QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(likeSubscriber);
        } else {
            Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void unlikeOfferComment(String id) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            rx.Observable<Response<StringOnlyResponse>> call = APICalls.Companion.getInstance().unLikeOfferComment(_id, id);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_UNLIKE_OFFER_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to unlike offer comment"
                    , "");

            parentCommentModel.setLikedByUser(false);
            parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() - 1);

            setLikeCount(parentCommentModel.getLocalizedLikesCount());
            setIsLiked(false);

            notifyDataChanged();

            unlikeSubscriber = new Subscriber<Response<StringOnlyResponse>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    parentCommentModel.setLikedByUser(true);
                    parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() + 1);
                    setLikeCount(parentCommentModel.getLocalizedLikesCount());
                    setIsLiked(true);
                    notifyDataChanged();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_UNLIKE_OFFER_COMMENT_FAIL,
                            Line.LEVEL_ERROR, "Failed to unlike offer comment"
                            , e.getMessage());
                    Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Response<StringOnlyResponse> response) {
                    if (response.code() == 200) {
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_UNLIKE_OFFER_COMMENT_FAIL,
                                Line.LEVEL_INFO, "User successfully unlike offer comment"
                                , "");
                    } else {
                        parentCommentModel.setLikedByUser(true);
                        parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() + 1);
                        setLikeCount(parentCommentModel.getLocalizedLikesCount());
                        setIsLiked(true);
                        notifyDataChanged();
                        assert response.errorBody() != null;
                        try {
                            showErrorMsg(response.errorBody().string(),
                                    Constants.USER_UNLIKE_OFFER_COMMENT_FAIL,
                                    "Failed to unlike offer comment ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(unlikeSubscriber);
        } else {
            Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void likeProductComment(String id) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            rx.Observable<Response<StringOnlyResponse>> call = APICalls.Companion.getInstance().likeProductComment(_id, id);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_LIKE_PRODUCT_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to like product comment"
                    , "");

            parentCommentModel.setLikedByUser(true);
            parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() + 1);

            setLikeCount(parentCommentModel.getLocalizedLikesCount());
            setIsLiked(true);

            notifyDataChanged();

            likeSubscriber = new Subscriber<Response<StringOnlyResponse>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    parentCommentModel.setLikedByUser(false);
                    parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() - 1);
                    setLikeCount(parentCommentModel.getLocalizedLikesCount());
                    setIsLiked(true);
                    notifyDataChanged();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_LIKE_PRODUCT_COMMENT_FAIL,
                            Line.LEVEL_ERROR, "Failed to like product comment"
                            , e.getMessage());

                    Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Response<StringOnlyResponse> response) {
                    if (response.code() == 200) {
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_LIKE_PRODUCT_COMMENT_SUCCESS,
                                Line.LEVEL_INFO, "User successfully like product comment"
                                , "");

                    } else {
                        parentCommentModel.setLikedByUser(false);
                        parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() - 1);
                        setLikeCount(parentCommentModel.getLocalizedLikesCount());
                        setIsLiked(false);
                        notifyDataChanged();
                        assert response.errorBody() != null;
                        try {
                            showErrorMsg(response.errorBody().string(),
                                    Constants.USER_LIKE_PRODUCT_COMMENT_FAIL,
                                    "Failed to like product comment ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(likeSubscriber);
        } else {
            Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void unlikeProductComment(String id) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            rx.Observable<Response<StringOnlyResponse>> call = APICalls.Companion.getInstance().unLikeProductComment(_id, id);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_UNLIKE_PRODUCT_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to unlike product comment"
                    , "");

            parentCommentModel.setLikedByUser(false);
            parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() - 1);

            setLikeCount(parentCommentModel.getLocalizedLikesCount());
            setIsLiked(false);

            notifyDataChanged();

            unlikeSubscriber = new Subscriber<Response<StringOnlyResponse>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    parentCommentModel.setLikedByUser(true);
                    parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() + 1);
                    setLikeCount(parentCommentModel.getLocalizedLikesCount());
                    setIsLiked(true);
                    notifyDataChanged();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_UNLIKE_PRODUCT_COMMENT_FAIL,
                            Line.LEVEL_ERROR, "Failed to unlike product comment"
                            , e.getMessage());
                    Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Response<StringOnlyResponse> response) {
                    if (response.code() == 200) {
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_UNLIKE_PRODUCT_COMMENT_SUCCESS,
                                Line.LEVEL_INFO, "User successfully unlike product comment"
                                , "");
                    } else {
                        parentCommentModel.setLikedByUser(true);
                        parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() + 1);
                        setLikeCount(parentCommentModel.getLocalizedLikesCount());
                        setIsLiked(true);
                        notifyDataChanged();
                        assert response.errorBody() != null;
                        try {
                            showErrorMsg(response.errorBody().string(),
                                    Constants.USER_UNLIKE_PRODUCT_COMMENT_FAIL,
                                    "Failed to unlike product comment ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(unlikeSubscriber);
        } else {
            Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void likePlaceComment(String id) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            rx.Observable<Response<StringOnlyResponse>> call = APICalls.Companion.getInstance().likePlaceComment(_id, id);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_LIKE_PLACE_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to like place comment"
                    , "");

            parentCommentModel.setLikedByUser(true);
            parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() + 1);

            setLikeCount(parentCommentModel.getLocalizedLikesCount());
            setIsLiked(true);

            notifyDataChanged();

            likeSubscriber = new Subscriber<Response<StringOnlyResponse>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    parentCommentModel.setLikedByUser(false);
                    parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() - 1);
                    setLikeCount(parentCommentModel.getLocalizedLikesCount());
                    setIsLiked(true);
                    notifyDataChanged();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_LIKE_PLACE_COMMENT_FAIL,
                            Line.LEVEL_ERROR, "Failed to like place comment"
                            , e.getMessage());

                    Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Response<StringOnlyResponse> response) {
                    if (response.code() == 200) {
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_LIKE_PLACE_COMMENT_SUCCESS,
                                Line.LEVEL_INFO, "User successfully like place comment"
                                , "");

                    } else {
                        parentCommentModel.setLikedByUser(false);
                        parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() - 1);
                        setLikeCount(parentCommentModel.getLocalizedLikesCount());
                        setIsLiked(false);
                        notifyDataChanged();
                        assert response.errorBody() != null;
                        try {
                            showErrorMsg(response.errorBody().string(),
                                    Constants.USER_LIKE_PLACE_COMMENT_FAIL,
                                    "Failed to like place comment ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(likeSubscriber);
        } else {
            Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void unlikePlaceComment(String id) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            rx.Observable<Response<StringOnlyResponse>> call = APICalls.Companion.getInstance().unLikePlaceComment(_id, id);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_UNLIKE_PLACE_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to unlike place comment"
                    , "");

            parentCommentModel.setLikedByUser(false);
            parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() - 1);

            setLikeCount(parentCommentModel.getLocalizedLikesCount());
            setIsLiked(false);

            notifyDataChanged();

            unlikeSubscriber = new Subscriber<Response<StringOnlyResponse>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    parentCommentModel.setLikedByUser(true);
                    parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() + 1);
                    setLikeCount(parentCommentModel.getLocalizedLikesCount());
                    setIsLiked(true);
                    notifyDataChanged();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_UNLIKE_PLACE_COMMENT_FAIL,
                            Line.LEVEL_ERROR, "Failed to unlike place comment"
                            , e.getMessage());
                    Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Response<StringOnlyResponse> response) {
                    if (response.code() == 200) {
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_UNLIKE_PLACE_COMMENT_SUCCESS,
                                Line.LEVEL_ERROR, "User successfully unlike place comment"
                                , "");
                    } else {
                        parentCommentModel.setLikedByUser(true);
                        parentCommentModel.setLikesCount(parentCommentModel.getLikesCount() + 1);
                        setLikeCount(parentCommentModel.getLocalizedLikesCount());
                        setIsLiked(true);
                        notifyDataChanged();
                        assert response.errorBody() != null;
                        try {
                            showErrorMsg(response.errorBody().string(),
                                    Constants.USER_UNLIKE_PLACE_COMMENT_FAIL,
                                    "Failed to unlike place comment ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(unlikeSubscriber);
        } else {
            Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }


    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public void setParentCommentModel(RepliesResponse repliesResponse) {
        if (parentCommentModel.get_id() != null) return;
        parentCommentModel = repliesResponse.getParentComment();
        parentCommentModel.setType(type);
        parentCommentModel.setReplies(repliesResponse.getReplies().getDocs());
        userName.set(parentCommentModel.getUser().getFirstName() + " " + parentCommentModel.getUser().getLastName());
        commentText.set(parentCommentModel.getComment());
        commentDate.set(DateUtils.getTimeAgoFromTimeStamp(activity, this.parentCommentModel.getCreatedAt()));
        setIsLiked(parentCommentModel.isLikedByUser());
        setLikeCount(parentCommentModel.getLocalizedLikesCount() + "");
        parentCommentModel.setRepliesCount(repliesResponse.getReplies().getTotal());
        notifyDataChanged();
    }

    public CommentModel getParentCommentModel() {
        return parentCommentModel;
    }
}

