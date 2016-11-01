package com.app.android.hwilliams.agroapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DetalleResultado extends Detalle {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri call = Uri.parse("tel:" + parque.getUsuario().getTelefono());
                Intent intent = new Intent(Intent.ACTION_DIAL, call);
                startActivity(intent);
            }
        });

        editar.setVisibility(View.INVISIBLE);
        guardar.setVisibility(View.INVISIBLE);
        contactar.setVisibility(View.VISIBLE);
    }
}
