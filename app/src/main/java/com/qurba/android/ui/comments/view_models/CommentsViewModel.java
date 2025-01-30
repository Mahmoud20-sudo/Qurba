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

import android.app.Application;
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
import com.qurba.android.network.models.CommentsPayload;
import com.qurba.android.network.models.UpdateCommentPayload;
import com.qurba.android.network.models.request_models.CommetnsPayload;
import com.qurba.android.network.models.request_models.CommetnsRequest;
import com.qurba.android.network.models.response_models.CommentsListResponse;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.CommentsCallBack;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SystemUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentsViewModel extends BaseViewModel {

    private CommentsCallBack addCommentCallBack;
    public ObservableField<String> commentText = new ObservableField("");
    private MutableLiveData<CommentsListResponse> commentsObservable;

    private MutableLiveData<List<CommentModel>> addCommentsObservable;
    private MutableLiveData<CommentModel> updateCommentsObservable;
    private MutableLiveData<CommentModel> deleteCommentsObservable;

    private boolean isDataFound = true;
    private String _id;
    private String commentError;
    private CommentModel commentModel;
    private boolean isEdit;
    private int commentsCount;
    private boolean isLoading;
    private boolean isHasPrevious = true;

    private boolean isProgress;

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public void setProgress(boolean progress) {
        isProgress = progress;
        notifyDataChanged();
    }

    @Bindable
    public boolean isProgress() {
        return isProgress;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public ObservableField<String> likeCount = new ObservableField("");
    public ObservableField<String> shareCount = new ObservableField("");

    public CommentsViewModel(Application application) {
        super(application);
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setCommentModel(CommentModel commentModel) {
        this.commentModel = commentModel;
    }

    private boolean checkInputsValidation() {
        if (commentText.get() == null || commentText.get().trim().isEmpty()) {
            Toast.makeText(getApplication().getApplicationContext(), getApplication().getString(R.string.add_comment_warning), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Bindable
    public String getCommentError() {
        return commentError;
    }

    private void setCommentError(String commentError) {
        this.commentError = commentError;
        notifyDataChanged();
    }

    public void addOfferComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_ADD_OFFER_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to add offer comment"
                    , "");

            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.commentText.get().toString());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    addOfferComment(_id, commetnsRequest).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<AddCommentPayload>>() {
                        public void onCompleted() {
                            setProgress(false);
                        }

                        public void onError(java.lang.Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_ADD_OFFER_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to add offer comment"
                                    , throwable.getStackTrace().toString());
                        }

                        public void onNext(Response<AddCommentPayload> response) {
                            if (response.code() == 200) {
                                //getOfferComments(true);
                                setDataFound(true);
                                addCommentsObservable.postValue(response.body().getPayload());
                                commentText.set("");
                                addCommentCallBack
                                        .onCommentAdded(response.body().getPayload().get(response.body().getPayload().size() - 1), false);

                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_ADD_OFFER_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully add offer comment"
                                        , "");
                                return;
                            }
                            String errorMsg = null;
                            try {
                                String error = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(error);
                                errorMsg = jObjError.getJSONObject("error").get("en").toString();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_ADD_OFFER_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to add offer comment"
                                        , errorMsg);
                            } catch (Exception e) {
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_ADD_OFFER_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to add offer comment"
                                        , e.getStackTrace().toString());
                            } finally {
                                Toast.makeText(QurbaApplication.getContext(),
                                        errorMsg == null ?
                                                QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void updateOfferComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_UPDATE_OFFER_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to update offer comment"
                    , "");

            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.commentText.get());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    updateOfferComment(_id, commentModel.get_id(), commetnsRequest).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<UpdateCommentPayload>>() {
                        public void onCompleted() {
                            setProgress(false);
                        }

                        public void onError(java.lang.Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_UPDATE_OFFER_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to update offer comment"
                                    , throwable.getStackTrace().toString());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200) {
                                assert response.body() != null;
                                setUpdateResponse(response.body().getPayload());
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_OFFER_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully update offer comment"
                                        , "");
                                return;
                            }
                            String errorMsg = null;
                            try {
                                String error = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(error);
                                errorMsg = jObjError.getJSONObject("error").get("en").toString();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_OFFER_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to update offer comment"
                                        , errorMsg);
                            } catch (Exception e) {
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_OFFER_COMMENT_SUCCESS,
                                        Line.LEVEL_ERROR, "Failed to update offer comment"
                                        , e.getStackTrace().toString());
                            } finally {
                                Toast.makeText(QurbaApplication.getContext(),
                                        errorMsg == null ?
                                                QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void deleteOfferComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_DELETE_OFFER_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to delete offer comment"
                    , "");

            APICalls.Companion.getInstance().
                    deleteOfferComment(_id, commentModel.get_id()).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<UpdateCommentPayload>>() {
                        public void onCompleted() {
                        }

                        public void onError(java.lang.Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_DELETE_OFFER_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to delete offer comment"
                                    , throwable.getStackTrace().toString());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 204) {
                                setDeleteResponse();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_OFFER_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully delete offer comment"
                                        , "");
                                return;
                            }
                            String errorMsg = null;
                            try {
                                String error = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(error);
                                errorMsg = jObjError.getJSONObject("error").get("en").toString();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_OFFER_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to delete offer comment"
                                        , errorMsg);
                            } catch (Exception e) {
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_OFFER_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to delete offer comment"
                                        , e.getStackTrace().toString());
                            } finally {
                                Toast.makeText(QurbaApplication.getContext(),
                                        errorMsg == null ?
                                                QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                        , Toast.LENGTH_SHORT).show();
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

    public void getOfferComments(String delimiter, String direction, boolean includeDelimiter) {
        setLoading(true);

        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_GET_OFFER_COMMENTS_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to retrieve offer comments"
                    , "");


            APICalls.Companion.getInstance().getOfferComments(_id, setQueryMap(delimiter, direction, includeDelimiter)).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response<CommentsPayload>>() {

                public void onCompleted() {
                    setLoading(false);
                }

                public void onError(Throwable throwable) {
                    setLoading(false);
                    throwable.printStackTrace();
                    Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_GET_OFFER_COMMENTS_FAIL,
                            Line.LEVEL_ERROR, "Failed to retrieve offer comments"
                            , throwable.getStackTrace().toString());
                }

                public void onNext(Response<CommentsPayload> response) {
                    setLoading(false);
                    CommentsPayload productCommentPayload = response.body();
                    if (response.code() == 200) {
                        getCommentsObservable().postValue(productCommentPayload.getPayload());
                        setHasPrevious(productCommentPayload.getPayload().isHasPrevious() && productCommentPayload.getPayload().getPage() == 1);
                        setDataFound(productCommentPayload.getPayload().getTotal() > 0);
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_GET_OFFER_COMMENTS_SUCCESS,
                                Line.LEVEL_INFO, "Successfully retrieve offer comments"
                                , "");
                    } else {
                        String errorMsg = null;
                        try {
                            String error = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(error);
                            errorMsg = jObjError.getJSONObject("error").get("en").toString();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_GET_OFFER_COMMENTS_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve offer comments"
                                    , errorMsg);
                        } catch (Exception e) {
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_GET_OFFER_COMMENTS_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve offer comments"
                                    , e.getStackTrace().toString());
                        } finally {
                            Toast.makeText(QurbaApplication.getContext(),
                                    errorMsg == null ?
                                            QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void addProductComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_ADD_PRODUCT_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to add product comment"
                    , "");

            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.commentText.get().toString());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    addProductComments(_id, commetnsRequest).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<AddCommentPayload>>() {
                        public void onCompleted() {
                            setProgress(false);
                        }

                        public void onError(java.lang.Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_ADD_PRODUCT_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to add product comment"
                                    , throwable.getStackTrace().toString());
                        }

                        public void onNext(Response<AddCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 204) {
//                                getProductComments(true);
                                setDataFound(true);
                                addCommentsObservable.postValue(response.body().getPayload());
                                commentText.set("");
                                addCommentCallBack
                                        .onCommentAdded(response.body().getPayload().get(response.body().getPayload().size() - 1), false);

                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_ADD_PRODUCT_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully add product comment"
                                        , "");
                                return;
                            }
                            String errorMsg = null;
                            try {
                                String error = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(error);
                                errorMsg = jObjError.getJSONObject("error").get("en").toString();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_ADD_PRODUCT_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to add product comment"
                                        , errorMsg);
                            } catch (Exception e) {
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_ADD_PRODUCT_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to add product comment"
                                        , e.getStackTrace().toString());
                            } finally {
                                Toast.makeText(QurbaApplication.getContext(),
                                        errorMsg == null ?
                                                QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void updateProductComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_UPDATE_PRODUCT_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to update product comment"
                    , "");

            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.commentText.get());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    updateProductComments(_id, commentModel.get_id(), commetnsRequest).
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
                                    , throwable.getStackTrace().toString());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 204) {
//                                getProductComments(true);
                                setUpdateResponse(response.body().getPayload());
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_PRODUCT_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully update product comment"
                                        , "");
                                return;
                            }
                            String errorMsg = null;
                            try {
                                String error = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(error);
                                errorMsg = jObjError.getJSONObject("error").get("en").toString();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_PRODUCT_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to update product comment"
                                        , errorMsg);
                            } catch (Exception e) {
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_PRODUCT_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to update product comment"
                                        , e.getStackTrace().toString());
                            } finally {
                                Toast.makeText(QurbaApplication.getContext(),
                                        errorMsg == null ?
                                                QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void deleteProductComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_DELETE_PRODUCT_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to delete product comment"
                    , "");

            APICalls.Companion.getInstance().
                    deleteProductComment(_id, commentModel.get_id()).
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
                                    , throwable.getStackTrace().toString());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 204) {
//                                getProductComments(true);
                                setDeleteResponse();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_PRODUCT_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully delete product comment"
                                        , "");
                                return;
                            }
                            String errorMsg = null;
                            try {
                                String error = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(error);
                                errorMsg = jObjError.getJSONObject("error").get("en").toString();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_PRODUCT_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to delete product comment"
                                        , errorMsg);
                            } catch (Exception e) {
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_PRODUCT_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to delete product comment"
                                        , e.getStackTrace().toString());
                            } finally {
                                Toast.makeText(QurbaApplication.getContext(),
                                        errorMsg == null ?
                                                QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void getProductComments(String delimiter, String direction, boolean includeDelimiter) {
        setLoading(true);
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_GET_PRODUCT_COMMENTS_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to retrieve product comments"
                    , "");

            APICalls.Companion.getInstance().getProductComments(_id, setQueryMap(delimiter, direction, includeDelimiter)).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response<CommentsPayload>>() {

                public void onCompleted() {
                    setLoading(false);
                }

                public void onError(Throwable throwable) {
                    setLoading(false);
                    throwable.printStackTrace();
                    Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_GET_PRODUCT_COMMENTS_FAIL,
                            Line.LEVEL_ERROR, "Failed to retrieve product comments"
                            , throwable.getStackTrace().toString());
                }

                public void onNext(Response<CommentsPayload> response) {
                    setLoading(false);
                    CommentsPayload productCommentPayload = response.body();
                    if (response.code() == 200) {
                        getCommentsObservable().postValue(productCommentPayload.getPayload());
                        setHasPrevious(productCommentPayload.getPayload().isHasPrevious());
                        setDataFound(productCommentPayload.getPayload().getTotal() > 0);
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_GET_PRODUCT_COMMENTS_SUCCESS,
                                Line.LEVEL_INFO, "Successfully retrieve product comments"
                                , "");
                    } else {
                        String errorMsg = null;
                        try {
                            String error = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(error);
                            errorMsg = jObjError.getJSONObject("error").get("en").toString();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_GET_PRODUCT_COMMENTS_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve product comments"
                                    , errorMsg);
                        } catch (Exception e) {
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_GET_PRODUCT_COMMENTS_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve product comments"
                                    , e.getStackTrace().toString());
                        } finally {
                            Toast.makeText(QurbaApplication.getContext(),
                                    errorMsg == null ?
                                            QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void deletePlaceComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_DELETE_PLACE_COMMENT_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to delete place comment"
                    , "");

            APICalls.Companion.getInstance().
                    deletePlaceComment(_id, commentModel.get_id()).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<UpdateCommentPayload>>() {
                        public void onCompleted() {
                        }

                        public void onError(java.lang.Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_DELETE_PLACE_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to delete place comment"
                                    , throwable.getStackTrace().toString());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 204) {
//                                getProductComments(true);
                                setDeleteResponse();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_PLACE_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully delete place comment"
                                        , "");
                                return;
                            }
                            String errorMsg = null;
                            try {
                                String error = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(error);
                                errorMsg = jObjError.getJSONObject("error").get("en").toString();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_PLACE_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to delete place comment"
                                        , errorMsg);
                            } catch (Exception e) {
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_DELETE_PLACE_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to delete place comment"
                                        , e.getStackTrace().toString());
                            } finally {
                                Toast.makeText(QurbaApplication.getContext(),
                                        errorMsg == null ?
                                                QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void getPlaceComments(String delimiter, String direction, boolean includeDelimiter) {
        setLoading(true);
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_GET_PLACE_COMMENTS_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to retrieve place comments"
                    , "");

            APICalls.Companion.getInstance().getPlaceComments(_id, setQueryMap(delimiter, direction, includeDelimiter)).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response<CommentsPayload>>() {

                public void onCompleted() {
                    setLoading(false);
                }

                public void onError(Throwable throwable) {
                    setLoading(false);
                    throwable.printStackTrace();
                    Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_GET_PLACE_COMMENTS_FAIL,
                            Line.LEVEL_ERROR, "Failed to retrieve place comments"
                            , throwable.getStackTrace().toString());
                }

                public void onNext(Response<CommentsPayload> response) {
                    setLoading(false);
                    CommentsPayload productCommentPayload = response.body();
                    if (response.code() == 200) {
                        getCommentsObservable().postValue(productCommentPayload.getPayload());
                        setHasPrevious(productCommentPayload.getPayload().isHasPrevious());
                        setDataFound(productCommentPayload.getPayload().getTotal() > 0);
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_GET_PLACE_COMMENTS_SUCCESS,
                                Line.LEVEL_INFO, "Successfully retrieve place comments"
                                , "");
                    } else {
                        String errorMsg = null;
                        try {
                            String error = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(error);
                            errorMsg = jObjError.getJSONObject("error").get("en").toString();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_GET_PLACE_COMMENTS_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve place comments"
                                    , errorMsg);
                        } catch (Exception e) {
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_GET_PLACE_COMMENTS_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve place comments"
                                    , e.getStackTrace().toString());
                        } finally {
                            Toast.makeText(QurbaApplication.getContext(),
                                    errorMsg == null ?
                                            QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void updatePlaceComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_UPDATE_PLACE_COMMENT_ATTEMPT,
                    Line.LEVEL_ERROR, "User attempt to update place comment"
                    , "");

            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.commentText.get().toString());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    updatePlaceComments(_id, commentModel.get_id(), commetnsRequest).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<UpdateCommentPayload>>() {
                        public void onCompleted() {
                            setProgress(false);
                        }

                        public void onError(java.lang.Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_UPDATE_PLACE_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to update place comment"
                                    , throwable.getStackTrace().toString());
                        }

                        public void onNext(Response<UpdateCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 204) {
//                                getProductComments(true);
                                setUpdateResponse(response.body().getPayload());
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_PLACE_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully update place comment"
                                        , "");
                                return;
                            }
                            String errorMsg = null;
                            try {
                                String error = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(error);
                                errorMsg = jObjError.getJSONObject("error").get("en").toString();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_PLACE_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to update place comment"
                                        , errorMsg);
                            } catch (Exception e) {
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_UPDATE_PLACE_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to update place comment"
                                        , e.getStackTrace().toString());
                            } finally {
                                Toast.makeText(QurbaApplication.getContext(),
                                        errorMsg == null ?
                                                QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public void addPlaceComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (!checkInputsValidation())
                return;

            setProgress(true);

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_ADD_PLACE_COMMENT_ATTEMPT,
                    Line.LEVEL_ERROR, "User attempt to add place comment"
                    , "");

            CommetnsPayload commetnsPayload = new CommetnsPayload();
            commetnsPayload.setComment(this.commentText.get());
            CommetnsRequest commetnsRequest = new CommetnsRequest(commetnsPayload);

            APICalls.Companion.getInstance().
                    addPlaceComment(_id, commetnsRequest).
                    subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Response<AddCommentPayload>>() {
                        public void onCompleted() {
                            setProgress(false);
                        }

                        public void onError(java.lang.Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_ADD_PLACE_COMMENT_FAIL,
                                    Line.LEVEL_ERROR, "Failed to add place comment"
                                    , throwable.getStackTrace().toString());
                        }

                        public void onNext(Response<AddCommentPayload> response) {
                            if (response.code() == 200 || response.code() == 201) {
//                                getProductComments(true);
                                setDataFound(true);
                                addCommentsObservable.postValue(response.body().getPayload());
                                commentText.set("");
                                addCommentCallBack
                                        .onCommentAdded(response.body().getPayload().get(response.body().getPayload().size() - 1), false);

                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_ADD_PLACE_COMMENT_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully add place comment"
                                        , "");
                                return;
                            }
                            String errorMsg = null;
                            try {
                                String error = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(error);
                                errorMsg = jObjError.getJSONObject("error").get("en").toString();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_ADD_PLACE_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to add place comment"
                                        , errorMsg);
                            } catch (Exception e) {
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_ADD_PLACE_COMMENT_FAIL,
                                        Line.LEVEL_ERROR, "Failed to add place comment"
                                        , e.getStackTrace().toString());
                            } finally {
                                Toast.makeText(QurbaApplication.getContext(),
                                        errorMsg == null ?
                                                QurbaApplication.getContext().getString(R.string.something_wrong) : errorMsg
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }
        Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public MutableLiveData<CommentsListResponse> getCommentsObservable() {
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

    @Bindable
    public boolean isHasPrevious() {
        return this.isHasPrevious;
    }

    public void setAddCommentCallBack(CommentsCallBack addCommentCallBack) {
        this.addCommentCallBack = addCommentCallBack;
    }

    public void setDataFound(boolean bl) {
        this.isDataFound = bl;
        this.notifyDataChanged();
    }

    public void setHasPrevious(boolean hasPrevious) {
        isHasPrevious = hasPrevious;
        notifyDataChanged();
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    private void setUpdateResponse(CommentModel model){
        commentModel.setComment(model.getComment());
        updateCommentsObservable.postValue(commentModel);
        addCommentCallBack
                .onCommentUpdated(commentModel, false);
        commentText.set("");
        setEdit(false);
        setCommentModel(null);
    }

    private void setDeleteResponse(){
        addCommentCallBack
                .onCommentDeleted(false, commentsCount);

        deleteCommentsObservable.postValue(new CommentModel());
    }
}

