package com.app.android.hwilliams.agroapp.carga.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Hernan on 7/26/2016.
 */
public class MarcaParcelable implements Parcelable{
    private String nombre;
    private ArrayList<String> modelos;

    public MarcaParcelable(String nombre, ArrayList<String> modelos) {
        this.nombre = nombre;
        this.modelos = modelos;
    }

    protected MarcaParcelable(Parcel in) {
        nombre = in.readString();
        modelos = in.createStringArrayList();
    }

    public static final Creator<MarcaParcelable> CREATOR = new Creator<MarcaParcelable>() {
        @Override
        public MarcaParcelable createFromParcel(Parcel in) {
            return new MarcaParcelable(in);
        }

        @Override
        public MarcaParcelable[] newArray(int size) {
            return new MarcaParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeStringList(modelos);
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<String> getModelos() {
        return modelos;
    }
}
