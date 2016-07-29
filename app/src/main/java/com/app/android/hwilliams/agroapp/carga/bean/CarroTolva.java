package com.app.android.hwilliams.agroapp.carga.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;

/**
 * Created by Hernan on 7/27/2016.
 */
public class CarroTolva extends MaquinaBasica{
    AutoCompleteTextView capacidad;

    public CarroTolva(Context context, LayoutInflater inflater, MaquinaParcelable maquina) {
        super(context, inflater, R.layout.carga_maquina_carro_tolva, maquina);
        capacidad = (AutoCompleteTextView) mainView.findViewById(R.id.carga_carro_tolva_capacidad);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_dropdown_item_1line, maquina.getCapacidades());
        capacidad.setAdapter(adapter);
    }

    public String getCapacidad(){
        return capacidad.getText().toString();
    }

    @Override
    public boolean isMissingInformation() {
        return super.isMissingInformation() && "".equalsIgnoreCase(capacidad.getText().toString());
    }
}
