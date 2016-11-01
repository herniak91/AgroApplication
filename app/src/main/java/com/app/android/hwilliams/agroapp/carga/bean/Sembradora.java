package com.app.android.hwilliams.agroapp.carga.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RadioGroup;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.activity.detalle.MaquinaDetalle;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;

/**
 * Created by Hernan on 7/27/2016.
 */
public class Sembradora extends MaquinaBasica {
    RadioGroup mapeo;

    public Sembradora(Context context, LayoutInflater inflater, MaquinaParcelable maquina) {
        super(context, inflater,R.layout.carga_maquina_sembradora, maquina);
        mapeo = (RadioGroup) mainView.findViewById(R.id.carga_sembradora_mapeo);
    }

    public Sembradora(Context context, LayoutInflater inflater, MaquinaParcelable maquina,  MaquinaDetalle maquinaDetalle) {
        super(context, inflater,R.layout.carga_maquina_sembradora, maquina, maquinaDetalle);
        mapeo = (RadioGroup) mainView.findViewById(R.id.carga_sembradora_mapeo);
        if(maquinaDetalle.isMapeo()){
            mapeo.check(R.id.carga_sembradora_mapeo_si);
            return;
        }
        mapeo.check(R.id.carga_sembradora_mapeo_no);
    }



    public boolean tieneMapeo(){
        switch (mapeo.getCheckedRadioButtonId()){
            case R.id.carga_sembradora_mapeo_si:
                return true;
            default:
                return false;
        }
    }
}
