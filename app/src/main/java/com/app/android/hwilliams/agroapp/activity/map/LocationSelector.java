package com.app.android.hwilliams.agroapp.activity.map;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.app.android.hwilliams.agroapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationSelector extends FragmentActivity implements OnMapReadyCallback {
    public static final String EXTRA_LAT = "lat";
    public static final String EXTRA_LON = "lon";

    Button confirm;
    FrameLayout confirmContainer;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selector);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        confirm = (Button) findViewById(R.id.loc_selector_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent().putExtra("lat",marker.getPosition().latitude);
                getIntent().putExtra("lon",marker.getPosition().longitude);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        confirmContainer = (FrameLayout) findViewById(R.id.loc_selector_confirm_container);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-34.813364,-59.9510945), 7));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(marker != null)
                    marker.remove();
                marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                confirmContainer.setVisibility(View.VISIBLE);
            }
        });

        Double lat = getIntent().getDoubleExtra(EXTRA_LAT, -1);
        Double lon = getIntent().getDoubleExtra(EXTRA_LON, -1);
        if(lat != -1 && lon != -1){
            googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
            confirmContainer.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }
}
