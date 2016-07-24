package com.app.android.hwilliams.agroapp.util;

import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.app.android.hwilliams.agroapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

    public static void populateRows(TableLayout tableCotizaciones, String stringExtra, Map<String, List<TableRow>> rowMap) {
        TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.FILL_PARENT);
        TableRow.LayoutParams cellLayout = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        TableRow.LayoutParams indicadorCellLayout = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 2);

        try {
            JSONArray array = new JSONArray(stringExtra);
            for (int i = 0; i < array.length(); i++){
                JSONObject cotiz = (JSONObject) array.get(i);
                TableRow row = new TableRow(tableCotizaciones.getContext());
                row.setLayoutParams(rowLayout);
                // grano
                TextView nombre = new TextView(row.getContext());
                nombre.setEnabled(false);
                nombre.setTextAppearance(row.getContext(), android.R.style.TextAppearance_Large);
                nombre.setTypeface(nombre.getTypeface(), Typeface.BOLD);
                nombre.setGravity(Gravity.CENTER);
                nombre.setLayoutParams(cellLayout);
                nombre.setText(cotiz.getString("nombre"));
                row.addView(nombre);
                // valor
                TextView valor = new TextView(row.getContext());
                valor.setEnabled(false);
                valor.setTextAppearance(row.getContext(), android.R.style.TextAppearance_Medium);
                valor.setLayoutParams(cellLayout);
                valor.setGravity(Gravity.CENTER);
                valor.setText(cotiz.getString("valor"));
                row.addView(valor);
                // variacion
                TextView variacion = new TextView(row.getContext());
                variacion.setEnabled(false);
                variacion.setTextAppearance(row.getContext(), android.R.style.TextAppearance_Medium);
                variacion.setLayoutParams(cellLayout);
                variacion.setGravity(Gravity.CENTER);
                variacion.setText(cotiz.getString("variacion"));
                row.addView(variacion);
                // indicador variacion
                TextView indicador = new TextView(row.getContext());
                indicador.setEnabled(false);
                indicador.setTextAppearance(row.getContext(), android.R.style.TextAppearance_Medium);
                indicador.setLayoutParams(indicadorCellLayout);
                indicador.setGravity(Gravity.CENTER);
                indicador.setText(cotiz.getString("indicador"));
                row.addView(indicador);

                // mercado
                String mercado = cotiz.getString("mercado");
                if(!rowMap.containsKey(mercado))
                    rowMap.put(mercado, new ArrayList<TableRow>());
                rowMap.get(mercado).add(row);

                tableCotizaciones.addView(row);
            }
        } catch (JSONException e) {

        }
    }

}
