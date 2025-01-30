package com.qurba.android.ui.place_details.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qurba.android.R;
import com.qurba.android.adapters.FacilitiesAdapter;
import com.qurba.android.adapters.GalleryAdapter;
import com.qurba.android.adapters.OpeningTimesAdapter;
import com.qurba.android.adapters.OtherBranchesAdapter;
import com.qurba.android.databinding.FragmentInfoBinding;
import com.qurba.android.network.models.DayModel;
import com.qurba.android.network.models.PlaceDetailsModel;

import com.qurba.android.ui.place_details.view_models.InfoViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.SharedPreferencesManager;
import com.qurba.android.utils.ViewUtils;

import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.qurba.android.utils.SystemUtils.getWeekDaysIndex;

public class InfoFragment extends Fragment implements OnMapReadyCallback {

    private InfoViewModel viewModel;
    private FragmentInfoBinding binding;
    private GoogleMap googleMap;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        googleMap.clear();
        context = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info, container, false);
        viewModel = new ViewModelProvider(this).get(InfoViewModel.class);
        binding.setViewModel(viewModel);
        viewModel.setActivity((BaseActivity) context);

        initializeObservables();
        viewModel.getPlaceDetails(getArguments().getString(Constants.PLACE_ID));

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.nearbyMapView.onCreate(savedInstanceState);
            binding.nearbyMapView.onResume();
        }, 100);

        binding.nearbyMapView.getMapAsync(mMap -> {
            googleMap = mMap;
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context.getApplicationContext()
                    , R.raw.uber_style));
        });

        MapsInitializer.initialize(context.getApplicationContext());

        return binding.getRoot();
    }

    private void initializeObservables() {
        viewModel.getDetailsObservable().observe(getViewLifecycleOwner(), this::updatePlaceDetails);
    }

    private void updatePlaceDetails(PlaceDetailsModel placeDetailsModel) {
        if (context == null) return;
        setFacilities(placeDetailsModel);
        setOtherBrances(placeDetailsModel);
        setGarageData(placeDetailsModel);
        setLandmarksData(placeDetailsModel);
        setGalleryData(placeDetailsModel);
        setProductsAndServicesData(placeDetailsModel);

        LatLng latLng = new LatLng(placeDetailsModel.getLocation().getCoordinates().get(1),
                placeDetailsModel.getLocation().getCoordinates().get(0));
        moveMapToCurrentLocation(latLng);

        if (placeDetailsModel.getOpeningTimes() != null && !placeDetailsModel.getOpeningTimes().isEmpty()) {
            setOpeningTime(placeDetailsModel);
        }

        if (!placeDetailsModel.isOpenNow()) {
            binding.placeOpenTimeTv.setTextColor(Color.parseColor("#9b9ba2"));
//            binding.placeOpenTimeTv.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            binding.placeOpenTimeTv.setCompoundDrawablesWithIntrinsicBounds(
                    sharedPref.getLanguage().equalsIgnoreCase("ar") ? null : getContext().getResources().getDrawable(R.drawable.ic_closed),
                    null,
                    sharedPref.getLanguage().equalsIgnoreCase("en") ? null : getContext().getResources().getDrawable(R.drawable.ic_closed),
                    null);
        }
    }

    private void setGalleryData(PlaceDetailsModel placeDetailsModel) {
        if (placeDetailsModel.getPlaceGalleryPicturesUrls() != null || !placeDetailsModel.getPlaceGalleryPicturesUrls().isEmpty()) {

            binding.galleryRv.setHasFixedSize(true);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
            binding.galleryRv.setLayoutManager(staggeredGridLayoutManager);
            GalleryAdapter rcAdapter = new GalleryAdapter((BaseActivity) context);
            rcAdapter.setData(placeDetailsModel.getPlaceGalleryPicturesUrls().size() > 6
                    ? placeDetailsModel.getPlaceGalleryPicturesUrls().subList(0, 6) : placeDetailsModel.getPlaceGalleryPicturesUrls());

            binding.galleryRv.setAdapter(rcAdapter);
            binding.galleryRv.setItemAnimator(null);

            binding.viewAllGalleryTv.setOnClickListener(view -> {
                SharedPreferencesManager.getInstance().setImages(placeDetailsModel.getPlaceGalleryPicturesUrls());
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }
    }

    private void setProductsAndServicesData(PlaceDetailsModel placeDetailsModel) {
//        if (placeDetailsModel.getProductAndServiceUrls() != null) {
//
//            binding.producrSetvicesRv.setHasFixedSize(true);
//            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
//            binding.producrSetvicesRv.setLayoutManager(staggeredGridLayoutManager);
//            GalleryAdapter rcAdapter = new GalleryAdapter(getActivity());
//            rcAdapter.setData(placeDetailsModel.getProductAndServiceUrls().size() > 6
//                    ? placeDetailsModel.getProductAndServiceUrls().subList(0, 6) : placeDetailsModel.getProductAndServiceUrls());
//
//            binding.producrSetvicesRv.setAdapter(rcAdapter);
//            binding.producrSetvicesRv.setItemAnimator(null);
//
//            binding.productsViewAllTv.setOnClickListener(view -> {
//                SharedPreferencesManager.getInstance().setImages(placeDetailsModel.getProductAndServiceUrls());
//                Intent intent = new Intent(requireActivity(), ProductsServicesActivity.class);
//                intent.putExtra("is-products", true);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
//            });
//        }
    }

    public static InfoFragment getInstance() {
        return new InfoFragment();
    }

    private void setGarageData(PlaceDetailsModel placeDetailsModel) {
        if (placeDetailsModel.getAddress().getNearestParking() != null && !placeDetailsModel.getAddress().getNearestParking().isEmpty()) {
            LayoutInflater linf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (int i = 0; i < placeDetailsModel.getAddress().getNearestParking().size(); i++) {

                final View v = linf.inflate(R.layout.item_place_garage, null);
                TextView textView = v.findViewById(R.id.title_tv);
                textView.setText(placeDetailsModel.getAddress().getNearestParking().get(i).getEn());

                binding.placeGaragesLl.addView(v);

            }
        }
    }

    private void setLandmarksData(PlaceDetailsModel placeDetailsModel) {
        if (placeDetailsModel.getAddress().getNearestLandmark() != null && !placeDetailsModel.getAddress().getNearestLandmark().isEmpty()) {
            LayoutInflater linf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (int i = 0; i < placeDetailsModel.getAddress().getNearestLandmark().size(); i++) {

                final View v = linf.inflate(R.layout.item_place_garage, null);
                TextView textView = v.findViewById(R.id.title_tv);
                textView.setText(placeDetailsModel.getAddress().getNearestLandmark().get(i).getEn());

                binding.placeLandmarksLl.addView(v);

            }
        }
    }

    //
    private void setFacilities(PlaceDetailsModel placeDetailsModel) {
        if (placeDetailsModel.getFacilities() != null || !placeDetailsModel.getFacilities().isEmpty()) {
            if (placeDetailsModel.getFacilities().size() > 0) {
                binding.facilitiesCv.setVisibility(View.VISIBLE);
                FacilitiesAdapter adapter = new FacilitiesAdapter((BaseActivity) context, placeDetailsModel.getFacilities());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                binding.facilitiesRv.setHasFixedSize(true);
                binding.facilitiesRv.setLayoutManager(linearLayoutManager);
                binding.facilitiesRv.setAdapter(adapter);
                binding.facilitiesRv.setNestedScrollingEnabled(false);
            }
        }
    }

    private void setOtherBrances(PlaceDetailsModel placeDetailsModel) {
        if (placeDetailsModel.getBranches() != null) {
            binding.otherBranchesRv.setVisibility(View.VISIBLE);
            OtherBranchesAdapter adapter = new OtherBranchesAdapter((BaseActivity) context, placeDetailsModel.getBranches());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            binding.otherBranchesRv.setHasFixedSize(true);
            binding.otherBranchesRv.setLayoutManager(linearLayoutManager);
            binding.otherBranchesRv.setAdapter(adapter);
            binding.otherBranchesRv.setNestedScrollingEnabled(false);
        }
    }

    private void setOpeningTime(PlaceDetailsModel placeDetailsModel) {
        OpeningTimesAdapter adapter = new OpeningTimesAdapter(context, sortOpeningTimes(placeDetailsModel));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.daysRv.setHasFixedSize(true);
        binding.daysRv.setLayoutManager(linearLayoutManager);
        binding.daysRv.setAdapter(adapter);
        binding.daysRv.setNestedScrollingEnabled(false);

        binding.placeOpenTimeView.setOnClickListener(v -> {
            if (binding.expandableLayout.isCollapsed()) {
                if (sharedPref.getLanguage().equalsIgnoreCase("ar")) {
                    binding.placeOpenTimeTv.setCompoundDrawablesWithIntrinsicBounds(
                            getContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_red_24dp),
                            null,
                            placeDetailsModel.isOpenNow() ?
                                    getContext().getResources().getDrawable(R.drawable.ic_access_time_black_24dp)
                                    : getContext().getResources().getDrawable(R.drawable.ic_closed)
                            , null);
                } else {
                    binding.placeOpenTimeTv.setCompoundDrawablesWithIntrinsicBounds(placeDetailsModel.isOpenNow() ?
                                    getContext().getResources().getDrawable(R.drawable.ic_access_time_black_24dp)
                                    : getContext().getResources().getDrawable(R.drawable.ic_closed),
                            null,
                            getContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_red_24dp), null);
                }
                binding.expandableLayout.expand(true);
            } else {
                if (sharedPref.getLanguage().equalsIgnoreCase("ar")) {
                    binding.placeOpenTimeTv.setCompoundDrawablesWithIntrinsicBounds(
                            getContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_red_24dp),
                            null,
                            placeDetailsModel.isOpenNow() ?
                                    getContext().getResources().getDrawable(R.drawable.ic_access_time_black_24dp)
                                    : getContext().getResources().getDrawable(R.drawable.ic_closed), null);
                } else {
                    binding.placeOpenTimeTv.setCompoundDrawablesWithIntrinsicBounds(placeDetailsModel.isOpenNow() ?
                                    getContext().getResources().getDrawable(R.drawable.ic_access_time_black_24dp)
                                    : getContext().getResources().getDrawable(R.drawable.ic_closed),
                            null,
                            getContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_red_24dp), null);
                }
                binding.expandableLayout.collapse(true);
            }
        });
    }

    private SortedMap<String, DayModel> sortOpeningTimes(PlaceDetailsModel placeDetailsModel) {
        SortedMap<String, DayModel> tmpMap = new TreeMap<>((key1, key2) -> {
            //logic for comparing dates
            return Integer.compare(getWeekDaysIndex(key1), getWeekDaysIndex(key2));
        });
        tmpMap.putAll(placeDetailsModel.getOpeningTimes());
        System.out.println(tmpMap);
        return tmpMap;
    }

    private void moveMapToCurrentLocation(LatLng latLng) {
        if (googleMap != null) {
            MarkerOptions options = new MarkerOptions();
            options.position(latLng);
            BitmapDescriptor icon =
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_icon);
            options.icon(icon);

            googleMap.clear();
            googleMap.addMarker(options);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleMap != null) {
            binding.nearbyMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleMap != null) {
            binding.nearbyMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            binding.nearbyMapView.onDestroy();
        } catch (Exception e) {
            Log.e("MapCrash" ,e.getLocalizedMessage());
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        super.onDestroy();
        try {
            binding.nearbyMapView.onLowMemory();
        } catch (Exception e) {
            Log.e("MapCrash" ,e.getLocalizedMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onDestroyView() {
        googleMap.clear();
        binding.nearbyMapView.onDestroy();
        super.onDestroyView();
    }
}