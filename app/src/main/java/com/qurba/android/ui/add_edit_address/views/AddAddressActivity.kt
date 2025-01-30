package com.qurba.android.ui.add_edit_address.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.qurba.android.R
import com.qurba.android.databinding.ActivityAddAddressBinding
import com.qurba.android.network.models.AddAddressModel
import com.qurba.android.network.models.LocationModel
import com.qurba.android.network.models.UserDataModel
import com.qurba.android.network.models.response_models.DeliveryValidationPayload
import com.qurba.android.ui.add_edit_address.view_models.AddAddressViewModel
import com.qurba.android.ui.address_component.views.AddressActivity
import com.qurba.android.ui.checkout.views.CheckoutActivity
import com.qurba.android.ui.home.views.CachedDeliveryAreasFragment
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.setInentResult
import com.qurba.android.utils.extenstions.shortToast

class AddAddressActivity : BaseActivity(), OnMapReadyCallback, SelectAddressCallBack {

    private var isHaveCart: Boolean = false
    private var newAddressModel: AddAddressModel? = null
    private var addressModel: AddAddressModel? = null
    private var usetModel: UserDataModel? = null
    private lateinit var viewModel: AddAddressViewModel
    private lateinit var binding: ActivityAddAddressBinding
    private var googleMap: GoogleMap? = null
    private val mMapView: SupportMapFragment? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var placeId: String? = null;
    private var selectAddressCallBack: SelectAddressCallBack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_address)
        viewModel = ViewModelProvider(this).get(AddAddressViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.setActivity(this)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.mapView.onCreate(savedInstanceState)
            binding.mapView.onResume()
        }, 100)

        initialize()
    }

    private fun initialize() {
        binding.toolbar.navigationIcon!!.setTint(Color.BLACK)
        usetModel = sharedPref.user

        addressModel = if (intent.extras?.get(Constants.ADDRESS) != null) {
            viewModel.isEditingAddress = true
            Gson().fromJson(intent.getStringExtra(Constants.ADDRESS), AddAddressModel::class.java)
        } else
            sharedPref.selectedCachedArea

        //you editing address from checkout page
        if (intent?.extras?.getString(Constants.PLACE_ID) != null)
            placeId = intent?.extras?.getString(Constants.PLACE_ID)

        //you editing address from home and have a cart on the same address
        else if (sharedPref?.cart != null && sharedPref.selectedCachedArea.area?._id == addressModel?.area?._id) {
            isHaveCart = true
        }

        binding.itemObject = addressModel
        addressModel?.let { viewModel.setSlectedAddress(it) }
        viewModel.isFromCheckout = intent.extras?.get(Constants.IS_ORDERING) as Boolean? ?: false

        binding.mapView.getMapAsync { mMap ->
            googleMap = mMap
            googleMap!!.uiSettings.isScrollGesturesEnabled = false
            googleMap!!.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = false
            googleMap!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    applicationContext,
                    R.raw.uber_style
                )
            )
            updatePinLocation()

            googleMap!!.setOnMapClickListener {
                val intent = Intent(QurbaApplication.getContext(), AddressActivity::class.java)
                startActivityForResult(intent, 1171)
            }
        }

        initListeners()
        initializeObservables()
        checkIFLabelSelectedBefore()
    }

    //prevent label duplication
    private fun checkIFLabelSelectedBefore() {
        if (usetModel != null)
            for (address in usetModel!!.deliveryAddresses) {
                if (addressModel?.getId() == address.getId()) {
                    if (addressModel?.label.equals("home", true))
                        setHomeLbelSelcted()
                    else if (addressModel?.label.equals("work", true))
                        setWorkbelSelcted()
                    else
                        setOtherlSelcted()
                } else {
                    if (address.label.equals("home", true))
                        setHomeDimmed()
                    else if (address.label.equals("work", true))
                        setWorkDimmed()
                }
            }
    }

    private fun initializeObservables() {
        viewModel.observable?.observe(this, Observer { addAddressModel: AddAddressModel ->
            this.publishAddress(addAddressModel)
        })
        viewModel.deleteObservable?.observe(this, Observer { addAddressModel: AddAddressModel ->
            this.deleteAddress(addAddressModel)
        })
        viewModel.deliveryAreaObservable?.observe(
            this,
            Observer { deliveryAreaPayload: DeliveryValidationPayload? ->
                this.updateAddress(deliveryAreaPayload)
            })
    }

    private fun updateAddress(deliveryAreaPayload: DeliveryValidationPayload?) {
        if (deliveryAreaPayload == null) {
            //not supported
            viewModel?.voteArea(newAddressModel)
            showUnSupportedAreaDialog(
                {
                    val savedDeliveryAddressList: ArrayList<AddAddressModel> =
                        sharedPref.savedDeliveryAddress
                    if (savedDeliveryAddressList.contains(addressModel))
                        savedDeliveryAddressList.remove(addressModel)

                    savedDeliveryAddressList.add(addressModel!!)
                    sharedPref.setSavedDeliveryAddress(savedDeliveryAddressList)
                    sharedPref.selectedCachedArea = addressModel
                },
                sharedPref?.cart?.placeModel?.name?.en + " " + getString(R.string.not_deliver_hint) + " " + newAddressModel?.area?.name?.en
            )

        } else {
            if (this.addressModel == null) {
                this.addressModel = newAddressModel
            } else {
                this.addressModel?.area = newAddressModel?.area
                this.addressModel?.city = newAddressModel?.city
                this.addressModel?.country = newAddressModel?.country
                newAddressModel?.setId(this.addressModel?.getId() ?: newAddressModel?.getId())
                //update user address selection during flow
                newAddressModel?.let { viewModel.setSlectedAddress(it) }
                clearLabelsSections()
                checkIFLabelSelectedBefore()
                when {
                    newAddressModel?.label.equals("home", true) -> setHomeLbelSelcted()
                    newAddressModel?.label.equals("work", true) -> setWorkbelSelcted()
                    newAddressModel?.label != null -> {
                        setOtherlSelcted()
                    }
                }
            }
            updatePinLocation()
        }
    }

    private fun deleteAddress(addAddressModel: AddAddressModel) {
        val userDataModel = sharedPref.user
        val userSavedAddresses = userDataModel.deliveryAddresses
        val itemList = sharedPref.savedDeliveryAddress
        userSavedAddresses.remove(addAddressModel)
        userDataModel.deliveryAddresses = userSavedAddresses
        sharedPref.user = userDataModel

        shortToast(getString(R.string.address_deleted))

        if (userSavedAddresses.isEmpty() && itemList.isEmpty()) {
            SystemUtils.restartApp(this)
        } else if (addAddressModel.area?.location != null && (sharedPref.selectedCachedArea.area?.location?.coordinates.toString()
                    == addAddressModel.area?.location?.coordinates.toString()) && itemList.isEmpty()
        ) {
            sharedPref.selectedCachedArea = userSavedAddresses[userSavedAddresses.size - 1]
//            selectAddressCallBack?.onSelect(userSavedAddresses[userSavedAddresses.size - 1])
            intent.putExtra(
                Constants.ADDRESS,
                Gson().toJson(userSavedAddresses[userSavedAddresses.size - 1])
            )

            sharedPref.setSavedDeliveryAddress(userSavedAddresses)
        } else if (itemList.isEmpty()) {
            sharedPref.selectedCachedArea = userSavedAddresses[userSavedAddresses.size - 1]
//            selectAddressCallBack?.onSelect(userSavedAddresses[userSavedAddresses.size - 1])
            intent.putExtra(
                Constants.ADDRESS,
                Gson().toJson(userSavedAddresses[userSavedAddresses.size - 1])
            )

            sharedPref.setSavedDeliveryAddress(itemList)
        } else {
            sharedPref.selectedCachedArea = itemList[itemList.size - 1]
//            selectAddressCallBack?.onSelect(itemList[itemList.size - 1])
            intent.putExtra(Constants.ADDRESS, Gson().toJson(itemList[itemList.size - 1]))

            sharedPref.setSavedDeliveryAddress(itemList)
        }

        setInentResult(intent)
        onBackPressed()
    }

    private fun publishAddress(addAddressModel: AddAddressModel) {
        try {
            val adddresesList = usetModel?.deliveryAddresses
            addAddressModel.case = 1
            if (adddresesList?.contains(addAddressModel) == false)
                adddresesList?.add(addAddressModel)
            else
                adddresesList?.set(adddresesList.indexOf(addAddressModel), addAddressModel)

            val savedAreasList = sharedPref.savedDeliveryAddress
            savedAreasList.remove(addAddressModel)
            sharedPref.setSavedDeliveryAddress(savedAreasList)

            sharedPref.selectedCachedArea = addAddressModel
            usetModel?.deliveryAddresses = adddresesList
            sharedPref.user = usetModel
//        selectAddressCallBack?.onSelect(addAddressModel)

            intent.putExtra(Constants.ADDRESS, Gson().toJson(addAddressModel))
            setInentResult(intent)

            shortToast(
                if (viewModel.isEditingAddress) getString(R.string.address_updated) else getString(
                    R.string.address_added
                )
            )

            //update only
            if (!viewModel.isEditingAddress) {
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                sharedPref.setOrdering(true)
                startActivity(intent)
                finish()
            }

            if (!placeId.isNullOrEmpty())//update cart area
                viewModel.updateCartArea(this, addAddressModel?.area?._id.toString(), callback = {
                    if (it == true)
                        onBackPressed()
                })
            else
                onBackPressed()
        } catch (e: Exception){
            e.message?.let { Log.e(packageName, it) }
        }
    }

    private fun updatePinLocation() {
        val location: LocationModel? = viewModel.address?.area?.location
            ?: sharedPref.selectedCachedArea.area?.location
        location?.coordinates?.let { LatLng(it[1]!!, it[0]!!) }
            ?.let { moveMapToCurrentLocation(it) }
        this.addressModel?.let { viewModel.setAreaTitle(it) }
    }

    private fun showAddressSheet() {
        val sheet = CachedDeliveryAreasFragment()
        sheet.isWithinOrderingFlow = true
        sheet.setSelectAddressCallBack(this)
        sheet.show(supportFragmentManager, "CachedDeliveryAreasFragment")
    }

    private fun initListeners() {
        binding.toolbar.setNavigationOnClickListener { view -> onBackPressed() }

        binding.changeTv.setOnClickListener {
            if (viewModel.isEditingAddress) {
                val intent = Intent(QurbaApplication.getContext(), AddressActivity::class.java)
                startActivityForResult(intent, 1171)
            } else
                showAddressSheet()
        }

        binding.mapView.setOnClickListener {
        }

        binding.otherLbl.setOnClickListener {
            binding.homeLbl.visibility = View.GONE
            binding.workLbl.visibility = View.GONE
            binding.othersInput.visibility = View.VISIBLE
            binding.otherCloseIv.visibility = View.VISIBLE

            clearLabelsSections()
            setOtherlSelcted()
        }

        binding.otherCloseIv.setOnClickListener {
            binding.homeLbl.visibility = View.VISIBLE
            binding.workLbl.visibility = View.VISIBLE
            binding.othersInput.visibility = View.GONE
            binding.otherCloseIv.visibility = View.GONE

            if (!viewModel.othersTitle.get().isNullOrEmpty()) {
                viewModel.setLabel(viewModel.othersTitle.get().toString())
                binding.otherLbl.setText(viewModel.othersTitle.get().toString())
            }
        }

        binding.workLbl.setOnClickListener {
            clearLabelsSections()
            setWorkbelSelcted()
        }

        binding.homeLbl.setOnClickListener {
            clearLabelsSections()
            setHomeLbelSelcted()
        }
    }

    private fun siwpVisibility() {
        binding.homeLbl.visibility = View.GONE
        binding.workLbl.visibility = View.GONE
        binding.othersInput.visibility = View.VISIBLE
        binding.otherCloseIv.visibility = View.VISIBLE
    }

    private fun setOtherlSelcted() {
        viewModel.setLabel("Other")
        siwpVisibility()

        viewModel.othersTitle.set(addressModel?.label)
        binding.otherLbl.isEnabled = true
        binding.otherLbl.alpha = 1.0f
        binding.otherLbl.setBackgroundColor(resources.getColor(R.color.order_color))
        binding.otherLbl.setTextColor(Color.parseColor("#FFFFFF"))
        binding.otherLbl.iconImageObject.setColorFilter(
            resources.getColor(R.color.white),
            android.graphics.PorterDuff.Mode.SRC_ATOP
        )
    }

    private fun setHomeDimmed() {
        binding.homeLbl.isEnabled = false
        binding.homeLbl.alpha = 0.6f
        binding.homeLbl.setBackgroundColor(Color.parseColor("#808f9bb3"))
        binding.homeLbl.setTextColor(Color.parseColor("#808f9bb3"))
        binding.homeLbl.iconImageObject.setColorFilter(
            Color.parseColor("#8f9bb3"),
            android.graphics.PorterDuff.Mode.SRC_ATOP
        )
    }

    private fun setWorkDimmed() {
        binding.workLbl.isEnabled = false
        binding.workLbl.alpha = 0.6f
        binding.workLbl.setBackgroundColor(Color.parseColor("#808f9bb3"))
        binding.workLbl.setTextColor(Color.parseColor("#808f9bb3"))
        binding.workLbl.iconImageObject.setColorFilter(
            Color.parseColor("#8f9bb3"),
            android.graphics.PorterDuff.Mode.SRC_ATOP
        )
    }

    private fun setWorkbelSelcted() {
        viewModel.setLabel("Work")

        binding.workLbl.isEnabled = true
        binding.workLbl.alpha = 1.0f
        binding.workLbl.setBackgroundColor(resources.getColor(R.color.order_color))
        binding.workLbl.setTextColor(Color.parseColor("#FFFFFF"))
        binding.workLbl.iconImageObject.setColorFilter(
            resources.getColor(R.color.white),
            android.graphics.PorterDuff.Mode.SRC_ATOP
        )
    }

    private fun setHomeLbelSelcted() {
        viewModel.setLabel("Home")

        binding.homeLbl.isEnabled = true
        binding.homeLbl.alpha = 1.0f
        binding.homeLbl.setBackgroundColor(resources.getColor(R.color.order_color))
        binding.homeLbl.setTextColor(Color.parseColor("#FFFFFF"))
        binding.homeLbl.iconImageObject.setColorFilter(
            resources.getColor(R.color.white),
            android.graphics.PorterDuff.Mode.SRC_ATOP
        )
    }

    private fun clearLabelsSections() {
        binding.homeLbl.setTextColor(resources.getColor(R.color.lbl_ddefault_color))
        binding.workLbl.setTextColor(resources.getColor(R.color.lbl_ddefault_color))
        binding.otherLbl.setTextColor(resources.getColor(R.color.lbl_ddefault_color))

        binding.homeLbl.iconImageObject.clearColorFilter()
        binding.workLbl.iconImageObject.clearColorFilter()
        binding.otherLbl.iconImageObject.clearColorFilter()

        binding.homeLbl.setBackgroundColor(Color.WHITE)
        binding.workLbl.setBackgroundColor(Color.WHITE)
        binding.otherLbl.setBackgroundColor(Color.WHITE)
    }

    private fun moveMapToCurrentLocation(latLng: LatLng) {
        val options = MarkerOptions();
        options.position(latLng);
        if (googleMap != null) {
            googleMap!!.clear()
            val icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_icon)
            options.icon(icon)
            googleMap!!.addMarker(options);
            val cameraPosition = CameraPosition.Builder().target(latLng).zoom(16f).build();
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    override fun onResume() {
        super.onResume()

        if (googleMap != null) {
            binding.mapView.onResume()
            Handler(Looper.getMainLooper()).postDelayed({ onMapReady(googleMap!!) }, 2000)
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding.mapView != null)
            binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding.mapView != null)
            binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (binding.mapView != null)
            binding.mapView.onLowMemory()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1171 || resultCode == -1 && data != null) {
            newAddressModel = Gson().fromJson(
                data?.extras?.getString(Constants.ADDRESS),
                AddAddressModel::class.java
            )

            if (data?.extras?.getString(Constants.ADDRESS) == null || data?.extras?.getString(
                    Constants.ADDRESS
                ).equals("null")
            )
                return

            when {
                //you have a cart
                placeId != null -> {
                    if (newAddressModel?.area?._id == null) {
                        updateAddress(null)
                    } else
                        viewModel.checkIfNotDelivering(placeId, newAddressModel?.area?._id)
                }
                isHaveCart -> {
                    showClearCartDialog(sharedPref?.cart?.placeModel?.name?.en, ClearCartCallback {
//                        clearCart()
                        updateCachedAddress()
                    })
                }
                else -> {
                    updateCachedAddress()
                }
            }
        }
    }

    private fun updateCachedAddress() {
        //case of just add/edit address out of ordering flow
        if (this.addressModel == null) {
            this.addressModel = newAddressModel
        } else {
            this.addressModel?.area = newAddressModel?.area
            this.addressModel?.city = newAddressModel?.city
            this.addressModel?.country = newAddressModel?.country
        }
        updatePinLocation()
    }

    override fun onSelect(deliveryResponse: AddAddressModel?) {
        newAddressModel = deliveryResponse
        viewModel.checkIfNotDelivering(
            sharedPref?.cart?.id,
            deliveryResponse?.area?._id
        )
    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }
}
