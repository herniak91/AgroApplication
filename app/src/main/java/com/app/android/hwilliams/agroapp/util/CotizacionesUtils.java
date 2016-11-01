package com.app.android.hwilliams.agroapp.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.android.hwilliams.agroapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hernan on 7/23/2016.
 */
public class CotizacionesUtils {

    public static void setCotizacionDolar(TextView compra, TextView venta, String jsonDolar) {
        /*TextView compra = (TextView) findViewById(R.id.home_dolarCompra);
        TextView venta = (TextView) findViewById(R.id.home_dolarVenta);*/
        String valorCompra = "", valorVenta = "";
        try{
            JSONObject obj = new JSONObject(jsonDolar);
            valorCompra = "$" + obj.getString("compra");
            valorVenta = "$" + obj.getString("venta");
        } catch (JSONException e) {

        }
        compra.setText("Compra: " + valorCompra);
        venta.setText("Venta: " + valorVenta);
    }

    public static List<String> getListaMercados(String stringExtra) {
        List<String> list = new ArrayList<String>();
        try {
            JSONArray array = new JSONArray(stringExtra);
            for (int i = 0; i < array.length(); i++){
                list.add((String) array.get(i));
            }
        } catch (JSONException e) {
        }
        return list;
    }

    public static void populateRowsInformacion(LinearLayout tableInformacion, String stringExtra, LayoutInflater inflater, Context context){
        try {
            JSONArray array = new JSONArray(stringExtra);
            for (int i = 0; i < array.length(); i++){
                JSONObject cereal = array.getJSONObject(i);
                String nombre = cereal.getString("nombre");
                //Se agrega cereal
                RelativeLayout cerealView = (RelativeLayout) inflater.inflate(R.layout.cotizaciones_cereal, null);
                cerealView.setPadding(10,0,4,0);

                TextView cerealText = (TextView) cerealView.findViewById(R.id.grano);
                cerealText.setBackgroundColor(Color.parseColor("#F2F7D5"));
                cerealText.setMinHeight(25);
                cerealText.setText(nombre);

                LinearLayout cerealCotizaciones = (LinearLayout) cerealView.findViewById(R.id.container_mercados);
                cerealCotizaciones.setPadding(4,0,4,0);
 //               cerealCotizaciones.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                agregarCeldaInformacion(cerealCotizaciones, inflater, "QQ / Ha", "$ / Ha");
                JSONArray quintalesHectarea = cereal.getJSONArray("qqhaes");
                for (int j=0; j < quintalesHectarea.length(); j++){
                    JSONObject cot = quintalesHectarea.getJSONObject(j);
                    String qqha = cot.getString("qqha");
                    Double precio = cot.getDouble("precio");
                    // Se agrega precio
                    agregarCeldaInformacion(cerealCotizaciones, inflater, qqha, String.valueOf(precio));
                }
                tableInformacion.addView(cerealView);
            }

        }catch(JSONException e){

        }

    }

    private static void agregarCeldaInformacion(LinearLayout cerealCotizaciones, LayoutInflater inflater, String qqha, String precio){
        LinearLayout cotView = (LinearLayout) inflater.inflate(R.layout.tab_informacion_cotizacion, null);
        cotView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        ((TextView)cotView.findViewById(R.id.qqha)).setText(qqha);
        ((TextView)cotView.findViewById(R.id.precio)).setText(precio);

        cerealCotizaciones.addView(cotView);
    }

    public static void populateRowsCotizaciones(LinearLayout tableCotizaciones, String stringExtra, LayoutInflater inflater, Context context) {
        Map<String, List<Cotizacion>> map = new HashMap<>();
        try {
            JSONArray array = new JSONArray(stringExtra);
            for (int i = 0; i < array.length(); i++){
                JSONObject cotiz = (JSONObject) array.get(i);

                Cotizacion cot = new Cotizacion();
                cot.nombre = cotiz.getString("nombre");
                cot.valor = cotiz.getString("valor");
                cot.variacion = cotiz.getString("variacion");
                cot.indicador = cotiz.getString("indicador");
                cot.mercado = cotiz.getString("mercado");

                if(map.get(cot.nombre) == null)
                    map.put(cot.nombre, new ArrayList<Cotizacion>());

                map.get(cot.nombre).add(cot);
            }

            for (Map.Entry<String, List<Cotizacion>> entry : map.entrySet()) {
                RelativeLayout main = (RelativeLayout) inflater.inflate(R.layout.cotizaciones_cereal, null);
                TextView cereal = (TextView) main.findViewById(R.id.grano);
                cereal.setBackgroundColor(Color.parseColor("#F2F7D5"));
                cereal.setMinHeight(25);
                cereal.setText(entry.getKey());

                LinearLayout container = (LinearLayout) main.findViewById(R.id.container_mercados);
                for (Cotizacion cot : entry.getValue()) {
                    LinearLayout frame = new LinearLayout(context);
                    frame.setOrientation(LinearLayout.VERTICAL);
                    frame.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                    frame.addView(createLabel(cot.mercado, context));
                    frame.addView(createLabel(cot.valor, context));
                    frame.addView(createLabel(cot.variacion, context));

                    container.addView(frame);
                }
                tableCotizaciones.addView(main);
            }

        } catch (JSONException e) {
        }
    }

    private static TextView createLabel(String content, Context context){
        TextView label = new TextView(context);
        label.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            label.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        label.setText(content);
        return label;
    }

}
