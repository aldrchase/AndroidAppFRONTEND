package edu.uw.tcss450.group6project.ui.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.uw.tcss450.group6project.R;
import edu.uw.tcss450.group6project.databinding.FragmentMapsBinding;
import edu.uw.tcss450.group6project.model.LocationViewModel;
import edu.uw.tcss450.group6project.model.UserInfoViewModel;
import edu.uw.tcss450.group6project.ui.weather.model.WeatherViewModel;

/**
 * Fragment that displays a map and lets a user select a location
 * to retrieve weather information from.
 * @author Anthony
 */
public class WeatherMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener{

    /** Model for the user's current device location. */
    private LocationViewModel mLocationModel;
    /** Model for the weather. */
    private WeatherViewModel mWeatherModel;
    /** Model for the user's information. Required for JWT. */
    private UserInfoViewModel mUserModel;
    /** The map being displayed. */
    private GoogleMap mMap;

    /** Latitude of the last click. */
    private double mLatitude;
    /** Longitude of hte last click. */
    private double mLongitude;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentMapsBinding binding =FragmentMapsBinding.bind(view);
        mLocationModel = new ViewModelProvider(getActivity()).get(LocationViewModel.class);
        mWeatherModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.fab.setOnClickListener(click -> {
            mWeatherModel.connectLocation(mLatitude, mLongitude, mUserModel.getJWT());
            getActivity().onBackPressed();
        });

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("LAT/LONG", latLng.toString());
        mLatitude = latLng.latitude;
        mLongitude = latLng.longitude;
        mMap.clear();
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("New Marker"));
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        latLng, mMap.getCameraPosition().zoom));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationViewModel model = new ViewModelProvider(getActivity())
                .get(LocationViewModel.class);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        final LatLng c = new LatLng(mLocationModel.getLatitude(), mLocationModel.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(c, 15.0f));
        mMap.setOnMapClickListener(this);

        // Create a LatLngBounds that includes the US
        LatLngBounds usBounds = new LatLngBounds(
                new LatLng(30.79, -127.34), // SW bounds
                new LatLng(47.69, -67.25)  // NE bounds
        );

        // Constrain the camera target to the US bounds.
        mMap.setLatLngBoundsForCameraTarget(usBounds);

        //Constrain the amount the user can zoom out
        mMap.setMinZoomPreference(6);
    }
}