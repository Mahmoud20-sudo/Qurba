package com.qurba.android.ui.address_component.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.Gson
import com.mazenrashed.logdnaandroidclient.models.Line
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.qurba.android.R
import com.qurba.android.adapters.MyInfoWindowAdapter
import com.qurba.android.databinding.ActivityAddressBinding
import com.qurba.android.network.QurbaLogger
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.AddAddressModel
import com.qurba.android.ui.address_component.view_models.AddressViewModel
import com.qurba.android.ui.home.views.HomeActivityKotlin
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.setInentResult
import io.nlopez.smartlocation.SmartLocation
import kotlinx.coroutines.delay
import mehdi.sakout.fancybuttons.Utils
import org.json.JSONObject
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*

class AddressActivity : BaseActivity(), OnMapReadyCallback, PlaceSelectionListener,
    AreaSelectCallBack {

    private var cameraMoving: Boolean = false
    private lateinit var infoAdpater: MyInfoWindowAdapter
    private lateinit var binding: ActivityAddressBinding
    private var googleMap: GoogleMap? = null
    private var isFirstTime = true
    private lateinit var viewModel: AddressViewModel
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()
    private var deliveryResponse: AddAddressModel? = null
    private var myLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address)
        binding.mapView.onCreate(savedInstanceState)
        initialize()
    }

    private fun initialize() {
        viewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        viewModel.setActivity(this)
        binding.viewModel = viewModel

        infoAdpater = MyInfoWindowAdapter(context = this);
        MapsInitializer.initialize(applicationContext)
        initializeMap()
        initAutoCompleteSearch()
        initListeners()
        initializeObservables()
    }

    private fun initListeners() {
        binding.changeTv.setOnClickListener {
            val sheet = DeliveryAddressFragment()
            sheet.setAreaSelectCallBack(this)
            sheet.show(supportFragmentManager, "DeliveryAddressFragment")
        }

        binding.accessToLocationCv.setOnClickListener {
            if (!SystemUtils.isGPSActive(this)) {
                showLocationSettingsDialog()
                return@setOnClickListener
            }
            if (ContextCompat.checkSelfPermission(
                    QurbaApplication.getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    2
                )
                return@setOnClickListener
            }
            myLocation?.longitude?.let { it1 ->
                myLocation?.latitude?.let { it2 ->
                    LatLng(
                        it2,
                        it1
                    )
                }
            }?.let { it2 -> addMarkerToPosition(it2, 16f) }
//            myLocation?.latitude?.let { it1 -> myLocation?.longitude?.let { it2 -> LatLng(it1, it2) } }?.let { it2 -> getAddressData(it2) }
        }

        binding.confirmLocationBtn.setOnClickListener {
            intent.putExtra(Constants.ADDRESS, Gson().toJson(deliveryResponse))
            setInentResult(intent)
            sharedPreferencesManager.setAreaVoting(false)

            if (!isTaskRoot) {
                super.onBackPressed();//or finish()
            } else {
                val savedDeliveryAddressList: ArrayList<AddAddressModel> =
                    sharedPreferencesManager.savedDeliveryAddress
                if (!savedDeliveryAddressList.contains(deliveryResponse))
                    savedDeliveryAddressList.add(deliveryResponse!!)
                sharedPreferencesManager.setSavedDeliveryAddress(savedDeliveryAddressList)
                sharedPreferencesManager.selectedCachedArea = deliveryResponse

                if (intent.extras?.get(Constants.BRANCH_OBJECT) != null) {
                    val routerController = RoutingController(this)
                    routerController.setReferringParams(
                        JSONObject(
                            intent.extras?.getString(
                                Constants.BRANCH_OBJECT
                            )
                        )
                    )
                    routerController.routeApp()
                } else {
                    val intent = Intent(this, HomeActivityKotlin::class.java)
                    startActivity(intent)
                }

                finish()
            }

            QurbaLogger.logging(
                application.applicationContext,
                Constants.USER_ADDRESS_COMPONENT_CONFIRM_ADDRESS_SUCCESS, Line.LEVEL_INFO,
                "User successfully confirming address", ""
            )
        }
    }

    private fun initializeObservables() {
        viewModel.getDeliveryResponse()?.observe(
            this,
            androidx.lifecycle.Observer<AddAddressModel> { deiverResponse: AddAddressModel? ->
                deiverResponse?.let { this.setMarkerData(it) }
            })
    }

    private fun initAutoCompleteSearch() {
        // Initialize the AutocompleteSupportFragment.
        // Initialize place API
        if (!Places.isInitialized())
            Places.initialize(applicationContext, getString(R.string.google_places_api_key), Locale("ar"))


        val autocompleteFragment: AutocompleteSupportFragment? =
            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment)
                    as AutocompleteSupportFragment?

        // Specify the types of place data to return.

        // Specify the types of place data to return.
        autocompleteFragment?.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment?.setCountry("EG");
        autocompleteFragment?.setHint(getString(R.string.search_deliver_area))
        val searchIcon: ImageView =
            (autocompleteFragment?.view as LinearLayout).getChildAt(0) as ImageView
        searchIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_search_red_24dp, null))
        (autocompleteFragment?.view?.findViewById(R.id.places_autocomplete_search_input) as EditText).textSize =
            Utils.spToPx(this, 5.3f).toFloat()
        (autocompleteFragment?.view?.findViewById(R.id.places_autocomplete_search_input) as EditText).setTextColor(
            Color.parseColor("#6d7278")
        )
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment?.setOnPlaceSelectedListener(this)

        autocompleteFragment.requireView().findViewById<View>(R.id.places_autocomplete_clear_button)
            .setOnClickListener {   // example : way to access view from PlaceAutoCompleteFragment
                // ((EditText) autocompleteFragment.getView()
                // .findViewById(R.id.place_autocomplete_search_input)).setText("");
                autocompleteFragment.setText("")
                getLocation()

                QurbaLogger.logging(
                    application.applicationContext,
                    Constants.USER_ADDRESS_COMPONENT_SEARCH_SUCCESS, Line.LEVEL_INFO,
                    "User successfully searching on map", ""
                )
            }
    }

//    private fun getAddressData(latLng: LatLng) {
//        Thread {
//            try {
//                val geocoder = Geocoder(applicationContext, Locale.getDefault())
//                val addresses = geocoder?.getFromLocation(latLng.latitude, latLng.longitude, 1)
//                val cityName = addresses[0]?.adminArea?.split("Governorate")?.get(0)
//
//                runOnUiThread {
//                    binding.deliveryAreaTv.visibility = View.VISIBLE
//                    binding.shimmerLayout.stopShimmer()
//                    binding.shimmerLayout.visibility = View.GONE
//                    binding.deliveryAreaTv.text = "$cityName, ${deliveryResponse?.area?.name?.en ?: ""} "
//                }
//            } catch (e: IOException) {
//            }
//
//        }.start()
//    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        SmartLocation.with(QurbaApplication.getContext()).location()
            .oneFix()
            .start { location: Location ->
                this.myLocation = location
                this.googleMap?.isMyLocationEnabled = true
                binding.accessToLocationCv.visibility = View.GONE
                addMarkerToPosition(LatLng(location.latitude, location.longitude), 16f)
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    cameraMoving = false;
                    googleMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                location.latitude,
                                location.longitude
                            ), 13f
                        )
                    )
                }, 0)
            }
    }

    private fun initializeMap() {
        binding.mapView.onResume() // needed to get the map to display immediately
        MapsInitializer.initialize(applicationContext)
        binding.mapView.getMapAsync(this)
    }

    private fun addMarkerToPosition(latLng: LatLng?, zoom: Float, isDragging: Boolean = false) {
        //val options = MarkerOptions()
        // options.position(latLng)
        //options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
        binding.infoWindowFl.visibility = View.GONE

        if (googleMap != null) {
            //googleMap?.clear()
            // locationMarker = googleMap?.addMarker(options)

//            if (!isDragging) {
//                val cameraPosition = CameraPosition.Builder().target(latLng).zoom(zoom).build()
//                googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//            }

            binding.deliveryAreaLl.visibility = View.VISIBLE
            if (isFirstTime) {
                binding.infoWindowFl.visibility = View.VISIBLE
//                locationMarker.showInfoWindow()
                isFirstTime = false
            }
        }

        binding.deliveryAreaTv.visibility = View.GONE
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.visibility = View.VISIBLE

        try {
            viewModel.getNearestAddress(latLng)
        } catch (e: Exception) {
            logging(
                this,
                Constants.GET_NEAREST_AREAS_CRASH, LEVEL_ERROR,
                "Failed to get nearest areas ", e.stackTraceToString()
            )
            //toast
        }
    }

    private fun setMarkerData(deliveryResponse: AddAddressModel) {
        this.deliveryResponse = deliveryResponse
        deliveryResponse.area?.location?.let { LatLng(it.coordinates[1]!!, it.coordinates[0]!!) }
            ?.let {
                //getAddressData(latLng = it)
            }

        binding.deliveryAreaTv.visibility = View.VISIBLE
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.deliveryAreaTv.text =
            "${deliveryResponse?.city?.name?.en}, ${deliveryResponse?.area?.name?.en ?: ""} "

        binding.confirmLocationBtn.revertAnimation()
        binding.confirmLocationBtn.setBackgroundResource(R.drawable.red_rect)
        binding.confirmLocationBtn.isEnabled = true

        if (deliveryResponse?.case > 1)
            Handler(Looper.getMainLooper()).postAtTime({
//                infoAdpater.getInfoWindow(locationMarker)?.findViewById<TextView>(R.id.info_title_tv)?.text =
//                        getString(R.string.not_supported_area_hint)
//                locationMarker.hideInfoWindow();
//                locationMarker.showInfoWindow();
                binding.infoWindowFl.visibility = View.VISIBLE
                binding.infoWindowFl.findViewById<TextView>(R.id.text).text =
                    getString(R.string.not_supported_area_hint)
            }, 0)
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2 -> {
                if (grantResults != null && (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    binding.confirmLocationBtn.startAnimation()
                    getLocation()

                    QurbaLogger.logging(
                        application.applicationContext,
                        Constants.USER_QURBA_ACCESS_LOCATION_ALLOW_SUCCESS, Line.LEVEL_INFO,
                        "User allowing location access", ""
                    )

                } else {
                    addMarkerToPosition(LatLng(31.2001, 29.9187), 10f)
                    binding.deliveryAreaTv.text = getString(R.string.default_city)
//                    getAddressData(LatLng(31.2001, 29.9187))

                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        cameraMoving = false;
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    31.2001,
                                    29.9187
                                ), 13f
                            )
                        )
                    }, 20)

                    QurbaLogger.logging(
                        application.applicationContext,
                        Constants.USER_QURBA_ACCESS_LOCATION_DENY_SUCCESS, Line.LEVEL_INFO,
                        "User denying location access", ""
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            111 -> ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                2
            )
        }
    }

    override fun onPlaceSelected(place: Place) {
        Log.i("", "An error occurred: " + place.getName() + "," + place.getId())
        place.latLng?.let {
            addMarkerToPosition(it, 16f)

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                cameraMoving = false;
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 13f))
            }, 0)

//            getAddressData(LatLng(it.latitude, it.longitude))
        }
    }

    override fun onError(status: Status) {
        Log.i("", "An error occurred: $status")
    }

    override fun onAreaSelect(addAddressModel: AddAddressModel?) {
        deliveryResponse = addAddressModel

        binding.deliveryAreaTv.visibility = View.VISIBLE
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE

        binding.deliveryAreaTv.text =
            addAddressModel?.city?.name?.en + ", " + addAddressModel?.area?.name?.en
        addAddressModel?.area?.location?.let {
            LatLng(it.coordinates[0]!!, it.coordinates[1]!!)
        }?.let {
            addMarkerToPosition(
                latLng = LatLng(
                    addAddressModel?.area?.location!!.coordinates[1]!!,
                    addAddressModel?.area?.location!!.coordinates[0]!!
                ), zoom = 16f
            )
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.longitude!!,
                        it.latitude
                    ), 16f
                )
            )
        }
    }

    fun showLocationSettingsDialog() {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.gps_network_not_enabled)
            builder.setPositiveButton(
                R.string.open_location_settings
            ) { dialog, arg1 ->
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    111
                )
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.not_open_settings, null)
            builder.setCancelable(true)
            val dialog = builder.create()
            dialog.show()
        } catch (e: Exception) {
            Log.e(packageName, e.message!!)
        }
    }

    override fun onMapReady(mMap: GoogleMap) {
        googleMap = mMap
        googleMap?.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                applicationContext,
                R.raw.uber_style
            )
        )
        googleMap?.setInfoWindowAdapter(infoAdpater)

        when {
            SystemUtils.isGPSActive(this) -> ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                2
            )
            ContextCompat.checkSelfPermission(
                QurbaApplication.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && SystemUtils.isGPSActive(this) -> {
                getLocation()
                binding.confirmLocationBtn.isEnabled = false
                binding.confirmLocationBtn.startAnimation()
            }
            else -> {
                addMarkerToPosition(LatLng(31.2001, 29.9187), 13f)
                binding.deliveryAreaTv.text = getString(R.string.default_city)
//                getAddressData(LatLng(31.2001, 29.9187))//31.2001° N, 29.9187
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    cameraMoving = false;
                    googleMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                31.2001,
                                29.9187
                            ), 13f
                        )
                    )
                }, 0)
            }
        }

        googleMap?.setOnCameraIdleListener { //get latlng at the center by calling
            val midLatLng: LatLng? = googleMap?.cameraPosition?.target
            if (!cameraMoving) {
                cameraMoving = true;
                return@setOnCameraIdleListener
            }
            if (midLatLng?.latitude != 0.0) {
                addMarkerToPosition(midLatLng, 16f)
//                getAddressData(LatLng(midLatLng.latitude, midLatLng.longitude))
                QurbaLogger.logging(
                    application.applicationContext,
                    Constants.USER_ADDRESS_COMPONENT_CHANGE_ADDRESS_SUCCESS, Line.LEVEL_INFO,
                    "User successfully changing address", ""
                )
            }
        }
    }

}