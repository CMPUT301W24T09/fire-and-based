package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    Event event;

    public static MapFragment newInstance(Event event) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event); // Make sure your Event class implements Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);


        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add markers or other map features here
        // Example: Add a marker at Sydney
        LatLng sydney = new LatLng(-34, 151);
        String eventAddress = event.getLocation();
        LatLng eventCoords = geocodeAddress(eventAddress);
        mMap.addMarker(new MarkerOptions().position(eventCoords).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(eventCoords));
    }

    public LatLng geocodeAddress(String address) {
        String api = "AIzaSyA7KifextDr3K6nGzosM-EJDDaHpPyRHqs";
        try {
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(api)
                    .build();
            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
            // Assuming the address is valid and results are returned
            if (results.length > 0) {
                double lat = results[0].geometry.location.lat;
                double lng = results[0].geometry.location.lng;
                LatLng addressCords = new LatLng(lat, lng);

                return addressCords;
            } else {

                return null;
            }
        } catch (Exception e) {
            // just return edmonton cordinates if error happens
            LatLng edmonton = new LatLng(53.5, -113.5);
            return edmonton;
        }

    }

}
