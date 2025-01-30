package com.qurba.android.ui.profile.view_models

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.graphics.PathUtils
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import automatic
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.ObjectMetadata
import com.github.dhaval2404.imagepicker.util.FileUtil
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.BuildConfig
import com.qurba.android.R
import com.qurba.android.network.APICalls.Companion.instance
import com.qurba.android.network.CognitoClient.getTransferUtility
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.UserDataModel
import com.qurba.android.network.models.request_models.auth.UpdateUserDataPayload
import com.qurba.android.network.models.request_models.auth.UpdateUserDataRequestModel
import com.qurba.android.network.models.response_models.CognitoResponseModel
import com.qurba.android.network.models.response_models.GuestLoginResponseModel
import com.qurba.android.network.models.response_models.UpdateProfileResponseModel
import com.qurba.android.ui.auth.views.VerifyAccountActivity
import com.qurba.android.utils.*
import com.qurba.android.utils.SystemUtils.isProbablyArabic
import com.qurba.android.utils.extenstions.setInentResult
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import rx.Subscriber
import java.io.File
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.shouheng.compress.Compress
import me.shouheng.compress.strategy.Strategies


class EditProfileViewModel(application: Application) : BaseViewModel(application),
    Observable {
    private var observer: TransferObserver? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var user: UserDataModel? = sharedPref.user
    private var phoneError: String? = null
    private var firstNameError: String? = null
    private var lastNameError: String? = null
    private var activity: BaseActivity? = null
    private var response: Response<UpdateProfileResponseModel>? = null
    var phoneUtil: PhoneNumberUtil? = null
    private var uri: Uri? = null

    fun setUri(uri: Uri?) {
        this.uri = uri
    }

    fun setUser() {
        this.user = sharedPref.user
    }

    @Bindable
    var phoneNumber: String? = user?.mobileNumber

    @Bindable
    var firstName: String? = user?.firstName

    @Bindable
    var lastName: String? = user?.lastName

    @Bindable
    var email: String? = user?.email
    var image: String? = user?.profilePictureUrl

    fun setActivity(activity: BaseActivity) {
        this.activity = activity
        phoneUtil = PhoneNumberUtil.createInstance(activity)
    }

    @Bindable
    fun getPhoneError(): String? {
        return phoneError
    }

    @Bindable
    fun getFirstNameError(): String? {
        return firstNameError
    }

    @Bindable
    fun getLastNameError(): String? {
        return lastNameError
    }

    private fun setPhoneError(phoneError: String?) {
        this.phoneError = phoneError
        notifyDataChanged()
    }

    private fun setFirstNameError(firstNameError: String?) {
        this.firstNameError = firstNameError
        notifyDataChanged()
    }

    private fun setLastNameError(lastNameError: String?) {
        this.lastNameError = lastNameError
        notifyDataChanged()
    }

    @get:Bindable
    val isGuest: Boolean
        get() = sharedPref.isGuest

    private fun checkInputsValidation(v: View) {
        try {
            setFirstNameError(null)
            setLastNameError(null)
            setPhoneError(null)

            if (phoneNumber == null || phoneNumber?.trim { it <= ' ' }?.length == 0) {
                setPhoneError(activity?.getString(R.string.required_field))
            } else if (ValidationUtils.validatePhoneLength(phoneNumber!!.trim()) || phoneUtil?.isValidNumber(
                    phoneUtil?.parse(phoneNumber, "EG")
                ) == false
            ) {
                setPhoneError(getApplication<Application>().getString(R.string.invalid_phone))
            } else if (firstName == null || firstName?.trim { it <= ' ' }?.length == 0) {
                setFirstNameError(activity?.getString(R.string.required_field))
            } else if (lastName == null || lastName?.trim { it <= ' ' }?.length == 0) {
                setLastNameError(activity?.getString(R.string.required_field))
            } else if (uri == null) {
                updateProfile(v, null)
            } else {
                uploadFile(v)
            }
        } catch (e: Exception) {
            logging(
                activity!!,
                Constants.USER_UPDATE_PROFILE_FAIL, LEVEL_ERROR,
                "User failed to update his profile ", e.stackTraceToString()
            )
        }
    }

    fun updateProfile(v: View, imageUrl: String?) {
        if (SystemUtils.isNetworkAvailable(activity)) {

            (v as CircularProgressButton).startAnimation()

            logging(
                activity!!,
                Constants.USER_UPDATE_PROFILE_ATTEMPT, LEVEL_INFO,
                "User trying to update his profile ",
            )

            val updateUserDataPayload = UpdateUserDataPayload()
            updateUserDataPayload.firstName = firstName
            updateUserDataPayload.lastName = lastName
            updateUserDataPayload.mobile =
                if (sharedPref.user.mobileNumber == phoneNumber) sharedPref.user.enMobileNumber else phoneNumber
            updateUserDataPayload.profilePictureUrl =
                imageUrl ?: SharedPreferencesManager.getInstance().user.profilePictureUrl

            val updateUserDataRequestModel = UpdateUserDataRequestModel()
            updateUserDataRequestModel.payload = updateUserDataPayload

            //lifecycleScope
            activity?.lifecycleScope?.launchWhenStarted {
                try {
                    response = instance.updateProfile(updateUserDataRequestModel)
                } catch (exception: Exception) {
                    v.revertAnimation()
                    exception.message?.let { activity?.showToastMsg(it) }
                    logging(
                        activity!!,
                        Constants.USER_UPDATE_PROFILE_FAIL, LEVEL_ERROR,
                        "User failed to update his profile ", exception.message
                    )
                }
                //for ui handling
                withContext(Dispatchers.Main) {
                    v.revertAnimation()
                    if (response?.isSuccessful == true) {
                        logging(
                            activity!!,
                            Constants.USER_UPDATE_PROFILE_SUCCESS, LEVEL_INFO,
                            "User successfully update his profie ", ""
                        )
                        if (response?.body()?.payload?.code == 1000) {
                            activity?.showToastMsg(response?.body()?.payload?.message.toString())
                            navigateToVerification()
                        } else
                            activity?.showToastMsg(activity!!.getString(R.string.profile_updated_success))

                        user?.firstName = firstName
                        user?.lastName = lastName
                        if (!imageUrl.isNullOrEmpty()) user?.profilePictureUrl = imageUrl

                        sharedPref.user = user
                        activity?.intent?.let { activity?.setInentResult(it) }

                        activity?.finish()
                    } else {
                        response?.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_UPDATE_PROFILE_FAIL,
                                "User failed to update his profile "
                            )
                        }
                    }
                }
            }
        } else {
            activity?.showToastMsg(activity!!.getString(R.string.no_connection))
        }
    }

    private fun navigateToVerification() {
        val intent = Intent(
            activity,
            VerifyAccountActivity::class.java
        )
        intent.putExtra("phone", phoneNumber)
        intent.putExtra(Constants.IS_PROFILE_EDITING, true)
        intent.putExtra(Constants.IS_ORDERING, false)
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        activity?.startActivity(intent)
        activity!!.finish()
    }

    fun updateProfileClick(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            checkInputsValidation(v)
        }
    }

    private fun uploadFile(v: View) {

        var file: File? = File(uri?.path)
        if (file?.exists() == false) {
            file = FileUtil.getTempFile(activity!!, uri!!)
            if (file?.exists() == false) {
                activity?.showToastMsg(activity!!.getString(R.string.failed_image_invalid_path))
                logging(
                    QurbaApplication.getContext(),
                    Constants.USER_UPLOAD_IMAGE_FILE_NOT_EXIST,
                    LEVEL_ERROR, "Failed to upload image", "File not exist ${file.path}"
                )
                return
            }
        }

        (v as CircularProgressButton).startAnimation()
        //lifecycleScope
        viewModelScope.launch(IO) {
            val compressedImageFile =
                Compress.with(activity!!, file!!)
                    .automatic {
                        this.quality = 70
                        this.format = Bitmap.CompressFormat.WEBP
                    }
                    .get()


            val filName = if (isProbablyArabic(compressedImageFile.name)) "IMG_${
                java.util.Random().nextInt()
            }.webp" else compressedImageFile.name

            observer = getTransferUtility().upload(
                BuildConfig.COGNITO_BUCKET_NAME,  // The S3 bucket to upload to
                "user/profile/${filName}",  // The key for the uploaded object
                compressedImageFile // The location of the file to be uploaded
                , setMetaData()
            )
            observer?.setTransferListener(
                UploadListener(v, "user/profile/${filName}")
            )
        }
    }

    private fun setMetaData(): ObjectMetadata {
        val myObjectMetadata = ObjectMetadata()
        myObjectMetadata.contentType = "image/webp";
        return myObjectMetadata
    }

    inner class UploadListener(val view: View, private val fileName: String) : TransferListener {
        // Simply updates the UI list when notified.
        override fun onError(id: Int, e: java.lang.Exception) {
            (view as CircularProgressButton).revertAnimation()
            logging(
                QurbaApplication.getContext(),
                Constants.USER_UPLOAD_IMAGE_FAIL,
                LEVEL_ERROR, "Failed to upload image", e.localizedMessage
            )
            activity?.showToastMsg(activity!!.getString(R.string.failed_update_pp))
            Log.e("Constants", "Error during upload: $id", e)
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            Log.d(
                "TAG", String.format(
                    "onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent
                )
            )
        }

        override fun onStateChanged(id: Int, newState: TransferState) {
            Log.d("", "onStateChanged: $id, $newState")
            if (newState.ordinal == 4 && uri != null) {
                updateProfile(view, BuildConfig.COGNITO_URL + fileName)
                setUri(null)
                logging(
                    QurbaApplication.getContext(),
                    Constants.USER_UPLOAD_IMAGE_SUCCESS,
                    LEVEL_INFO, "User successfully uploading image", ""
                )
            } else if (newState.ordinal == 5 || newState.ordinal == 6) {
                (view as CircularProgressButton).revertAnimation()
                activity?.showToastMsg(activity!!.getString(R.string.failed_update_pp))
                logging(
                    QurbaApplication.getContext(),
                    Constants.USER_UPLOAD_IMAGE_FAIL,
                    LEVEL_ERROR, "Failed to upload image", newState.name
                )
            }
        }
    }
}
