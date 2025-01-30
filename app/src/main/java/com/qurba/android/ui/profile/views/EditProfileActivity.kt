package com.qurba.android.ui.profile.views

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUtil
import com.qurba.android.R
import com.qurba.android.databinding.ActivityEditProfileBinding
import com.qurba.android.network.CognitoClient
import com.qurba.android.ui.profile.view_models.EditProfileViewModel
import com.qurba.android.utils.BaseActivity
import com.qurba.android.utils.SharedPreferencesManager
import com.qurba.android.utils.SystemUtils.getRealPathFromURI
import com.qurba.android.utils.extenstions.changeStatusBarColor
import io.intercom.android.sdk.Intercom
import java.io.File

class EditProfileActivity : BaseActivity() {
    private var binding: ActivityEditProfileBinding? = null
    private var viewModel: EditProfileViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        initActivity()
        super.onCreate(savedInstanceState)
        initialization()
    }

    override fun onStart() {
        super.onStart()
        CognitoClient.startService()
    }

    override fun onStop() {
        super.onStop()
        CognitoClient.stopService()
    }

    private fun initActivity() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        changeStatusBarColor(R.color.white)
    }

    private fun initialization() {
        binding?.offerItemOrderBtn?.startAnimation()
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding?.viewModel = viewModel
        viewModel?.setActivity(this)

        Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE)
        Glide.with(this).load(SharedPreferencesManager.getInstance().user.profilePictureUrl)
            .into(binding!!.profileAvatarIv)
        viewModel?.getProfile(activity = this) {
            viewModel?.setUser()
            binding?.offerItemOrderBtn?.revertAnimation()
        }

        initListeners()
    }

    private fun initListeners() {
        binding!!.profileAvatarIv.setOnClickListener {

            ImagePicker.with(this)
                .galleryOnly()        //Final image size will be less than 1 MB(Optional)
                .cropSquare()
                .galleryMimeTypes(  //Exclude gif images
                    mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .createIntent { intent ->
                    binding?.offerItemOrderBtn?.startAnimation()
                    startForProfileImageResult.launch(intent)
                }
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            binding?.offerItemOrderBtn?.revertAnimation()

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                viewModel?.setUri(fileUri)
                binding?.profileAvatarIv?.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
        }
}