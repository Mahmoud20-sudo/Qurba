package com.qurba.android.ui.my_orders.view_models;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.network.APICalls;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.OrdersModel;
import com.qurba.android.network.models.OrdersPayload;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SystemUtils;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.qurba.android.utils.extenstions.ExtesionsKt.showToastMsg;

public class MyOrderViewModel extends BaseViewModel {

    private boolean isDataFound = true;
    private boolean isLoading;
    private Subscriber<Response<OrdersPayload>> subscriber;
    private MutableLiveData<List<OrdersModel>> ordersObservalble;
    private BaseActivity activity;

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public MyOrderViewModel(@NonNull Application application) {
        super(application);
    }

    @Bindable
    public boolean isLoading() {
        return isLoading;
    }

    @Bindable
    public boolean isDataFound() {
        return isDataFound;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public void setDataFound(boolean dataFound) {
        isDataFound = dataFound;
        notifyDataChanged();
    }

    public void getMyOrders(int page) {
        if (page == 1)
            setLoading(true);

        if (SystemUtils.isNetworkAvailable(getApplication())) {

            Observable<Response<OrdersPayload>> call = APICalls.Companion.getInstance().getOrders(page);
            subscriber = new Subscriber<Response<OrdersPayload>>() {
                @Override
                public void onCompleted() {
                    setLoading(false);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    setLoading(false);
                    showToastMsg(activity, activity.getString(R.string.something_wrong));
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_ORDER_MY_ORDERS_CLICK_FAIL
                            , Line.LEVEL_ERROR, "Failed to retrieve my orders", e.getLocalizedMessage());
                }

                @Override
                public void onNext(Response<OrdersPayload> response) {
                    setLoading(false);
                    if (response.code() == 200) {
                        getOrdersObservalble().postValue(response.body().getPayload().getDocs());

                        if (page == 1 && response.body().getPayload().getDocs().isEmpty())
                            setDataFound(false);

                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_ORDER_MY_ORDERS_CLICK_SUCCESS,
                                Line.LEVEL_INFO, "My orders has been successfully returned", "");

                    } else {
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_ORDER_MY_ORDERS_CLICK_FAIL
                                , Line.LEVEL_ERROR, "Failed to retrieve my orders", response.code() + " " + response.errorBody());
                        showToastMsg(activity, activity.getString(R.string.something_wrong));
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } else {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public MutableLiveData<List<OrdersModel>> getOrdersObservalble() {
        if (ordersObservalble == null) {
            ordersObservalble = new MutableLiveData<>();
        }
        return ordersObservalble;
    }

}