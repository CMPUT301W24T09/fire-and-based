package com.example.fire_and_based;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * fragment for the map
 * @author Tyler, Sumayya
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    Event event;
    String mode;

    /**
     * Creates a new instance of the MapFragment with the specified event and mode.
     *
     * @param event The event to be displayed on the map.
     * @param mode  The mode indicating the user's role (e.g., Attending, Organizing).
     * @return A new instance of the MapFragment.
     */
    public static MapFragment newInstance(Event event, String mode) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        args.putString("mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inflates the map fragment layout and initializes the map.
     *
     * @param inflater           LayoutInflater object for inflating views.
     * @param container          Parent view to which the fragment's UI should be attached.
     * @param savedInstanceState If not null, fragment is being reconstructed from a previous saved state.
     * @return Root view of the inflated layout.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);


        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
            mode = getArguments().getString("mode");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        return view;
    }

    /**
     * Called when the GoogleMap object is ready to be used.
     *
     * @param googleMap The GoogleMap object representing the map instance.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add markers or other map features here
        // Example: Add a marker at Sydney
        String eventAddress = event.getLocation();
        LatLng eventCoords = geocodeAddress(eventAddress);
        float zoomLevel = 13.0f;  // higher == more zoom
        BitmapDescriptor icon = bitmapDescriptorFromVector(getContext(), R.drawable.google_maps, 150, 150);

        mMap.addMarker(new MarkerOptions()
                .position(eventCoords)
                .title(event.getEventName())
                .icon(icon));

        if (Objects.equals(mode, "Organizing")){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUtil.getEventCheckInLocations(db, event, new OnSuccessListener<List<GeoPoint>>() {
                @Override
                public void onSuccess(List<GeoPoint> geoPoints) {
                    for (GeoPoint geoPoint : geoPoints) {
                        // Process each GeoPoint
                        LatLng point = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                        // Add a marker for each LatLng to the map
                        mMap.addMarker(new MarkerOptions().position(point).title(""));

                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error getting event check in Locations", Toast.LENGTH_SHORT).show();
                }
            });
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventCoords, zoomLevel));
    }

    /**
     * Geocodes the provided address to obtain its coordinates (latitude and longitude).
     * If the address is not found or an error occurs, it defaults to Edmonton, Alberta.
     *
     * @param address The address to geocode.
     * @return The LatLng object representing the coordinates of the address.
     */
    public LatLng geocodeAddress(String address) {
        List<Address> addressList = null;
        String eventLocation = event.getLocation();
        try {

            Geocoder geocoder = new Geocoder(getContext());
            addressList = geocoder.getFromLocationName(eventLocation, 1);

            if (addressList != null && !addressList.isEmpty()) {
                // Get the first address from the list
                Address location = addressList.get(0);
                // Create a LatLng object from the Address
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                return latLng;

            } else {
                // Handle case where no address was found -> Default to edmonton
                Toast.makeText(getContext(), "No location found for the address provided.", Toast.LENGTH_LONG).show();
                LatLng edmonton = new LatLng(53.5, -113.5); // Your default coordinates
                return edmonton;

            }
        } catch (IOException e) {
            // Handle the exception
            e.printStackTrace();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error finding address. Using default location.", Toast.LENGTH_LONG).show());
            }
            // defaults to edmonton alberta
            LatLng edmonton = new LatLng(53.5, -113.5); // Your default coordinates
            return edmonton;
        }

         catch (Exception e) {
            // Show a Toast message when an exception occurs
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error finding address. Using default location.", Toast.LENGTH_LONG).show());
            }
            // defaults to edmonton alberta
            LatLng edmonton = new LatLng(53.5, -113.5); // Your default coordinates
            return edmonton;
        }
    }


    /**
     * Creates a BitmapDescriptor from a vector drawable resource.
     *
     * @param context                  The context in which the vector drawable will be loaded.
     * @param vectorDrawableResourceId The resource ID of the vector drawable.
     * @param width                    The desired width of the bitmap.
     * @param height                   The desired height of the bitmap.
     * @return A BitmapDescriptor representing the vector drawable.
     */
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId, int width, int height) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, width, height); // Set the bounds with the new width and height
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
