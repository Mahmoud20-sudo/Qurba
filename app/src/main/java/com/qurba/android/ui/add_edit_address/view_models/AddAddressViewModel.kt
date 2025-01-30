package com.qurba.android.ui.add_edit_address.view_models

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.databinding.DialogUpdateBinding
import com.qurba.android.network.APICalls
import com.qurba.android.network.APICalls.Companion.getInstance
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.QurbaLogger.Companion.standardFirebaseAnalyticsEventsLogging
import com.qurba.android.network.models.AddAddressModel
import com.qurba.android.network.models.BaseModel
import com.qurba.android.network.models.request_models.AddAddressPayload
import com.qurba.android.network.models.request_models.AddAddressRequestModel
import com.qurba.android.network.models.request_models.CartUpdateModel
import com.qurba.android.network.models.request_models.CartUpdatePayload
import com.qurba.android.network.models.response_models.AddAddressResponseModel
import com.qurba.android.network.models.response_models.DeliveryValidationPayload
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AddAddressViewModel(application: Application) : BaseViewModel(application) {
    private var responseCallback: Response<BaseModel>? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var subscriber: Subscriber<Response<AddAddressResponseModel>>? = null
    private var deliveryAreaPayloadSubscriber: Subscriber<Response<DeliveryValidationPayload>>? = null
    private var deliveryObservable: MutableLiveData<AddAddressModel>? = null
    private var deliveryAreaPayObservable: MutableLiveData<DeliveryValidationPayload>? = null
    private var deleteAddressObservable: MutableLiveData<AddAddressModel>? = null
    private var isLoading = false

    @get:Bindable
    var deliveryArea: String? = null
        private set

    @get:Bindable
    var isEditingAddress = false

    @get:Bindable
    var isFromCheckout = false
    var streetNameError: String? = null
        set(streetNameError) {
            field = streetNameError
            notifyDataChanged()
        }
    var buildingNumberError: String? = null
        set(buildingNumberError) {
            field = buildingNumberError
            notifyDataChanged()
        }
    var flatNumberError: String? = null
        set(flatNumberError) {
            field = flatNumberError
            notifyDataChanged()
        }
    var floorNumberError: String? = null
        set(floorNumberError) {
            field = floorNumberError
            notifyDataChanged()
        }
    var nearestLandMarkError: String? = null
        private set
    var branchedFromError: String? = null
        set(branchedFromError) {
            field = branchedFromError
            notifyDataChanged()
        }
    var streetName: ObservableField<String> = ObservableField<String>("")
    var buildingNumber: ObservableField<String> = ObservableField<String>("")
    var floorNumber: ObservableField<String> = ObservableField<String>("")
    var flatNumber: ObservableField<String> = ObservableField<String>("")
    var nearesLandmark: ObservableField<String> = ObservableField<String>("")
    var othersTitle: ObservableField<String> = ObservableField<String>("")
    var branchedFrom: ObservableField<String> = ObservableField<String>("")

    private var label: String? = ""
    var address: AddAddressModel? = null
        private set
    private var activity: BaseActivity? = null
    fun setActivity(activity: BaseActivity?) {
        this.activity = activity
    }

    fun setLabel(label: String?) {
        this.label = label
    }

    private fun checkInputsValidation(v: View) {
        streetNameError = null
        buildingNumberError = null
        flatNumberError = null
        floorNumberError = null
        setNearesLandmarkError(null)
        branchedFromError = null
        if (streetName.get() == null || streetName.get()!!.isEmpty()) {
            streetNameError = getApplication<Application>().getString(R.string.required_field)
        } else if (streetName.get()!!.trim { it <= ' ' }.length < 6) {
            streetNameError = getApplication<Application>().getString(R.string.invalid_street_length)
        } else if (branchedFrom.get() == null || branchedFrom.get()!!.isEmpty()) {
            branchedFromError = getApplication<Application>().getString(R.string.required_field)
        } else if (branchedFrom.get()!!.trim { it <= ' ' }.length < 4) {
            branchedFromError = getApplication<Application>().getString(R.string.invalid_bracnhed_length)
        } else if (buildingNumber.get() == null || buildingNumber.get()!!.isEmpty()) {
            buildingNumberError = getApplication<Application>().getString(R.string.required_field)
        } else if (flatNumber.get() == null || flatNumber.get()!!.isEmpty()) {
            flatNumberError = getApplication<Application>().getString(R.string.required_field)
        } else if (floorNumber.get() == null || floorNumber.get()!!.isEmpty()) {
            floorNumberError = getApplication<Application>().getString(R.string.required_field)
        } else if (label == null || label!!.isEmpty()) {
            Toast.makeText(QurbaApplication.getContext(), getApplication<Application>().getString(R.string.select_label_error_msg), Toast.LENGTH_LONG).show()
        } else if (nearesLandmark.get() == null || nearesLandmark.get()!!.isEmpty()) setNearesLandmarkError(getApplication<Application>().getString(R.string.required_field)) else if (label.equals("other", ignoreCase = true) && othersTitle.get() == null) Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.select_other_label_error_msg), Toast.LENGTH_LONG).show() else addAddress(v)
    }

    fun setAreaTitle(address: AddAddressModel) {
        deliveryArea = (address.city!!.name.en
                + ", " + address.area!!.name.en)
        notifyDataChanged()
    }

    fun setSlectedAddress(address: AddAddressModel?) {
        this.address = address
        streetName.set(address?.street)
        buildingNumber.set(address?.building)
        floorNumber.set(address?.floor)
        flatNumber.set(address?.flat)
        nearesLandmark.set(address?.nearestLandmark)
        branchedFrom.set(address?.branchedStreet)
        label = address?.label
        //        setAreaTitle(address);
    }

    @Bindable
    override fun isLoading(): Boolean {
        return isLoading
    }

    fun setLoading(loading: Boolean) {
        isLoading = loading
        notifyDataChanged()
    }

    fun addAddress(v: View) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            setLoading(true)
            v.isEnabled = false
            val addAddressRequestModel = AddAddressRequestModel()
            val payload = AddAddressPayload()
            payload.country = if (address!!.country!!.id == null) address!!.country!!._id else address!!.country!!.id
            payload.area = if (address!!.area!!.id == null) address!!.area!!._id else address!!.area!!.id
            payload.city = if (address!!.city!!.id == null) address!!.city!!._id else address!!.city!!.id
            payload.building = buildingNumber.get()
            payload.flat = flatNumber.get()
            payload.floor = floorNumber.get()
            payload.branchedStreet = branchedFrom.get()
            payload.nearestLandmark = nearesLandmark.get()
            payload.street = streetName.get()
            payload.label = if (label.equals("other", ignoreCase = true)) othersTitle.get() else label
            addAddressRequestModel.payload = payload
            logging(getApplication<Application>().applicationContext,
                    if (isEditingAddress) Constants.USER_ADDRESS_UPDATE_SUBMIT else Constants.USER_ADDRESS_ADD_SUBMIT, LEVEL_INFO,
                    if (isEditingAddress) "User attempting to edit address " else "User attempting to edit address ", "")
            val call = if (address!!.getId() == null) getInstance().addAddress(addAddressRequestModel) else getInstance().updateddress(address!!.getId()!!, addAddressRequestModel)
            subscriber = object : Subscriber<Response<AddAddressResponseModel>>() {
                override fun onCompleted() {
                    setLoading(false)
                    v.isEnabled = true
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logging(getApplication<Application>().applicationContext,
                            if (isEditingAddress) Constants.USER_ADDRESS_UPDATE_FAIL else Constants.USER_ADDRESS_ADD_SUCCESS, LEVEL_INFO,
                            if (isEditingAddress) "User successfully add address " else "User successfully edit address ", Log.getStackTraceString(e))
                    Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
                }

                override fun onNext(response: Response<AddAddressResponseModel>) {
                    val nearestAreaResponseModel = response.body()
                    if (response.code() == 200 || response.code() == 201) {
                        observable?.postValue(nearestAreaResponseModel!!.payload)
                        logging(getApplication<Application>().applicationContext,
                                if (isEditingAddress) Constants.USER_ADDRESS_UPDATE_SUCCESS else Constants.USER_ADDRESS_ADD_SUCCESS, LEVEL_INFO,
                                if (isEditingAddress) "User successfully add address " else "User successfully edit address ", "")
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "EGP")
                        standardFirebaseAnalyticsEventsLogging(activity!!, FirebaseAnalytics.Event.ADD_SHIPPING_INFO, bundle)
                    } else {
                        try {
                            val error = response.errorBody()!!.string()
                            val jObjError = JSONObject(error)
                            val errorMsg = jObjError[sharedPref.language].toString()
                            logging(getApplication<Application>().applicationContext,
                                    if (isEditingAddress) Constants.USER_ADDRESS_UPDATE_FAIL else Constants.USER_ADDRESS_ADD_FAIL, LEVEL_ERROR,
                                    if (isEditingAddress) "User failed to add address " else "User failed to edit address ", errorMsg)
                            Toast.makeText(QurbaApplication.getContext(), errorMsg, Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            logging(getApplication<Application>().applicationContext,
                                    if (isEditingAddress) Constants.USER_ADDRESS_UPDATE_FAIL else Constants.USER_ADDRESS_ADD_FAIL, LEVEL_ERROR,
                                    if (isEditingAddress) "User failed to add address " else "User failed to edit address ", Log.getStackTraceString(e))
                            Toast.makeText(QurbaApplication.getContext(),
                                    QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
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

    fun deleteAddress(addAddressModel: AddAddressModel) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            logging(getApplication<Application>().applicationContext,
                    Constants.USER_ADDRESS_DELETE_SUBMIT, LEVEL_INFO,
                    "User attempting to delete address", "")
            val call = getInstance().deleteAddress(addAddressModel.getId()!!)
            val baseSubscriber: Subscriber<Response<BaseModel>> = object : Subscriber<Response<BaseModel>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logging(getApplication<Application>().applicationContext,
                            Constants.USER_ADDRESS_DELETE_FAIL, LEVEL_ERROR,
                            "User failed to add address ", Log.getStackTraceString(e))
                    Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
                }

                override fun onNext(response: Response<BaseModel>) {
                    if (response.code() == 200 || response.code() == 204) {
                        deleteObservable?.postValue(addAddressModel)
                        logging(getApplication<Application>().applicationContext,
                                Constants.USER_ADDRESS_DELETE_SUCCESS, LEVEL_INFO,
                                "User deleted address successfully", "")
                    } else {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMsg = jObjError.getJSONArray("error").getJSONObject(0)["message"].toString()
                            logging(getApplication<Application>().applicationContext,
                                    Constants.USER_ADDRESS_DELETE_FAIL, LEVEL_ERROR,
                                    "User failed to delete address ", errorMsg)
                            Toast.makeText(QurbaApplication.getContext(), errorMsg, Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            logging(getApplication<Application>().applicationContext,
                                    Constants.USER_ADDRESS_DELETE_FAIL, LEVEL_ERROR,
                                    "User failed to delete address ", Log.getStackTraceString(e))
                            Toast.makeText(QurbaApplication.getContext(),
                                    QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(baseSubscriber)
        } else {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
        }
    }

    fun checkIfNotDelivering(placeId: String?, areaId: String?) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            setLoading(true)
            val call = getInstance().checkDelivering(placeId!!, areaId!!)
            deliveryAreaPayloadSubscriber = object : Subscriber<Response<DeliveryValidationPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    setLoading(false)
                }

                override fun onNext(response: Response<DeliveryValidationPayload>) {
                    setLoading(false)
                    deliveryAreaObservable?.postValue(response.body())
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(deliveryAreaPayloadSubscriber)
        } else {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
        }
    }

    val observable: MutableLiveData<AddAddressModel>?
        get() {
            if (deliveryObservable == null) {
                deliveryObservable = MutableLiveData()
            }
            return deliveryObservable
        }
    val deleteObservable: MutableLiveData<AddAddressModel>?
        get() {
            if (deleteAddressObservable == null) {
                deleteAddressObservable = MutableLiveData()
            }
            return deleteAddressObservable
        }
    val deliveryAreaObservable: MutableLiveData<DeliveryValidationPayload>?
        get() {
            if (deliveryAreaPayObservable == null) {
                deliveryAreaPayObservable = MutableLiveData()
            }
            return deliveryAreaPayObservable
        }

    fun updateAddress(): View.OnClickListener {
        return View.OnClickListener { v: View -> checkInputsValidation(v) }
    }

    fun deleteAddressClickEvent(addAddressModel: AddAddressModel): View.OnClickListener {
        return  View.OnClickListener { v: View ->
            if (sharedPref.cart != null) {
                if (addAddressModel.area!!._id.equals(sharedPref.selectedCachedArea.area!!._id, ignoreCase = true)) {
                    activity!!.showClearCartDialog(sharedPref.cart.placeModel.name.en, {
                        sharedPref.clearCart()
                        deleteAddress(addAddressModel)
                    }, activity!!.getString(R.string.already_have_items) + " " + sharedPref.cart.placeModel.name.en + ", "
                            + activity!!.getString(R.string.and_delete_Address))
                    return@OnClickListener
                }
            }
            showDeleteConfirmialog(v.context, addAddressModel)
        }
    }

    fun showDeleteConfirmialog(context: Context, addAddressModel: AddAddressModel): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val binding: DialogUpdateBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_update, null, false)
        builder.setView(binding.root)
        val dialog = builder.create()
        binding.notNowTv.text = context.getString(R.string.cancel)
        binding.updateNowTv.text = context.getString(R.string.yes_delete)
        binding.contentTv.text = context.getString(R.string.delete_address_msg)
        binding.titleTv.text = context.getString(R.string.delete_address_title)
        binding.notNowTv.setOnClickListener { view -> dialog.dismiss() }
        binding.updateNowTv.setOnClickListener { view ->
            deleteAddress(addAddressModel)
            dialog.dismiss()
        }
        if (dialog.window != null) {
            dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        dialog.show()
        return dialog
    }

    fun setNearesLandmarkError(nearestLandMarkError: String?) {
        this.nearestLandMarkError = nearestLandMarkError
        notifyDataChanged()
    }


}