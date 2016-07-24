package com.app.android.hwilliams.agroapp.admin;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hernan on 7/23/2016.
 */
public class AdminMaquina implements Parcelable{
    private Integer id;
    private String tipo;
    private String marca;
    private String modelo;
    private String atributos;

    public AdminMaquina(Integer id, String tipo, String marca, String modelo, String atributos) {
        this.id = id;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.atributos = atributos;
    }

    protected AdminMaquina(Parcel in) {
        id = in.readInt();
        tipo = in.readString();
        marca = in.readString();
        modelo = in.readString();
        atributos = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(tipo);
        dest.writeString(marca);
        dest.writeString(modelo);
        dest.writeString(atributos);
    }

    public static final Creator<AdminMaquina> CREATOR = new Creator<AdminMaquina>() {
        @Override
        public AdminMaquina createFromParcel(Parcel in) {
            return new AdminMaquina(in);
        }

        @Override
        public AdminMaquina[] newArray(int size) {
            return new AdminMaquina[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public Integer getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getAtributos() {
        return atributos;
    }
}
