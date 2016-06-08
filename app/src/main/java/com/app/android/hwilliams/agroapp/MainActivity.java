package com.app.android.hwilliams.agroapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.app.android.hwilliams.agroapp.fragment.ClimaFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragManager = getFragmentManager();
        Fragment climaFrag = fragManager.findFragmentById(R.id.frag_clima);
        Fragment cotizFrag = fragManager.findFragmentById(R.id.frag_cotiz);

    }

}
