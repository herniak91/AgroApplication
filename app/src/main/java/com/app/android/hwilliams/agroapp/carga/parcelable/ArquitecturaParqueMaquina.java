package com.app.android.hwilliams.agroapp.carga.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Hernan on 7/27/2016.
 */
public class ArquitecturaParqueMaquina implements Parcelable{
    private String nombre;
    private ArrayList<String> tiposMaquinas;

    public ArquitecturaParqueMaquina(String nombre, ArrayList<String> tiposMaquinas) {
        this.nombre = nombre;
        this.tiposMaquinas = tiposMaquinas;
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<String> getTiposMaquinas() {
        return tiposMaquinas;
    }

    protected ArquitecturaParqueMaquina(Parcel in) {
        nombre = in.readString();
        tiposMaquinas = in.createStringArrayList();
    }

    public static final Creator<ArquitecturaParqueMaquina> CREATOR = new Creator<ArquitecturaParqueMaquina>() {
        @Override
        public ArquitecturaParqueMaquina createFromParcel(Parcel in) {
            return new ArquitecturaParqueMaquina(in);
        }

        @Override
        public ArquitecturaParqueMaquina[] newArray(int size) {
            return new ArquitecturaParqueMaquina[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeStringList(tiposMaquinas);
    }
}
