package com.app.android.hwilliams.agroapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.android.hwilliams.agroapp.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class Busqueda extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        /*FragmentManager fragManager = getFragmentManager();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        MapFragment googleMap = (MapFragment)fragManager.findFragmentById(R.id.buscar_map);
        googleMap.getMapAsync(this);*/

        Button buscarButton = (Button) findViewById(R.id.busqueda_boton_buscar);
        buscarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Busqueda.this, Resultados.class);
                Busqueda.this.startActivity(intent);
            }
        });
    }


    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setOnMapClickListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
        showToast("Location updated");
    }

    @Override
    public void onMapClick(LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Busqueda.this);
        builder.setTitle("Confimar ubicacion");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToast("Se confirmo la ubicacion");
            }
        }).setNegativeButton("Cambiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToast("Se quiere cambiar la ubicacion");
            }
        });
        builder.create().show();
    }
}
