package com.app.android.hwilliams.agroapp.activity.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.activity.Detalle;
import com.app.android.hwilliams.agroapp.activity.DetalleResultado;
import com.app.android.hwilliams.agroapp.activity.detalle.MaquinaDetalle;
import com.app.android.hwilliams.agroapp.activity.detalle.ParqueDetalle;
import com.app.android.hwilliams.agroapp.activity.detalle.UsuarioDetalle;
import com.app.android.hwilliams.agroapp.admin.AdminMaquina;
import com.app.android.hwilliams.agroapp.admin.AdminParque;
import com.app.android.hwilliams.agroapp.util.Params;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultadosMap extends FragmentActivity implements OnMapReadyCallback {
    public static final String EXTRA_RESULTADOS = "results";
    public static final String EXTRA_RUBRO  =  "rubro";
    List<AdminParque> results;
    Map<Marker, AdminParque> parquesUbicados = new HashMap<>();
    AdminParque parqueSeleccionado;
    LayoutInflater inflater;

    LinearLayout detalleContainer, tableDetalle;
    Button detalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        String rubro = getIntent().getStringExtra(EXTRA_RUBRO);
        ((TextView)findViewById(R.id.resultados_rubro)).setText(rubro);

        results = getIntent().getParcelableArrayListExtra(EXTRA_RESULTADOS);
        detalleContainer = (LinearLayout) findViewById(R.id.resultados_detalle_container);
        tableDetalle = (LinearLayout) findViewById(R.id.resultados_detalle_table);
        detalle = (Button) findViewById(R.id.resultados_detalle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.resultados_mapa);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        LatLngBounds.Builder bld = new LatLngBounds.Builder();
        for (AdminParque parque : results) {
            MarkerOptions opt = new MarkerOptions();
            opt.position(new LatLng(parque.getLat(), parque.getLon()));
            Marker mark = googleMap.addMarker(opt);
            bld.include(opt.getPosition());
            parquesUbicados.put(mark, parque);
        }

        final LatLngBounds bounds = bld.build();
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                parqueSeleccionado = parquesUbicados.get(marker);
                tableDetalle.removeAllViews();
  //              tableDetalle.removeAllViews();
                LinearLayout.LayoutParams lpTipo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lpTipo.setMargins(0,5,0,0);
                LinearLayout.LayoutParams lpMarMod = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lpMarMod.setMargins(0,0,0,5);
                for (AdminMaquina maquina : parqueSeleccionado.getItems()) {
                    Context context = ResultadosMap.this;

                    TextView tipo = new TextView(context);
                    tipo.setLayoutParams(lpTipo);
                    tipo.setTypeface(null, Typeface.BOLD);
                    tipo.setTextAppearance(context, android.R.style.TextAppearance_Large);
                    tipo.setText(maquina.getTipo().toUpperCase());
                    tableDetalle.addView(tipo);

                    if(!"".equalsIgnoreCase(maquina.getMarca())){
                        TextView marMod = new TextView(context);
                        marMod.setLayoutParams(lpMarMod);
                        marMod.setText("MARCA: " + maquina.getMarca());
                        marMod.setTextAppearance(context, android.R.style.TextAppearance_Medium);
                        if(!"".equalsIgnoreCase(maquina.getModelo())){
                            TextView modelo = new TextView(context);
                            marMod.setText( marMod.getText().toString() + " - MODELO: " + maquina.getModelo());
                        }
                        tableDetalle.addView(marMod);
                    }
                }

                detalle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ObtenerDetalleParqueMaquina().execute(parqueSeleccionado.getId());
                    }
                });

                changeInfoDetalleVisivility(View.VISIBLE);
                return false;
            }
        });
    }

    private class ObtenerDetalleParqueMaquina extends AsyncTask <Integer, Object, JSONObject>{

        @Override
        protected JSONObject doInBackground(Integer... params) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Params.URL_PARQUEMAQUINA_BUSCAR_DETALLE);
                try {
                    List nameValuePairs = new ArrayList();
                    nameValuePairs.add(new BasicNameValuePair("prestacionId", String.valueOf(params[0])));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity, "UTF-8");
                    return new JSONObject(responseString);
                }catch(Exception e){
                    return null;
                }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try{
                // Dueño del parque de maquina
                JSONObject jsonUser = jsonObject.getJSONObject("user");
                UsuarioDetalle user = new UsuarioDetalle();
                user.setUsuarioId(jsonUser.getInt("id"));
                user.setUsername(jsonUser.getString("username"));
                user.setTelefono(jsonUser.getString("telefono"));

                // Maquinas del parque de maquina
                JSONArray maquinasArray = jsonObject.getJSONArray("maquinas");
                ArrayList<MaquinaDetalle> maquinas = new ArrayList<>();
                for (int i = 0; i < maquinasArray.length(); i++){
                    JSONObject jsonMaquina = maquinasArray.getJSONObject(i);
                    MaquinaDetalle maq = new MaquinaDetalle();
                    maq.setId(jsonMaquina.getInt("id"));
                    maq.setTipo(jsonMaquina.getString("tipo"));
                    maq.setMarca(jsonMaquina.getString("marca"));
                    maq.setModelo(jsonMaquina.getString("modelo"));
                    // atributos e imagen
                    maquinas.add(maq);
                }

                // Parque de Maquina
                JSONObject jsonParque = jsonObject.getJSONObject("parque");
                ParqueDetalle parque = new ParqueDetalle(jsonParque.getString("rubro"), maquinas);
                parque.setId(jsonParque.getInt("id"));
                parque.setUsuario(user);
                parque.setEstado(jsonParque.getString("estado"));
                parque.setLat(jsonParque.getDouble("lat"));
                parque.setLon(jsonParque.getDouble("lon"));

                Intent intent = new Intent(ResultadosMap.this, DetalleResultado.class);
                intent.putExtra(Detalle.EXTRA_PARQUE, parque);
                // Haciendo esta forrada, porque android no pasa bien las maquinas dentro del parque, entre actividades
                intent.putParcelableArrayListExtra(Detalle.EXTRA_MAQUINAS_PARQUE, parque.getMaquinas());
                startActivity(intent);

            }catch(Exception e){

            }
        }
    }

    private void changeInfoDetalleVisivility(int visible) {
        detalleContainer.setVisibility(visible);
    }

}
