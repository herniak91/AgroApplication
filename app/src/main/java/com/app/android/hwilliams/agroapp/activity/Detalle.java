package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;
import com.app.android.hwilliams.agroapp.carga.parcelable.MarcaParcelable;

import java.util.List;

public class Detalle extends Activity {
    public static final int RESULT_DESCARTAR = 5;
    public static final int RESULT_EDITAR = 6;
    public static final String EXTRA_MAQUINAS = "parque";
    public static final String EXTRA_RUBRO = "rubro";

    List<MaquinaParcelable> maquinasCargadas;
    String rubro;

    ViewFlipper flipperImagenes;
    TableLayout containerMaquinas;
    Button descartar, editar, guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        maquinasCargadas = getIntent().getParcelableArrayListExtra(EXTRA_MAQUINAS);
        rubro = getIntent().getStringExtra(EXTRA_RUBRO);

        flipperImagenes = (ViewFlipper) findViewById(R.id.detalle_flipper);
        containerMaquinas = (TableLayout) findViewById(R.id.detalle_container_maquinas);
        descartar = (Button) findViewById(R.id.detalle_descartar);
        editar = (Button) findViewById(R.id.detalle_editar);
        guardar = (Button) findViewById(R.id.detalle_guardar);

        LinearLayout layout;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (MaquinaParcelable maq : maquinasCargadas) {
            layout = (LinearLayout) inflater.inflate(R.layout.detalle_maquina_row, null);
            String tipo = maq.getTipo();
            ((TextView)layout.findViewById(R.id.tipo)).setText(tipo.toUpperCase());
            String marcaModelo = "";
            MarcaParcelable marca = maq.getMarcas().get(0);
            if(!"".equalsIgnoreCase(marca.getNombre())){
                marcaModelo = "MARCA: " + marca.getNombre();
                if(!"".equalsIgnoreCase(marca.getModelos().get(0)))
                    marcaModelo = marcaModelo + " - MODELO: " + marca.getModelos().get(0);
                TextView marcaModeloView = (TextView)layout.findViewById(R.id.marca_modelo);
                marcaModeloView.setText(marcaModelo);
                marcaModeloView.setVisibility(View.VISIBLE);
            }

            if("sembradora".equalsIgnoreCase(tipo)){
                TextView mapeo = (TextView)layout.findViewById(R.id.mapeo);
                mapeo.setText( maq.isMapeo() ? "Incluye mapeo satelital" : "No incluye mapeo satelital" );
                mapeo.setVisibility(View.VISIBLE);
            }
            if("carro tolva".equalsIgnoreCase(tipo) && !"".equalsIgnoreCase(maq.getCapacidades().get(0))){
                TextView capacidad = (TextView)layout.findViewById(R.id.capacidad);
                capacidad.setText("Capacidad: " + maq.getCapacidades().get(0));
                capacidad.setVisibility(View.VISIBLE);
            }
            if("laboreo".equalsIgnoreCase(tipo) && !"".equalsIgnoreCase(maq.getTiposTrabajo().get(0))){
                TextView laboreo = (TextView)layout.findViewById(R.id.tipo_trabajo);
                laboreo.setText("Especialización: " + maq.getTiposTrabajo().get(0));
                laboreo.setVisibility(View.VISIBLE);
            }

            TableRow row = new TableRow(getApplicationContext());
            row.setGravity(Gravity.CENTER_HORIZONTAL);
            row.addView(layout);
            containerMaquinas.addView(row);
        }

        descartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }
}
