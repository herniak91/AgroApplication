package com.app.android.hwilliams.agroapp.carga.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hernan on 7/26/2016.
 */
public class MaquinaParcelable implements Parcelable{
    private String tipo;
    private ArrayList<MarcaParcelable> marcas;
    private ArrayList<String> capacidades;
    private ArrayList<String> tiposTrabajo;

    //Usado internamente entre Carga-Detalle
    private boolean mapeo;

    public MaquinaParcelable(String tipo, ArrayList<MarcaParcelable> marcas) {
        this.tipo = tipo;
        this.marcas = marcas;
    }

    protected MaquinaParcelable(Parcel in) {
        tipo = in.readString();
        marcas = in.createTypedArrayList(MarcaParcelable.CREATOR);
        capacidades = in.createStringArrayList();
        tiposTrabajo = in.createStringArrayList();
        mapeo = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tipo);
        dest.writeTypedList(marcas);
        dest.writeStringList(capacidades);
        dest.writeStringList(tiposTrabajo);
        dest.writeByte((byte) (mapeo ? 1 : 0));
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

    public ArrayList<String> getCapacidades() {
        return capacidades;
    }

    public void setCapacidades(ArrayList<String> capacidades) {
        this.capacidades = capacidades;
    }

    public ArrayList<String> getTiposTrabajo() {
        return tiposTrabajo;
    }

    public void setTiposTrabajo(ArrayList<String> tiposTrabajo) {
        this.tiposTrabajo = tiposTrabajo;
    }

    public boolean isMapeo() {
        return mapeo;
    }

    public void setMapeo(boolean mapeo) {
        this.mapeo = mapeo;
    }

}
