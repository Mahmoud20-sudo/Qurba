package com.qurba.android.ui.place_details.view_models

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.models.PlaceModel
import com.qurba.android.network.models.response_models.StringOnlyResponse
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity
import com.qurba.android.utils.*
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PlaceItemViewModel : BaseObservable() {

    var isFavourite: ObservableField<Boolean> = ObservableField()

    private lateinit var placeModel: PlaceModel

    private lateinit var folloPlaceSubscriber: Subscriber<Response<StringOnlyResponse>>
    private lateinit var unFolloPlaceSubscriber: Subscriber<Response<StringOnlyResponse>>
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()

    fun bind(faqs: PlaceModel) {
        this.placeModel = faqs
        isFavourite.set(placeModel.isLikedByUser)
    }

    fun openPlaceDetails(): View.OnClickListener? {
        return View.OnClickListener { v: View ->
            val intent = Intent(v.context, PlaceDetailsActivity::class.java)
            if (SharedPreferencesManager.getInstance().language == "en") {
                intent.putExtra("place_name", placeModel.name.en)
            } else {
                intent.putExtra("place_name", placeModel.name.ar)
            }
            intent.putExtra("unique_name", placeModel.uniqueName)
            intent.putExtra("place_id", placeModel._id)
            v.context.startActivity(intent)
        }
    }

    fun likePlace(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            placeModel.isLikedByUser = true;
            placeModel.likesCount = placeModel.likesCount + 1;
            isFavourite.set(true)

            val call = APICalls.getInstance().likePlace(id)
            folloPlaceSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onNext(t: Response<StringOnlyResponse>) {
                    if (t?.code() == 200) {
                        Log.e("message", t.message())
                    } else {
                        Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()

                        placeModel.isLikedByUser = false;
                        placeModel.likesCount = placeModel.likesCount - 1;
                        isFavourite.set(false)
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

    private fun unLikePlace(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            placeModel.isLikedByUser = false;
            placeModel.likesCount = placeModel.likesCount - 1;
            isFavourite.set(false)

            val call = APICalls.getInstance().disLikePlace(id)
            unFolloPlaceSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onNext(t: Response<StringOnlyResponse>) {
                    if (t?.code() == 200) {
                        Log.e("message", t?.message())
                    } else {
                        placeModel.isLikedByUser = true;
                        placeModel.likesCount = placeModel.likesCount + 1;
                        isFavourite.set(true)
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

    @Bindable
    fun getPlaceName(): String? {
        return placeModel.getName().getEn()
    }

    private fun setActions() {
        if (!placeModel.isLikedByUser) {
            likePlace(placeModel._id)
        } else {
            unLikePlace(placeModel._id)
        }
    }

    fun onLoginFinished() {
        setActions()
    }

}