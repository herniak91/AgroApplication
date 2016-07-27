package com.app.android.hwilliams.agroapp.carga.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;
import com.app.android.hwilliams.agroapp.carga.parcelable.MarcaParcelable;

import java.util.ArrayList;

/**
 * Created by Hernan on 7/27/2016.
 */
public class MaquinaBasica extends LinearLayout{
    protected View mainView;
    protected AutoCompleteTextView marca, modelo;

    public MaquinaBasica(Context context, LayoutInflater inflater, int layoutId, final MaquinaParcelable maquina) {
        super(context);
        mainView = inflater.inflate(layoutId, this);

        marca = (AutoCompleteTextView) mainView.findViewById(R.id.carga_item_marca);
        modelo = (AutoCompleteTextView) mainView.findViewById(R.id.carga_item_modelo);

        // marcas
        ArrayList<String> marcas = new ArrayList<>();
        for (MarcaParcelable marc: maquina.getMarcas()) {
            marcas.add(marc.getNombre());
        }
        ArrayAdapter<String> marcasAdapter = new ArrayAdapter<>(context,android.R.layout.simple_dropdown_item_1line, marcas);
        marca.setAdapter(marcasAdapter);

        // modelos
        ArrayList<String> modelos = new ArrayList<>();
        modelos.add("Seleccione una marca");
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
