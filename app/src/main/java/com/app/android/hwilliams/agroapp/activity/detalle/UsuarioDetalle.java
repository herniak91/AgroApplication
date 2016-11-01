package com.app.android.hwilliams.agroapp.activity.detalle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hernan on 8/18/2016.
 */
public class UsuarioDetalle implements Parcelable{
    private String username;
    private String telefono;
    private Integer usuarioId;

    public UsuarioDetalle(){}

    protected UsuarioDetalle(Parcel in) {
        username = in.readString();
        telefono = in.readString();
        usuarioId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(telefono);
        if(usuarioId == null)
            usuarioId = 0;
        dest.writeInt(usuarioId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UsuarioDetalle> CREATOR = new Creator<UsuarioDetalle>() {
        @Override
        public UsuarioDetalle createFromParcel(Parcel in) {
            return new UsuarioDetalle(in);
        }

        @Override
        public UsuarioDetalle[] newArray(int size) {
            return new UsuarioDetalle[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
}
