package com.app.android.hwilliams.agroapp.util;

/**
 * Created by Hernan on 6/13/2016.
 */
public class Params {

//    public static final String URL_APP = "http://186.19.251.22:8080/AgroServer/";
    public static final String URL_APP = "http://192.168.2.3:8080/AgroServer/";
    public static final String URL_INITIAL = URL_APP + "Home/initialInfo";
    public static final String URL_PERFIL_CREAR = URL_APP + "Perfil/crear";
    public static final String URL_PERFIL_LOGIN = URL_APP + "Perfil/login";
    public static final String URL_PERFIL_ACTUALIZAR = URL_APP + "Perfil/actualizar";
    public static final String URL_PERFIL_CHECK_USERNAME = URL_APP + "Perfil/verificarUsername";
    public static final String URL_PARQUEMAQUINA_CREAR = URL_APP + "Parque/crear";
    public static final String URL_PARQUEMAQUINA_BUSCAR = URL_APP + "Parque/buscar";
    public static final String URL_PARQUEMAQUINA_BUSCAR_DETALLE = URL_APP + "Parque/buscarEnDetalle";
    public static final String URL_PARQUEMAQUINA_BORRAR = URL_APP + "Parque/borrar";


    public static final String PREF_USERNAME = "username";
    public static final String PREF_NOMBRE = "nombre";
    public static final String PREF_APELLIDO = "apellido";
    public static final String PREF_TEL = "tel";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_PASS = "password";

    public static final String URL_CLIMA = "https://api.forecast.io/forecast/531d1d487866e79e4b45b0b725944e46/";
}
