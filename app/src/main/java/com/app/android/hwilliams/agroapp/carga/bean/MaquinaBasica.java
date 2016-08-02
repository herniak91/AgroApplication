package com.app.android.hwilliams.agroapp.carga.bean;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.activity.Carga;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;
import com.app.android.hwilliams.agroapp.carga.parcelable.MarcaParcelable;

import java.util.ArrayList;

/**
 * Created by Hernan on 7/27/2016.
 */
public class MaquinaBasica extends LinearLayout{
    protected View mainView;
    protected AutoCompleteTextView marca, modelo;
    protected Button imagenCarga;
    protected ImageButton imagenCancel;
    protected Context contexto;
    TextView imageDir;
    CharSequence[] opcionesImagen;

    public MaquinaBasica(Context context, LayoutInflater inflater, int layoutId, final MaquinaParcelable maquina) {
        super(context);
        contexto = context;
        mainView = inflater.inflate(layoutId, this);

        marca = (AutoCompleteTextView) mainView.findViewById(R.id.carga_item_marca);
        modelo = (AutoCompleteTextView) mainView.findViewById(R.id.carga_item_modelo);
        imagenCarga = (Button) mainView.findViewById(R.id.carga_imagen_action);
        imagenCancel = (ImageButton) mainView.findViewById(R.id.carga_imagen_cancel);
        imageDir = (TextView) mainView.findViewById(R.id.carga_item_image_dir);

        // marcas
        ArrayList<String> marcas = new ArrayList<>();
        for (MarcaParcelable marc: maquina.getMarcas()) {
            marcas.add(marc.getNombre());
        }
        ArrayAdapter<String> marcasAdapter = new ArrayAdapter<>(context,android.R.layout.simple_dropdown_item_1line, marcas);
        marca.setAdapter(marcasAdapter);

        // modelos
        ArrayList<String> modelos = new ArrayList<>();
        ArrayAdapter<String> modelosAdapter = new ArrayAdapter<>(context,android.R.layout.simple_dropdown_item_1line, modelos);

        modelo.setAdapter(modelosAdapter);
        modelo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    String marcaSeleccionada = marca.getText().toString();
                    ArrayAdapter adapter = (ArrayAdapter)((AutoCompleteTextView)v).getAdapter();
                    for (MarcaParcelable marca: maquina.getMarcas()) {
                        if(marca.getNombre().equalsIgnoreCase(marcaSeleccionada)){
                            adapter.clear();
                            adapter.addAll(marca.getModelos());
                            break;
                        }
                    }
                }
            }
        });

        // Imagen
        opcionesImagen = new CharSequence[]{"Galeria"};
        if(getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            opcionesImagen = new CharSequence[]{"Galeria", "Camara"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(opcionesImagen, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                starImageSelection(item);
            }
        });

        imagenCarga.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });
        imagenCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDir.setText("");
                v.setVisibility(GONE);
            }
        });
    }

    private void starImageSelection(int pos){
        if(opcionesImagen[pos].toString().equalsIgnoreCase("Galeria")){
            ((Carga)contexto).selectPicture(imageDir,imagenCancel);
            return;
        }
        if(opcionesImagen[pos].toString().equalsIgnoreCase("Camara")){
            ((Carga)contexto).takePicture(imageDir,imagenCancel);
        }
    }

    public boolean isMissingInformation(){
        if("".equalsIgnoreCase(marca.getText().toString()) && "".equalsIgnoreCase(modelo.getText().toString()))
            return true;
        return false;
    }

    public String getMarca(){
        return marca.getText().toString();
    }

    public String getModelo(){
        return modelo.getText().toString();
    }

}
