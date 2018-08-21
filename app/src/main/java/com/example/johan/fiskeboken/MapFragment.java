package com.example.johan.fiskeboken;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applemacbookair.locationtracker.LocationTracker;
import com.example.applemacbookair.locationtracker.OnLocationChanged;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MapFragment extends Fragment implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback {
    private GoogleMap mGoogleMap; //Map
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;


    //For saving states
    static final String STATE_OF_LIST = "listState";
    static final String WEIGHT_STATE = "weightState";
    static final String LENGTH_STATE = "heightState";
    static final String LANG_STATE = "langState";
    static final String PICKER_MODE_STATE = "pickerModeState";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Object mGeoDataClient;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(STATE_OF_LIST, AppSettings.getCatchList());
        savedInstanceState.putSerializable(WEIGHT_STATE, AppSettings.isKg());
        savedInstanceState.putSerializable(LENGTH_STATE, AppSettings.isM());
        savedInstanceState.putSerializable(LANG_STATE, AppSettings.isSwedish());
        savedInstanceState.putSerializable(PICKER_MODE_STATE, AppSettings.isMapPickerMode());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

            AppSettings.setAllCatchesList((ArrayList<Catch>) savedInstanceState.getSerializable(STATE_OF_LIST));
            AppSettings.setKg((Boolean) savedInstanceState.getSerializable(WEIGHT_STATE));
            AppSettings.setM((Boolean) savedInstanceState.getSerializable(LENGTH_STATE));
            AppSettings.setSwedish((Boolean) savedInstanceState.getSerializable(LANG_STATE));
            AppSettings.setMapPickerMode((Boolean) savedInstanceState.getSerializable(PICKER_MODE_STATE));

            MapFragment mapFragment = new MapFragment();
            getFragmentManager().beginTransaction().replace(R.id.frag, mapFragment).commit();
            FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_button);
            floatingActionButton.show();
            BottomNavigationView navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.map);
            TextView mTitle = (TextView) getActivity().findViewById(R.id.title);
            mTitle.setText("Karta");
        }
    }

    //Constructors
    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapView mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this); //when you already implement OnMapReadyCallback in your fragment

    }

    @Override
    public void onMapLoaded() {
    }

    @SuppressLint("MissingPermission")
    public void onMapReady(final GoogleMap googleMap) {
        try {
            MapsInitializer.initialize(getContext());
        } catch (NullPointerException e) {
        }

        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        ArrayList<Catch> cl = AppSettings.getCatchList();

        if (cl.size() > 0) { //Set camera to zoomed in on first item in list, instead of full world screen
            LatLng zoomHere = new LatLng(AppSettings.getCatchList().get(0).getGpsLat(), AppSettings.getCatchList().get(0).getGpsLong());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomHere, 12.0f));
        }

        if (AppSettings.isMapPickerMode()) { //If we want to pick a coordinate on the map for adding a catch

            final LocationTracker tracker;
            tracker =new LocationTracker(getActivity()); //Gets current position

            double longitude = tracker.getLongitude();
            double latitude = tracker.getLatitude();
            LatLng test = new LatLng(latitude,longitude);
            MarkerOptions mark = new MarkerOptions().position(test);
            mark.title("Du är här");
            mGoogleMap.addMarker(mark);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 12.0f));

            tracker.setOnLocationChanged(new OnLocationChanged() {
                @Override
                public void OnChange(Location location) {}

            });

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("Ny fisk");
                    googleMap.addMarker(marker);

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    AppSettings.getTempCatch().setGpsLat(point.latitude);
                    AppSettings.getTempCatch().setGpsLong(point.longitude);
                    AddNewFragment addNewFragment = new AddNewFragment();
                    getFragmentManager().beginTransaction().replace(R.id.frag, addNewFragment).commit();
                    TextView mTitle = (TextView) getActivity().findViewById(R.id.title);
                    mTitle.setText("Lägg till fisk");
                    AppSettings.setMapPickerMode(false);
                }
            });
        }
        else { //Loop through catchlist and place marker on each catch on map
            for (int i = 0; i < cl.size(); i++) {
                showOneMarker(i);
            }
        }
    }

    //Show marker number "i" in Catchlist on map
    private void showOneMarker(int i) { //Place one marker on map
        ArrayList<Catch> cl = AppSettings.getCatchList();
        DateTime date = cl.get(i).getDate();
        String dateStr = date.getYear() + "-" + date.getMonthOfYear() + "-" + date.getDayOfMonth();

        try {
            LatLng newMarker = new LatLng(cl.get(i).getGpsLat(), cl.get(i).getGpsLong());
            MarkerOptions mark = new MarkerOptions().position(newMarker);
            mark.title(dateStr + " " + cl.get(i).getFishType() + " " + cl.get(i).getWeight() + "kg");
            mGoogleMap.addMarker(mark);
        }
        catch (NullPointerException e) {}
    }


}
