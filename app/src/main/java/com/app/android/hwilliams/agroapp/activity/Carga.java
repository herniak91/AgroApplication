package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.app.android.hwilliams.agroapp.carga.bean.CarroTolva;
import com.app.android.hwilliams.agroapp.carga.bean.Laboreo;
import com.app.android.hwilliams.agroapp.carga.bean.MaquinaBasica;
import com.app.android.hwilliams.agroapp.carga.bean.Sembradora;
import com.app.android.hwilliams.agroapp.carga.parcelable.ArquitecturaParqueMaquina;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;
import com.app.android.hwilliams.agroapp.carga.parcelable.MarcaParcelable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Carga extends Activity {
    public static final String EXTRA_RUBRO = "rubro";
    public static final String EXTRA_OPCIONES_MAQUINA = "opMaquina";
    public static final String EXTRA_ARQ_PARQUES = "arqParque";

    private static final int ACTION_IMAGE_CAPTURE = 1;
    private static final int ACTION_IMAGE_SELECT = 3;
    private static final int ACTION_DETALLE = 2;

    TextView rubro;
    Button siguiente;
    LinearLayout container;
    List<ArquitecturaParqueMaquina> arquitecturas;
    List<MaquinaParcelable> opcionesMaquinas;

    // A ser usados por las maquinas contenidas
    TextView destinoImage;
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
            final View body = getMaquinaByTipo(getApplicationContext(), inflater, maquina);
            body.setVisibility(View.GONE);

            View header = inflater.inflate(R.layout.carga_maquina_header, null);
            final TextView tipo = (TextView) header.findViewById(R.id.carga_group_check_tipo);
            tipo.setText(maquina.getTipo());
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
    }

    private void startDetalleActivity() {
        Intent intent = new Intent(Carga.this, Detalle.class);
        intent.putExtra(Detalle.EXTRA_RUBRO,rubro.getText().toString());
        intent.putParcelableArrayListExtra(Detalle.EXTRA_MAQUINAS, (ArrayList<? extends Parcelable>) getMaquinasCargadas());
        startActivityForResult(intent, ACTION_DETALLE);
    }

    // Usado por las maquinas cargandose
    public void takePicture(TextView destino, ImageButton cancel) {
/*        destinoImage = destino;
        cancelView = cancel;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String root = Environment.getExternalStorageDirectory().toString();
        Calendar cal = Calendar.getInstance();
        destinoFile = new File(root, "AgroRent_" + cal.get(Calendar.HOUR) + cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND) + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, destinoFile.getAbsolutePath()); // set the image file name
        startActivityForResult(intent, ACTION_IMAGE_CAPTURE);*/

        destinoImage = destino;
        cancelView = cancel;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Calendar cal = Calendar.getInstance();
        String imageFileName = "AgroRent_" + cal.get(Calendar.HOUR) + cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND);
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
//            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            image = new File(storageDir, "AgroRent_" + cal.get(Calendar.HOUR) + cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND) + ".jpg");
            destinoFile = image;
            Uri uri = Uri.fromFile(image);
            //           Uri photoURI = FileProvider.getUriForFile(Carga.this, "com.app.android.hwilliams.agroapp.fileprovider", image);
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
                        super.onBackPressed();
                        break;
                    }
                    case Detalle.RESULT_DESCARTAR :{
                        super.onBackPressed();
                        break;
                    }
                    default:{
                        break;
                    }
                }
                break;
            }
            default:
                break;
        }
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
        if (!anySelected)
            return false;
        return true;
    }

    private List<MaquinaParcelable> getMaquinasCargadas(){
        List<MaquinaParcelable> maquinasCargadas = new ArrayList<>();
        String tipo = null;
        ArrayList<MarcaParcelable> marcasArr;
        ArrayList<String> modelos;
        for(int i =0; i < container.getChildCount(); i = i + 2 ){
            // Header
            View header = container.getChildAt(i);
            if(((CheckBox) header.findViewById(R.id.carga_group_check_yes)).isChecked()){
                TextView tipoView = (TextView) header.findViewById(R.id.carga_group_check_tipo);
                tipo = tipoView.getText().toString();
                // Body
                View body = container.getChildAt(i+1);
                MaquinaBasica maquinaView = (MaquinaBasica) body;
                modelos = new ArrayList<>();
                modelos.add(maquinaView.getModelo());
                marcasArr = new ArrayList<>();
                marcasArr.add(new MarcaParcelable(maquinaView.getMarca(), modelos));
                MaquinaParcelable maquina = new MaquinaParcelable(tipo,marcasArr);
                if ("sembradora".equalsIgnoreCase(tipo)){
                    Sembradora s = (Sembradora)body;
                    maquina.setMapeo(s.tieneMapeo());
                }
                if ("carro tolva".equalsIgnoreCase(tipo)){
                    CarroTolva ct = (CarroTolva)body;
                    ArrayList<String> capacidadArr = new ArrayList<>();
                    capacidadArr.add(ct.getCapacidad());
                    maquina.setCapacidades(capacidadArr);
                }
                if ("laboreo".equalsIgnoreCase(tipo)){
                    Laboreo l = (Laboreo)body;
                    ArrayList<String> tiposArr = new ArrayList<>();
                    tiposArr.add(l.getTipoTrabajo());
                    maquina.setTiposTrabajo(tiposArr);
                }
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
}
