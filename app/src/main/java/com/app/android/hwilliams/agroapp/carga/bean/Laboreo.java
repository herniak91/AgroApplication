package com.app.android.hwilliams.agroapp.carga.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.activity.detalle.MaquinaDetalle;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;

/**
 * Created by Hernan on 7/27/2016.
 */
public class Laboreo extends MaquinaBasica {
    AutoCompleteTextView tipoTrabajo;

    public Laboreo(Context context, LayoutInflater inflater, MaquinaParcelable maquina) {
        super(context, inflater, R.layout.carga_maquina_laboreo, maquina);
        tipoTrabajo = (AutoCompleteTextView) mainView.findViewById(R.id.carga_item_tipoTrabajo);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_dropdown_item_1line, maquina.getTiposTrabajo());
        tipoTrabajo.setAdapter(adapter);
    }

    public Laboreo(Context context, LayoutInflater inflater, MaquinaParcelable maquina,  MaquinaDetalle maquinaDetalle) {
        super(context, inflater,R.layout.carga_maquina_laboreo, maquina, maquinaDetalle);
        tipoTrabajo = (AutoCompleteTextView) mainView.findViewById(R.id.carga_item_tipoTrabajo);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_dropdown_item_1line, maquina.getTiposTrabajo());
        tipoTrabajo.setAdapter(adapter);
        tipoTrabajo.setText(maquinaDetalle.getTipoTrabajo());
    }

    @Override
    public boolean isMissingInformation() {
        return super.isMissingInformation() && "".equalsIgnoreCase(tipoTrabajo.getText().toString());
    }

    public String getTipoTrabajo() {
        return tipoTrabajo.getText().toString();
    }

    public void setTipoTrabajo(String tipo){
        tipoTrabajo.setText(tipo);
    }
}
