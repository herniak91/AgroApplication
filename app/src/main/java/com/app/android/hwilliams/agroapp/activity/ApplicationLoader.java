package com.app.android.hwilliams.agroapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.admin.AdminParque;
import com.app.android.hwilliams.agroapp.admin.AdminMaquina;
import com.app.android.hwilliams.agroapp.carga.parcelable.ArquitecturaParqueMaquina;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;
import com.app.android.hwilliams.agroapp.carga.parcelable.MarcaParcelable;
import com.app.android.hwilliams.agroapp.util.Params;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationLoader extends ActionBarActivity {
    private int APP_STARTED = 1;
    private int APP_NOT_STARTED = -1;
    private int APP_CURRENT_STATUS = 0;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_loader);
        progressBar = (ProgressBar) findViewById(R.id.appLoader_progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isConnected = isConnected();
        if(isConnected){
            progressBar.setVisibility(View.VISIBLE);
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
            boolean isUserLoggedIn = sharedPref.contains(Params.PREF_USERNAME);
            if(isUserLoggedIn){
                new CheckServerAvailable().execute(sharedPref.getString(Params.PREF_USERNAME, ""));
            }else{
                new CheckServerAvailable().execute();
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sin conexión");
            builder.setMessage("Conecte su dispositivo a Internet e intentelo nuevamente");
            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ApplicationLoader.this.finishAffinity();
                }
            });
            builder.show();
        }
    }

    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null)
            return false;
        return true;
    }

    private void startApplication(JSONObject json) {
        Intent intent = new Intent(this, Home.class);
        try {
            JSONObject response = (JSONObject) json.get("response");
            intent.putExtra(Home.EXTRA_COTIZACIONES, ((JSONArray)response.get("cotizaciones")).toString());
            intent.putExtra(Home.EXTRA_DOLAR, ((JSONObject)response.get("dolar")).toString());
            intent.putExtra(Home.EXTRA_MERCADOS, ((JSONArray)response.get("mercados")).toString());
            intent.putParcelableArrayListExtra(Administracion.EXTRA_GROUPS, (ArrayList<? extends Parcelable>) getAdminData(response.get("admin").toString()));
            intent.putParcelableArrayListExtra(Carga.EXTRA_OPCIONES_MAQUINA, (ArrayList<? extends Parcelable>) getOpcionesMaquinas(response.getJSONObject("maquinariaParams")));
            intent.putParcelableArrayListExtra(Carga.EXTRA_ARQ_PARQUES, (ArrayList<? extends Parcelable>) getArquitecturaParques(response.getJSONObject("maquinariaParams")));
        }catch (Exception e){
            e.printStackTrace();
        }
        this.startActivityForResult(intent, 0);
    }

    private List<ArquitecturaParqueMaquina> getArquitecturaParques(JSONObject maquinas) throws JSONException {
        List<ArquitecturaParqueMaquina> arquitecturas = new ArrayList<>();
        JSONArray arquitecturaParques = maquinas.getJSONArray("arquitecturaParques");
        for(int i = 0; i < arquitecturaParques.length(); i++){
            JSONObject obj = arquitecturaParques.getJSONObject(i);
            ArrayList<String> tiposMaquina = new ArrayList<>();
            JSONArray modelosArr = obj.getJSONArray("maquinas");
            for(int i4 = 0; i4 < modelosArr.length(); i4++){
                tiposMaquina.add(modelosArr.getString(i4));
            }
            arquitecturas.add(new ArquitecturaParqueMaquina(obj.getString("nombre"), tiposMaquina));
        }
        return arquitecturas;
    }

    private List<MaquinaParcelable> getOpcionesMaquinas(JSONObject maquinas) throws JSONException {
        List<MaquinaParcelable> resultList = new ArrayList<>();
        JSONArray maquinasArr = maquinas.getJSONArray("maquinas");
        for(int i = 0; i < maquinasArr.length(); i++){
            JSONObject tipoMaquina = maquinasArr.getJSONObject(i);

            // Marcas, con sus modelos correspondientes
            ArrayList<MarcaParcelable> marcas = new ArrayList<>();
            JSONArray marcaArr = tipoMaquina.getJSONArray("marcas");
            for(int i3 = 0; i3 < marcaArr.length(); i3++){
                JSONObject obj = marcaArr.getJSONObject(i3);
                ArrayList<String> modelos = new ArrayList<>();
                JSONArray modelosArr = obj.getJSONArray("modelos");
                for(int i4 = 0; i4 < modelosArr.length(); i4++){
                    modelos.add(modelosArr.getString(i4));
                }
                marcas.add(new MarcaParcelable(obj.getString("nombre"), modelos));
            }

            String tipo = tipoMaquina.getString("tipo");
            MaquinaParcelable maquina = new MaquinaParcelable(tipo, marcas);
            if(tipo.equalsIgnoreCase("carro tolva")){
                ArrayList<String> capacidades = new ArrayList<>();
                JSONArray capacidadArr = tipoMaquina.getJSONArray("capacidad");
                for(int i2 = 0; i2 < capacidadArr.length(); i2++){
                    capacidades.add(capacidadArr.getString(i2));
                }
                maquina.setCapacidades(capacidades);
            }
            if(tipo.equalsIgnoreCase("laboreo")){
                ArrayList<String> tiposTrabajo = new ArrayList<>();
                JSONArray capacidadArr = tipoMaquina.getJSONArray("tipo_trabajo");
                for(int i2 = 0; i2 < capacidadArr.length(); i2++){
                    tiposTrabajo.add(capacidadArr.getString(i2));
                }
                maquina.setTiposTrabajo(tiposTrabajo);
            }
            resultList.add(maquina);
        }
        return resultList;
    }

    private List<AdminParque> getAdminData(String jsonAdmin) throws JSONException {
        List<AdminParque> groupList = new ArrayList<>();
        JSONArray list = new JSONArray(jsonAdmin);
        for (int i = 0; i < list.length(); i++){
            JSONObject obj = list.getJSONObject(i);
            JSONObject groupJson = obj.getJSONObject("parque");
            List<AdminMaquina> items = new ArrayList<>();
            JSONArray itemsJsonArray = obj.getJSONArray("maquinas");
            for (int a = 0; a < itemsJsonArray.length(); a++){
                JSONObject maquinaJson = itemsJsonArray.getJSONObject(a);
                items.add(new AdminMaquina(maquinaJson.getInt("id"), maquinaJson.getString("tipo"), maquinaJson.getString("marca"), maquinaJson.getString("modelo"), maquinaJson.getString("atributos")));
            }
            AdminParque groupObj = new AdminParque(groupJson.getInt("id"), groupJson.getString("estado"), groupJson.getString("rubro"), items);
            groupList.add(groupObj);
        }
        return groupList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onBackPressed();
    }

    private class CheckServerAvailable extends AsyncTask<String,Integer,JSONObject>{

        @Override
        protected JSONObject doInBackground(String... params) {
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection  = 25000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection );
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(Params.URL_INITIAL);
            try {
                if(params != null && params.length > 0){
                    List nameValuePairs = new ArrayList();
                    nameValuePairs.add(new BasicNameValuePair("username", params[0]));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                }
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                return new JSONObject(responseString);
            } catch (ClientProtocolException e){
                return null;
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            if(json != null){
                progressBar.setVisibility(View.GONE);
                startApplication(json);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationLoader.this);
                builder.setTitle("Problema de conexión");
                builder.setMessage("Lamentablemente la aplicacion no puede conectarse en este momento. Intentelo nuevamente mas tarde");
                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ApplicationLoader.this.finishAffinity();
                    }
                });
                builder.show();
            }

        }
    }
}
