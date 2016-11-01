package com.app.android.hwilliams.agroapp.activity.detalle;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.app.android.hwilliams.agroapp.admin.AdminMaquina;

/**
 * Created by Hernan on 8/3/2016.
 */
public class MaquinaDetalle implements Parcelable {
    private int id;
    private String tipo = "";
    private String marca = "";
    private String modelo = "";
    private String capacidad = "";
    private String tipoTrabajo = "";
    private String imageDir = "";
    private boolean mapeo =  false;
    private Bitmap imageView;

    public MaquinaDetalle(){
    }

    public static MaquinaDetalle createFromAdmin(AdminMaquina m){
        MaquinaDetalle bean = new MaquinaDetalle();
        bean.setId(m.getId());
        bean.setTipo(m.getTipo());
        bean.setMarca(m.getMarca());
        bean.setModelo(m.getModelo());
        return bean;
    }

    protected MaquinaDetalle(Parcel in) {
        id = in.readInt();
        tipo = in.readString();
        marca = in.readString();
        modelo = in.readString();
        capacidad = in.readString();
        tipoTrabajo = in.readString();
        imageDir = in.readString();
        mapeo = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(tipo);
        dest.writeString(marca);
        dest.writeString(modelo);
        dest.writeString(capacidad);
        dest.writeString(tipoTrabajo);
        dest.writeString(imageDir);
        dest.writeByte((byte) (mapeo ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MaquinaDetalle> CREATOR = new Creator<MaquinaDetalle>() {
        @Override
        public MaquinaDetalle createFromParcel(Parcel in) {
            return new MaquinaDetalle(in);
        }

        @Override
        public MaquinaDetalle[] newArray(int size) {
            return new MaquinaDetalle[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getTipoTrabajo() {
        return tipoTrabajo;
    }

    public void setTipoTrabajo(String tipoTrabajo) {
        this.tipoTrabajo = tipoTrabajo;
    }

    public boolean isMapeo() {
        return mapeo;
    }

    public void setMapeo(boolean mapeo) {
        this.mapeo = mapeo;
    }

    public String getImageDir() {
        return imageDir;
    }

    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    public Bitmap getImageView() {
        return imageView;
    }

    public void setImageView(Bitmap imageView) {
        this.imageView = imageView;
    }
}
