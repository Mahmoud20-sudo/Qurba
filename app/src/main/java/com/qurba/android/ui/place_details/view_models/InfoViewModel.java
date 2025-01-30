package com.qurba.android.ui.place_details.view_models;

import static com.qurba.android.utils.extenstions.ExtesionsKt.showErrorMsg;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.network.APICalls;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.PlaceDetailsModel;
import com.qurba.android.network.models.response_models.PlaceDetailsResponseModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.DateUtils;
import com.qurba.android.utils.SharedPreferencesManager;
import com.qurba.android.utils.SystemUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InfoViewModel extends BaseViewModel {

    private Subscriber<Response<PlaceDetailsResponseModel>> subscriber;
    private MutableLiveData<PlaceDetailsModel> placeDetailsObservable;
    private boolean isLoading;
    private PlaceDetailsModel placeDetailsModel = new PlaceDetailsModel();
    private BaseActivity activity;

    private boolean isCollapsed;

    public void setCollapsed(boolean collapsed) {
        isCollapsed = collapsed;
        notifyDataChanged();
    }

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public InfoViewModel(@NonNull Application application) {
        super(application);
    }

    @Bindable
    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    @Bindable
    public String getDescription() {
        return placeDetailsModel.getShortDescription() != null ? placeDetailsModel.getShortDescription().getEn() : "";
    }

    @Bindable
    public String getAbout() {
        return placeDetailsModel.getAbout() == null ? "" : placeDetailsModel.getAbout().getEn();
    }

    @Bindable
    public boolean isHaveAbout() {
        return placeDetailsModel.getAbout() == null || placeDetailsModel.getAbout().getEn() == null;
    }

    @Bindable
    public boolean isHavingNumber() {
        return placeDetailsModel.getTelephoneNumber() != null || placeDetailsModel.getMobileNumber() != null;
    }

    @Bindable
    public String getPlaceLandLineNumber() {
        return placeDetailsModel.getTelephoneNumber();
    }

    @Bindable
    public String getPlaceOpenClosed() {
        String todayOpeningTime = "";
        String todayClosingTime = "";
        String availabilty = "";
        String[] keys = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        if (placeDetailsModel.getAvailability() != null)
            availabilty = placeDetailsModel.getAvailability().getEn();
//        if (placeDetailsModel.isHasOpeningTimes()) {
//            for (DayModel dayModel : placeDetailsModel.getOpeningTimes())
//                if (dayModel.getDay().equalsIgnoreCase(DateUtils.getCurrentDayName())) {
//                    todayOpeningTime = " - " + dayModel.getShifts().get(0).getOpens();
//                    todayClosingTime = dayModel.getShifts().get(0).getCloses();
//                }
//        }
        if (placeDetailsModel.getOpeningTimes() != null) {
            for (int i = 0; i < keys.length; i++)
                if (keys[i].equalsIgnoreCase(DateUtils.getCurrentDayName()) && placeDetailsModel.getOpeningTimes().get(keys[i]).isDayOpen()) {
                    todayOpeningTime = " - " + placeDetailsModel.getOpeningTimes().get(keys[i]).getShifts().get(0).getOpens().getHour() + ":" + placeDetailsModel.getOpeningTimes().get(keys[i]).getShifts().get(0).getOpens().getMinutes();
                    todayClosingTime = " - " + placeDetailsModel.getOpeningTimes().get(keys[i]).getShifts().get(0).getCloses().getHour() + ":" + placeDetailsModel.getOpeningTimes().get(keys[i]).getShifts().get(0).getCloses().getMinutes();
                }
        }

        return placeDetailsModel.isOpenNow() ?
                activity.getString(R.string.open_now) + todayOpeningTime + "" + todayClosingTime
                : availabilty;
    }

    @Bindable
    public String getPlaceMobileNumber() {
        return placeDetailsModel.getMobileNumber();
    }

    @Bindable
    public String getPlaceAddress() {
        String address = "";

        if (placeDetailsModel.getAddress() == null)
            return address;

        if (placeDetailsModel.getAddress() != null && placeDetailsModel.getAddress().getFirstLine() != null && !placeDetailsModel.getAddress().getFirstLine().getEn().equals("NA")
                && !placeDetailsModel.getAddress().getFirstLine().getAr().equals("NA")) {
            if (SharedPreferencesManager.getInstance().getLanguage().equals("en")) {
                address = placeDetailsModel.getAddress().getFirstLine().getEn() + " "
                        + (placeDetailsModel.getAddress().getArea() == null ? ""
                        : placeDetailsModel.getAddress().getArea().getName().getEn());
            } else {
                address = placeDetailsModel.getAddress().getFirstLine().getAr() + " "
                        + placeDetailsModel.getAddress().getArea().getName().getAr();
            }
        } else {
            if (placeDetailsModel.getAddress().getArea().getName().getEn() != null) {
                address = placeDetailsModel.getAddress().getArea().getName().getEn();
            } else {
                address = "";
            }
        }
        return address;
    }

    @Bindable
    public boolean isHaveFacilities() {
        return placeDetailsModel.getFacilities() == null || placeDetailsModel.getFacilities().isEmpty();
    }

    @Bindable
    public boolean isHaveGarage() {
        if (placeDetailsModel.getAddress() == null || placeDetailsModel.getAddress().getNearestParking() == null)
            return true;
        return placeDetailsModel.getAddress().getNearestParking().isEmpty() || placeDetailsModel.getAddress().getNearestParking().get(0).getEn() == null;
    }

    @Bindable
    public boolean isHaveGallery() {
        return placeDetailsModel.getPlaceGalleryPicturesUrls() == null || placeDetailsModel.getPlaceGalleryPicturesUrls().isEmpty();
    }

    @Bindable
    public boolean isHaveLandmarks() {
        if (placeDetailsModel.getAddress() == null || placeDetailsModel.getAddress().getNearestLandmark() == null)
            return true;
        return placeDetailsModel.getAddress().getNearestLandmark().isEmpty() || placeDetailsModel.getAddress().getNearestLandmark().get(0).getEn() == null;
    }

    @Bindable
    public boolean isHaveOtherBranches() {
        return placeDetailsModel.getBranches() == null || placeDetailsModel.getBranches().isEmpty();
    }

    @Bindable
    public boolean isHasOpeningTimes() {
        return placeDetailsModel.getOpeningTimes() != null && !placeDetailsModel.getOpeningTimes().isEmpty();
    }

    @Bindable
    public boolean isCollapsed() {
        return isCollapsed;
    }

    public void getPlaceDetails(String _id) {
        if (SystemUtils.isNetworkAvailable(activity)) {
            //setLoading(true);

            QurbaLogger.Companion.logging(activity,
                    Constants.USER_GET_PLACE_INFO_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to retrieve place info"
                    , "");

            Observable<Response<PlaceDetailsResponseModel>> call = APICalls.Companion.getInstance().getPlaceDetails(_id);
            subscriber = new rx.Subscriber<Response<PlaceDetailsResponseModel>>() {
                @Override
                public void onCompleted() {
                    setLoading(false);
                }

                @Override
                public void onError(Throwable e) {
                    setLoading(false);
                    e.printStackTrace();
                    QurbaLogger.Companion.logging(activity,
                            Constants.USER_GET_PLACE_INFO_FAIL,
                            Line.LEVEL_ERROR, "Failed to retrieve place info"
                            , e.getMessage());
                }

                @Override
                public void onNext(Response<PlaceDetailsResponseModel> response) {
                    setLoading(false);
                    if (response.code() == 200 && !Objects.requireNonNull(response.body())
                            .getType().equalsIgnoreCase("error")) {
                        placeDetailsModel = response.body().getPayload();
                        getDetailsObservable().postValue(placeDetailsModel);
                        QurbaLogger.Companion.logging(activity,
                                Constants.USER_GET_PLACE_INFO_SUCCESS,
                                Line.LEVEL_INFO, "Successfully retrieve place info"
                                , "");
                        notifyDataChanged();
                    } else {
                        assert response.errorBody() != null;
                        try {
                            showErrorMsg(response.errorBody().string(),
                                    Constants.USER_GET_PLACE_INFO_FAIL,
                                    "Failed to retrieve place info ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } else {
            Toast.makeText(activity, activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public MutableLiveData<PlaceDetailsModel> getDetailsObservable() {
        if (placeDetailsObservable == null) {
            placeDetailsObservable = new MutableLiveData<>();
        }
        return placeDetailsObservable;
    }


    public View.OnClickListener callBranch() {
        return v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", ((TextView) v).getText().toString(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        };
    }
}
