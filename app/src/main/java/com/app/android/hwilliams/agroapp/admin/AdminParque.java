package com.app.android.hwilliams.agroapp.admin;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.android.hwilliams.agroapp.activity.detalle.UsuarioDetalle;

import java.util.List;

/**
 * Created by Hernan on 7/23/2016.
 */
public class AdminParque implements Parcelable{
    private Integer id;
    private String rubro;
    private String estado;
    private List<AdminMaquina> items;
    private double lat;
    private double lon;
    private UsuarioDetalle usuario;

    public AdminParque(Integer id, String estado, String rubro, List<AdminMaquina> items){
        this.id = id;
        this.rubro = rubro;
        this.estado = estado;
        this.items = items;
    }

    protected AdminParque(Parcel in) {
        id = in.readInt();
        rubro = in.readString();
        estado = in.readString();
        items = in.createTypedArrayList(AdminMaquina.CREATOR);
        lat = in.readDouble();
        lon = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(rubro);
        dest.writeString(estado);
        dest.writeTypedList(items);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }

    public static final Creator<AdminParque> CREATOR = new Creator<AdminParque>() {
        @Override
        public AdminParque createFromParcel(Parcel in) {
            return new AdminParque(in);
        }

        @Override
        public AdminParque[] newArray(int size) {
            return new AdminParque[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public String getRubro() {
        return rubro;
    }

    public String getEstado() {
        return estado;
    }

    public List<AdminMaquina> getItems() {
        return items;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public UsuarioDetalle getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDetalle usuario) {
        this.usuario = usuario;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
