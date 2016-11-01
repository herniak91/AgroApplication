package com.app.android.hwilliams.agroapp.activity.detalle;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.android.hwilliams.agroapp.admin.AdminMaquina;
import com.app.android.hwilliams.agroapp.admin.AdminParque;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hernan on 8/18/2016.
 */
public class ParqueDetalle implements Parcelable {

    private Integer id;
    private String rubro;
    private UsuarioDetalle usuario;
    private ArrayList<MaquinaDetalle> maquinas;
    private String estado;
    private Double lat;
    private Double lon;

    public ParqueDetalle(String rub, ArrayList<MaquinaDetalle> maquinas){
        this.rubro = rub;
        this.maquinas = maquinas;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(rubro);
        dest.writeParcelable(usuario, flags);
//        dest.writeList(maquinas);
        dest.writeString(estado);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }


    protected ParqueDetalle(Parcel in) {
        id = in.readInt();
        rubro = in.readString();
        usuario = in.readParcelable(UsuarioDetalle.class.getClassLoader());
//        maquinas = in.createTypedArrayList(MaquinaDetalle.CREATOR);
        estado = in.readString();
        lat = Double.valueOf(in.readDouble());
        lon = Double.valueOf(in.readDouble());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParqueDetalle> CREATOR = new Creator<ParqueDetalle>() {
        @Override
        public ParqueDetalle createFromParcel(Parcel in) {
            return new ParqueDetalle(in);
        }

        @Override
        public ParqueDetalle[] newArray(int size) {
            return new ParqueDetalle[size];
        }
    };

    public static ParqueDetalle createFromAdmin(AdminParque parque){
        ArrayList<MaquinaDetalle> maquinas = new ArrayList<>();
        for (AdminMaquina maqAd : parque.getItems()) {
            maquinas.add(MaquinaDetalle.createFromAdmin(maqAd));
        }
        ParqueDetalle bean = new ParqueDetalle(parque.getRubro(), maquinas);
        bean.setId(parque.getId());
        bean.setEstado(parque.getEstado());
        bean.setLat(parque.getLat());
        bean.setLon(parque.getLon());
        bean.setUsuario(parque.getUsuario());
        return bean;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRubro() {
        return rubro;
    }

    public void setRubro(String rubro) {
        this.rubro = rubro;
    }

    public UsuarioDetalle getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDetalle usuario) {
        this.usuario = usuario;
    }

    public ArrayList<MaquinaDetalle> getMaquinas() {
        return maquinas;
    }

    public void setMaquinas(ArrayList<MaquinaDetalle> maquinas) {
        this.maquinas = maquinas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
