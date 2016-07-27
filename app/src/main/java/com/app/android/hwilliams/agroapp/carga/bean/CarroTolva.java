package com.app.android.hwilliams.agroapp.carga.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;

/**
 * Created by Hernan on 7/27/2016.
 */
public class CarroTolva extends MaquinaBasica{
    EditText capacidad;

    public CarroTolva(Context context, LayoutInflater inflater, MaquinaParcelable maquina) {
        super(context, inflater, R.layout.carga_item_carro_tolva, maquina);
        capacidad = (EditText) mainView.findViewById(R.id.carga_carro_tolva_capacidad);
    }

    public String getCapacidad(){
        return capacidad.getText().toString();
    }
}
