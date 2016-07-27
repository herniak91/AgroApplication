package com.app.android.hwilliams.agroapp.carga.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Hernan on 7/26/2016.
 */
public class MaquinaParcelable implements Parcelable{
    private String tipo;
    private ArrayList<MarcaParcelable> marcas;

    public MaquinaParcelable(String tipo, ArrayList<MarcaParcelable> marcas) {
        this.tipo = tipo;
        this.marcas = marcas;
    }

    protected MaquinaParcelable(Parcel in) {
        tipo = in.readString();
        marcas = in.createTypedArrayList(MarcaParcelable.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tipo);
        dest.writeTypedList(marcas);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MaquinaParcelable> CREATOR = new Creator<MaquinaParcelable>() {
        @Override
        public MaquinaParcelable createFromParcel(Parcel in) {
            return new MaquinaParcelable(in);
        }

        @Override
        public MaquinaParcelable[] newArray(int size) {
            return new MaquinaParcelable[size];
        }
    };

    public String getTipo() {
        return tipo;
    }

    public ArrayList<MarcaParcelable> getMarcas() {
        return marcas;
    }
}
