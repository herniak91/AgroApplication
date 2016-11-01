package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.activity.detalle.MaquinaDetalle;
import com.app.android.hwilliams.agroapp.activity.detalle.ParqueDetalle;
import com.app.android.hwilliams.agroapp.activity.detalle.UsuarioDetalle;
import com.app.android.hwilliams.agroapp.activity.map.LocationSelector;
import com.app.android.hwilliams.agroapp.carga.bean.CarroTolva;
import com.app.android.hwilliams.agroapp.carga.bean.Laboreo;
import com.app.android.hwilliams.agroapp.carga.bean.MaquinaBasica;
import com.app.android.hwilliams.agroapp.carga.bean.Sembradora;
import com.app.android.hwilliams.agroapp.carga.parcelable.ArquitecturaParqueMaquina;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;
import com.app.android.hwilliams.agroapp.carga.parcelable.MarcaParcelable;
import com.app.android.hwilliams.agroapp.util.Params;
import com.app.android.hwilliams.agroapp.util.PerfilUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Carga extends Activity {
    public static final String EXTRA_RUBRO = "rubro";
    public static final String EXTRA_OPCIONES_MAQUINA = "opMaquina";
    public static final String EXTRA_ARQ_PARQUES = "arqParque";
    public static final String EXTRA_PARQUE_A_EDITAR = "parque";
    public static final String EXTRA_MAQUINAS_A_EDITAR = "maquinas";

    private static final int ACTION_IMAGE_CAPTURE = 1;
    private static final int ACTION_IMAGE_SELECT = 3;
    private static final int ACTION_DETALLE = 2;
    private static final int ACTION_GET_LOCATION = 4;

    TextView rubro;
    Button siguiente;
    LinearLayout container;
    List<ArquitecturaParqueMaquina> arquitecturas;
    List<MaquinaParcelable> opcionesMaquinas;
    Double lat = null;
    Double lon = null;

    // A ser usados por las maquinas contenidas
    TextView destinoImage, ubicacion;
    ImageButton cancelView;
    File destinoFile;
    //

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga);
        rubro = (TextView) findViewById(R.id.carga_rubro);
        rubro.setText(getIntent().getStringExtra(EXTRA_RUBRO));

        arquitecturas = getIntent().getParcelableArrayListExtra(EXTRA_ARQ_PARQUES);
        opcionesMaquinas = getIntent().getParcelableArrayListExtra(EXTRA_OPCIONES_MAQUINA);

        final List<MaquinaParcelable> groups = getMaquinasDeArquitecturaByRubro();

        container = (LinearLayout) findViewById(R.id.carga_container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (final MaquinaParcelable maquina : groups) {
            final View body;
            MaquinaDetalle maquinaDetalle = getMaquinaDetalle(maquina.getTipo());
            if(maquinaDetalle == null){
                // Carga de un parque nuevo
                body = getMaquinaByTipo(getApplicationContext(), inflater, maquina);
            }else{
                // Edicion de un parque existente
                body = getMaquinaByTipo(getApplicationContext(), inflater, maquina, maquinaDetalle);
            }

            body.setVisibility(View.GONE);

            View header = inflater.inflate(R.layout.carga_maquina_header, null);
            final TextView tipo = (TextView) header.findViewById(R.id.carga_group_check_tipo);
            tipo.setText(maquina.getTipo().toUpperCase());
            tipo.setEnabled(false);
            tipo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newVisivility = body.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                    if (newVisivility == View.VISIBLE)
                        hideAllViews();
                    body.setVisibility(newVisivility);
                }
            });

            CheckBox yes = (CheckBox) header.findViewById(R.id.carga_group_check_yes);
            yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        hideAllViews();
                        body.setVisibility(View.VISIBLE);
                        tipo.setEnabled(true);
                    } else {
                        body.setVisibility(View.GONE);
                        tipo.setEnabled(false);
                    }
                }
            });
            yes.setChecked(maquinaDetalle != null);

            container.addView(header);
            container.addView(body);
        }

        siguiente = (Button) findViewById(R.id.carga_siguiente);
        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldCheck()) {
                    startDetalleActivity();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Carga.this);
                    dialog.setTitle("Un momento!").setMessage("Alguna de la maquinaria que va a cargar no tiene información. ¿Desea continuar de cualquier manera?");
                    dialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDetalleActivity();
                        }
                    }).setNegativeButton("Revisar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

            }
        });

        ubicacion = (TextView) findViewById(R.id.carga_ubicacion);
        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationSelector.class);
                startActivityForResult(intent, ACTION_GET_LOCATION);
            }
        });
        cambiarEstadoUbicacion(false);
    }

    private void startDetalleActivity() {
        ParqueDetalle parque = new ParqueDetalle(rubro.getText().toString(), getMaquinasCargadas());
        parque.setId(0);
        parque.setLat(lat);
        parque.setLon(lon);
        parque.setEstado("");
        SharedPreferences pref = getPreferencesFile();
        UsuarioDetalle usuarioDetalle = new UsuarioDetalle();
        usuarioDetalle.setUsername(pref.getString(Params.PREF_USERNAME, ""));
        usuarioDetalle.setTelefono(pref.getString(Params.PREF_TEL, ""));
        parque.setUsuario(usuarioDetalle);
        Intent intent = new Intent(Carga.this, DetalleCarga.class);
        intent.putExtra(Detalle.EXTRA_PARQUE, parque);
        intent.putParcelableArrayListExtra(Detalle.EXTRA_MAQUINAS_PARQUE, parque.getMaquinas());
        startActivityForResult(intent, ACTION_DETALLE);
    }

    private SharedPreferences getPreferencesFile(){
        return getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
    }

    // Usado por las maquinas cargandose
    public void takePicture(TextView destino, ImageButton cancel) {
        destinoImage = destino;
        cancelView = cancel;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Calendar cal = Calendar.getInstance();
        String imageFileName = "AgroRent_" + cal.get(Calendar.HOUR) + cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND);
        File image = null;
        try {
            image = new File(storageDir.getAbsolutePath() + "/Camera/AgroRent_" + cal.get(Calendar.HOUR) + cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND) + ".jpg");
            destinoFile = image;
            Uri uri = Uri.fromFile(image);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // set the image file name
            startActivityForResult(intent, ACTION_IMAGE_CAPTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectPicture(TextView destino, ImageButton cancel){
        destinoImage = destino;
        cancelView = cancel;
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, ACTION_IMAGE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ACTION_IMAGE_CAPTURE :{
                if (resultCode == RESULT_OK) {
                    destinoImage.setText(destinoFile.getName());
                    cancelView.setVisibility(View.VISIBLE);
                }
                break;
            }
            case ACTION_IMAGE_SELECT : {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data != null ? data.getData() : Uri.EMPTY;
                    destinoImage.setText(selectedImage.getLastPathSegment());
                    cancelView.setVisibility(View.VISIBLE);
                }
                break;
            }
            case ACTION_DETALLE : {
                switch (resultCode){
                    case RESULT_OK :{
                        getIntent().putExtra(DetalleCarga.EXTRA_PARQUE, data.getParcelableArrayListExtra(DetalleCarga.EXTRA_PARQUE));
                        setResult(RESULT_OK, getIntent());
                        finish();
                        break;
                    }
                    case RESULT_CANCELED :{
                        super.onBackPressed();
                        break;
                    }
                    default:{
                        break;
                    }
                }
                break;
            }
            case ACTION_GET_LOCATION : {
                if (resultCode == RESULT_OK) {
                    Bundle MBuddle = data.getExtras();
                    lat = MBuddle.getDouble("lat");
                    lon = MBuddle.getDouble("lon");
                    cambiarEstadoUbicacion(true);
                }else{
                    lat = null;
                    lon = null;
                    cambiarEstadoUbicacion(false);
                }
                break;
            }
            default:
                break;
        }
    }

    private void cambiarEstadoUbicacion(boolean selected){
        if(!selected){
            ubicacion.setText("Seleccione una ubicación");
            ubicacion.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            return;
        }
        ubicacion.setText("Ubicación elegida");
        ubicacion.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
    }

    private List<MaquinaParcelable> getMaquinasDeArquitecturaByRubro() {
        String rubroSeleccionado = rubro.getText().toString();
        ArquitecturaParqueMaquina arquitecturaSeleccionada = null;
        for (ArquitecturaParqueMaquina arq : arquitecturas) {
            if (arq.getNombre().equalsIgnoreCase(rubroSeleccionado)) {
                arquitecturaSeleccionada = arq;
                break;
            }
        }
        List<MaquinaParcelable> maquinas = new ArrayList<>();
        for (String maquina : arquitecturaSeleccionada.getTiposMaquinas()) {
            for (MaquinaParcelable maq : opcionesMaquinas) {
                if (maq.getTipo().equalsIgnoreCase(maquina)) {
                    maquinas.add(maq);
                    break;
                }
            }
        }
        return maquinas;
    }

    private MaquinaBasica getMaquinaByTipo(Context context, LayoutInflater inflater, final MaquinaParcelable maquina) {
        if ("sembradora".equalsIgnoreCase(maquina.getTipo()))
            return new Sembradora(this, inflater, maquina);
        if ("carro tolva".equalsIgnoreCase(maquina.getTipo()))
            return new CarroTolva(this, inflater, maquina);
        if ("laboreo".equalsIgnoreCase(maquina.getTipo()))
            return new Laboreo(this, inflater, maquina);
        return new MaquinaBasica(this, inflater, R.layout.carga_maquina_basica, maquina);
    }

    private MaquinaBasica getMaquinaByTipo(Context context, LayoutInflater inflater, MaquinaParcelable maquina, MaquinaDetalle maquinaDetalle) {
        if ("sembradora".equalsIgnoreCase(maquina.getTipo()))
            return new Sembradora(this, inflater, maquina, maquinaDetalle);
        if ("carro tolva".equalsIgnoreCase(maquina.getTipo()))
            return new CarroTolva(this, inflater, maquina, maquinaDetalle);
        if ("laboreo".equalsIgnoreCase(maquina.getTipo()))
            return new Laboreo(this, inflater, maquina, maquinaDetalle);
        return new MaquinaBasica(this, inflater, R.layout.carga_maquina_basica, maquina, maquinaDetalle);
    }

    private boolean fieldCheck() {
        boolean anySelected = false;
        for (int i = 0; i < container.getChildCount(); i = i + 2) {
            LinearLayout header = (LinearLayout) container.getChildAt(i);
            CheckBox yes = (CheckBox) header.findViewById(R.id.carga_group_check_yes);
            if (yes.isChecked()) {
                anySelected = true;
                MaquinaBasica maquina = (MaquinaBasica) container.getChildAt(i + 1);
                if (maquina.isMissingInformation())
                    return false;
            }
        }
        if (!anySelected || lat == null || lon == null)
            return false;
        return true;
    }

    private ArrayList<MaquinaDetalle> getMaquinasCargadas(){
        ArrayList<MaquinaDetalle> maquinasCargadas = new ArrayList<>();
        String tipo = null;
        for(int i =0; i < container.getChildCount(); i = i + 2 ){
            // Header
            View header = container.getChildAt(i);
            if(((CheckBox) header.findViewById(R.id.carga_group_check_yes)).isChecked()){
                TextView tipoView = (TextView) header.findViewById(R.id.carga_group_check_tipo);
                tipo = tipoView.getText().toString();
                // Body
                View body = container.getChildAt(i+1);
                MaquinaBasica maquinaView = (MaquinaBasica) body;
                MaquinaDetalle maquina = new MaquinaDetalle();
                maquina.setTipo(tipo);
                maquina.setMarca(maquinaView.getMarca());
                maquina.setModelo(maquinaView.getModelo());
                if ("sembradora".equalsIgnoreCase(tipo)){
                    maquina.setMapeo(((Sembradora)body).tieneMapeo());
                }
                if ("carro tolva".equalsIgnoreCase(tipo)){
                    maquina.setCapacidad(((CarroTolva)body).getCapacidad());
                }
                if ("laboreo".equalsIgnoreCase(tipo)){
                    maquina.setTipoTrabajo(((Laboreo)body).getTipoTrabajo());
                }
                maquina.setImageDir(maquinaView.getImageDir());
                maquinasCargadas.add(maquina);
            }
        }
        return maquinasCargadas;
    }

    private void hideAllViews() {
        for (int i = 1; i < container.getChildCount(); i = i + 2) {
            container.getChildAt(i).setVisibility(View.GONE);
        }
    }


    // Si se trata de una edicion , esto no devuelve nulo
    public MaquinaDetalle getMaquinaDetalle(String tipoMaquina) {
        ParqueDetalle parque = getIntent().getParcelableExtra(EXTRA_PARQUE_A_EDITAR);
        if(parque != null){
            ArrayList<MaquinaDetalle> maquinas = getIntent().getParcelableArrayListExtra(EXTRA_MAQUINAS_A_EDITAR);
            for (MaquinaDetalle maquina : maquinas) {
                if(tipoMaquina.equalsIgnoreCase(maquina.getTipo()))
                    return maquina;
            }
        }
        return null;
    }
}
