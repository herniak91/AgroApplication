package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.activity.detalle.UsuarioDetalle;
import com.app.android.hwilliams.agroapp.activity.map.LocationSelector;
import com.app.android.hwilliams.agroapp.activity.map.ResultadosMap;
import com.app.android.hwilliams.agroapp.admin.AdminMaquina;
import com.app.android.hwilliams.agroapp.admin.AdminParque;
import com.app.android.hwilliams.agroapp.util.JsonPost;
import com.app.android.hwilliams.agroapp.util.Params;
import com.app.android.hwilliams.agroapp.util.PerfilUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Busqueda extends Activity {

    private static final int GET_LOCATION = 2;
    private static final int SHOW_RESULTS = 3;
    ListView listado;
    TextView ubicacion;
    Button buscar;
    Double lat = null;
    Double lon = null;
    String [] rubros;
    String rubroSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        listado = (ListView) findViewById(R.id.busqueda_opciones);
        listado.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listado.setItemsCanFocus(false);
        rubros= getResources().getStringArray(R.array.rubros);
        listado.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, rubros));

        ubicacion = (TextView) findViewById(R.id.busqueda_ubicacion);
        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationSelector.class);
                startActivityForResult(intent, GET_LOCATION);
            }
        });
        cambiarEstadoUbicacion(false);

        buscar = (Button) findViewById(R.id.busqueda_buscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try{
                    if(lat != null && lon != null){
                        json.put("lat", lat);
                        json.put("lon", lon);
                    }
                    JSONArray rubrosElegidos = new JSONArray();
                    int len = listado.getCount();
                    SparseBooleanArray checked = listado.getCheckedItemPositions();
                    if(checked.size() == 0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(Busqueda.this);
                        builder.setMessage("Debe seleccionar un rubro").show();
                        return;
                    }
                    for (int i = 0; i < len; i++){
                        if (checked.get(i)) {
                            rubroSeleccionado = rubros[i];
                            rubrosElegidos.put(rubros[i]);
                        }
                    }
                    json.put("rubros", rubrosElegidos);
                    new BuscarAsync().execute(json);
                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Busqueda.this);
                    builder.setMessage("No se puede realizar la busqueda en este momento. Por favor, intentelo de nuevo mas tarde").show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case GET_LOCATION : {
                if (resultCode == RESULT_OK) {
                    Bundle MBuddle = data.getExtras();
                    lat = MBuddle .getDouble("lat");
                    lon = MBuddle .getDouble("lon");
                    cambiarEstadoUbicacion(true);
                }else{
                    lat = null;
                    lon = null;
                    cambiarEstadoUbicacion(false);
                }
                break;
            }
        }

    }

    private void cambiarEstadoUbicacion(boolean selected){
        if(!selected){
            ubicacion.setText("Seleccione una ubicación");
            ubicacion.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            return;
        }
        ubicacion.setText("Ubicación elegida");
        ubicacion.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
    }

    private class BuscarAsync extends AsyncTask<JSONObject, JSONObject, JSONObject>{

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            return new JsonPost().postData(Params.URL_PARQUEMAQUINA_BUSCAR, params[0].toString());
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try{
                if(jsonObject.getInt("code") != 0)
                    throw new RuntimeException();
                JSONArray resultados = jsonObject.getJSONArray("response");
                List<AdminParque> parques = new ArrayList<>();
                for(int i = 0; i < resultados.length(); i++){
                    JSONObject obj = resultados.getJSONObject(i);
                    Integer id = obj.getInt("id");
                    String rubro = obj.getString("rubro");
                    String estado = obj.getString("estado");
                    List<AdminMaquina> maquinas = new ArrayList<>();
                    JSONArray maquinasJson = obj.getJSONArray("maquinas");
                    for (int j = 0; j < maquinasJson.length(); j++) {
                        JSONObject maqObj = maquinasJson.getJSONObject(j);
                        Integer idMaq = maqObj.getInt("id");
                        String tipoMaq = maqObj.getString("tipo");
                        String marcaMaq = maqObj.getString("marca");
                        String modeloMaq = maqObj.getString("modelo");
                        maquinas.add(new AdminMaquina(idMaq, tipoMaq, marcaMaq, modeloMaq, null));
                    }
                    AdminParque parque = new AdminParque(id, estado, rubro, maquinas);
                    parque.setLat(obj.getDouble("lat"));
                    parque.setLon(obj.getDouble("lon"));
                    UsuarioDetalle user = new UsuarioDetalle();
                    user.setUsuarioId(obj.getInt("usuarioId"));
                    parque.setUsuario(user);
                    parques.add(parque);
                }
                startResultados(rubroSeleccionado, parques);
            }catch (Exception e){
                showAlert("No se puede realizar la busqueda en este momento. Por favor, intentelo de nuevo más tarde.");
            }
        }
    }

    public void startResultados(String rubro, List<AdminParque> parques){
        Intent intent = new Intent(Busqueda.this, ResultadosMap.class);
        intent.putExtra(ResultadosMap.EXTRA_RUBRO, rubro);
        intent.putParcelableArrayListExtra(ResultadosMap.EXTRA_RESULTADOS, (ArrayList<? extends Parcelable>) parques);
        startActivityForResult(intent, SHOW_RESULTS);
    }

    public void showAlert(String str){
        AlertDialog.Builder error = new AlertDialog.Builder(Busqueda.this);
        error.setMessage(str).show();
    }
}
