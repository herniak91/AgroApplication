package com.app.android.hwilliams.agroapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.activity.detalle.MaquinaDetalle;
import com.app.android.hwilliams.agroapp.util.JsonPost;
import com.app.android.hwilliams.agroapp.util.Params;
import com.app.android.hwilliams.agroapp.util.PerfilUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class DetalleCarga extends Detalle {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Acciones posibles
        editar = (Button) findViewById(R.id.detalle_editar);
        guardar = (Button) findViewById(R.id.detalle_guardar);

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GuardarParqueMaquina().execute();
            }
        });

        editar.setVisibility(View.VISIBLE);
        guardar.setVisibility(View.VISIBLE);
        contactar.setVisibility(View.INVISIBLE);
    }

    private class GuardarParqueMaquina extends AsyncTask<Object, JSONObject, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object[] params) {
            try {
                JSONObject parameters = new JSONObject();
                parameters.put("username", parque.getUsuario().getUsername());
                parameters.put("rubro", parque.getRubro());
                parameters.put("lat", parque.getLat());
                parameters.put("lon", parque.getLon());
                JSONArray maqsArr = new JSONArray();
                for (MaquinaDetalle maq : parque.getMaquinas()) {
                    maqsArr.put(getJSONFromMaquina(maq));
                }
                parameters.put("maquinas", maqsArr);
                return new JsonPost().postData(Params.URL_PARQUEMAQUINA_CREAR, parameters.toString());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject obj) {
            super.onPostExecute(obj);
            try{
                if((Integer)obj.get("code") != 0)
                    throw new RuntimeException();

                getIntent().putExtra(EXTRA_PARQUE, (ArrayList<? extends Parcelable>) PerfilUtils.getAdminData(obj.get("response").toString()));
                setResult(RESULT_OK, getIntent());
                finish();
            }catch (Exception e){
                e.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage("En este momento no se puede guardarButton su parque de maquinas. Intentelo nuevamente mas tarde");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
                builder.show();
            }

        }

        private JSONObject getJSONFromMaquina(MaquinaDetalle maq) throws JSONException {
            JSONObject param = new JSONObject();
            String tipo = maq.getTipo();
            param.put("id", maq.getId());
            param.put("tipo", maq.getTipo());
            param.put("marca", maq.getMarca());
            param.put("modelo",maq.getModelo());
            if ("sembradora".equalsIgnoreCase(tipo)){
                param.put("mapeo",maq.isMapeo());
            }
            if ("carro tolva".equalsIgnoreCase(tipo)){
                param.put("capacidad",maq.getCapacidad());
            }
            if ("laboreo".equalsIgnoreCase(tipo)){
                param.put("tipoTrabajo",maq.getTipoTrabajo());
            }

            if(maq.getImageView() != null) {
                int bytes = maq.getImageView().getByteCount();
                ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
                maq.getImageView().copyPixelsToBuffer(buffer); //Move the byte data to the buffer
                byte[] array = buffer.array();
                String base64String = Base64.encodeToString(array, Base64.DEFAULT);
                param.put("image", base64String);
            }
            return param;
        }
    }
}
