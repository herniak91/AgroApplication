package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.activity.detalle.MaquinaDetalle;
import com.app.android.hwilliams.agroapp.activity.detalle.ParqueDetalle;

import java.util.ArrayList;

public class Detalle extends Activity {
    public static final String EXTRA_PARQUE = "parque";
    public static final String EXTRA_MAQUINAS_PARQUE = "maquinasParque";
    ParqueDetalle parque;

    ViewFlipper contenedorImagenes;
    TableLayout contenedorMaquinas;
    Button editar, guardar, contactar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        // Imagenes
        contenedorImagenes = (ViewFlipper) findViewById(R.id.detalle_flipper);
        contenedorImagenes.setInAnimation(this, R.anim.slide_from_right);
        contenedorImagenes.setOutAnimation(this, R.anim.slide_to_left);
        // Resumen del parque. Maquinas que contiene
        contenedorMaquinas = (TableLayout) findViewById(R.id.detalle_container_maquinas);
        // Acciones posibles
        editar = (Button) findViewById(R.id.detalle_editar);
        guardar = (Button) findViewById(R.id.detalle_guardar);
        contactar = (Button) findViewById(R.id.detalle_contactar);

        parque = getIntent().getParcelableExtra(EXTRA_PARQUE);
        ArrayList<MaquinaDetalle> maquinas2 = getIntent().getParcelableArrayListExtra(EXTRA_MAQUINAS_PARQUE);
        parque.setMaquinas(maquinas2);

        LinearLayout layout_maquina;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (MaquinaDetalle maq : parque.getMaquinas()) {
            layout_maquina = (LinearLayout) inflater.inflate(R.layout.detalle_maquina_row, null);
            String tipo = maq.getTipo();
            ((TextView)layout_maquina.findViewById(R.id.tipo)).setText(tipo.toUpperCase());
            String marcaModelo = "";
            if(!"".equalsIgnoreCase(maq.getMarca())){
                marcaModelo = "MARCA: " + maq.getMarca();
                if(!"".equalsIgnoreCase(maq.getModelo()))
                    marcaModelo = marcaModelo + " - MODELO: " + maq.getModelo();
                TextView marcaModeloView = (TextView)layout_maquina.findViewById(R.id.marca_modelo);
                marcaModeloView.setText(marcaModelo);
                marcaModeloView.setVisibility(View.VISIBLE);
            }

            if("sembradora".equalsIgnoreCase(tipo)){
                TextView mapeo = (TextView)layout_maquina.findViewById(R.id.mapeo);
                mapeo.setText( maq.isMapeo() ? "Incluye mapeo satelital" : "No incluye mapeo satelital" );
                mapeo.setVisibility(View.VISIBLE);
            }
            if("carro tolva".equalsIgnoreCase(tipo) && !"".equalsIgnoreCase(maq.getCapacidad())){
                TextView capacidad = (TextView)layout_maquina.findViewById(R.id.capacidad);
                capacidad.setText("Capacidad: " + maq.getCapacidad());
                capacidad.setVisibility(View.VISIBLE);
            }
            if("laboreo".equalsIgnoreCase(tipo) && !"".equalsIgnoreCase(maq.getTipoTrabajo())){
                TextView laboreo = (TextView)layout_maquina.findViewById(R.id.tipo_trabajo);
                laboreo.setText("Especialización: " + maq.getTipoTrabajo());
                laboreo.setVisibility(View.VISIBLE);
            }
            TableRow row = new TableRow(getApplicationContext());
            row.setGravity(Gravity.CENTER_HORIZONTAL);
            row.addView(layout_maquina);
            contenedorMaquinas.addView(row);
        }
    }

   /* private void addImage(ViewFlipper viewFlipper, String imageDir, MaquinaDetalle maquinaDetalle) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String mCurrentPhotoPath = storageDir.getAbsolutePath() + "/Camera/" + imageDir;
        // Get the dimensions of the View
        int targetW = viewFlipper.getWidth();
        int targetH = viewFlipper.getHeight();

        // Get the dimensions of the bitmap
       *//* BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;*//*

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        maquinaDetalle.setImageView(bitmap);
        ImageView mImageView = new ImageView(getApplicationContext());
        mImageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mImageView.setImageBitmap(bitmap);
        viewFlipper.addView(mImageView);
    }*/

}
