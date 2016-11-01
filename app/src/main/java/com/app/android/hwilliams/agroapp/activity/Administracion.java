package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.activity.detalle.MaquinaDetalle;
import com.app.android.hwilliams.agroapp.activity.detalle.ParqueDetalle;
import com.app.android.hwilliams.agroapp.activity.detalle.UsuarioDetalle;
import com.app.android.hwilliams.agroapp.admin.AdminListGroup;
import com.app.android.hwilliams.agroapp.admin.AdminMaquina;
import com.app.android.hwilliams.agroapp.admin.AdminParque;
import com.app.android.hwilliams.agroapp.util.Params;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Administracion extends Activity {
    public static final String EXTRA_GROUPS = "groups";

    private static final int CARGAR_NUEVO = 1;
    private static final int EDITAR_EXISTENTE = 2;

    ExpandableListView list;
    int previousGroup = -1;
    int cargaItemSelected = -1;
    List<AdminParque> parquesOnDisplay;
    LinearLayout administracionContent;
    ProgressBar administracionLoader;

    Button cargar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administracion);
        list = (ExpandableListView) findViewById(R.id.admin_expandableList);
        administracionContent = (LinearLayout) findViewById(R.id.administracion_content);
        administracionLoader = (ProgressBar) findViewById(R.id.administracion_loader);

        parquesOnDisplay = getIntent().getParcelableArrayListExtra(EXTRA_GROUPS);
        addParquesToDisplay(parquesOnDisplay);

        list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                // Collapse previous parent if expanded.
                if ((previousGroup != -1) && (groupPosition != previousGroup)) {
                    list.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(Administracion.this);
        // Set the dialog title
        builder.setTitle("Seleccione rubro a cargar")
                .setSingleChoiceItems(R.array.rubros, -1, null)
                .setPositiveButton("Cargar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        String[] some_array = getResources().getStringArray(R.array.rubros);
                        startActivityCargar(some_array[selectedPosition]);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        cargar = (Button) findViewById(R.id.admin_cargar);
        cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });
    }

    private void startActivityCargar(String rubro){
        Intent intent = new Intent(Administracion.this, Carga.class);
        intent.putExtra(Carga.EXTRA_RUBRO, sacarAcentos(rubro));
        intent.putParcelableArrayListExtra(Carga.EXTRA_OPCIONES_MAQUINA, getIntent().getParcelableArrayListExtra(Carga.EXTRA_OPCIONES_MAQUINA));
        intent.putParcelableArrayListExtra(Carga.EXTRA_ARQ_PARQUES, getIntent().getParcelableArrayListExtra(Carga.EXTRA_ARQ_PARQUES));
        startActivityForResult(intent, CARGAR_NUEVO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CARGAR_NUEVO && resultCode == RESULT_OK){
            List<AdminParque> parquesCreados = data.getParcelableArrayListExtra(DetalleCarga.EXTRA_PARQUE);
            parquesOnDisplay.addAll(parquesCreados);
            addParquesToDisplay(parquesOnDisplay);
        }
        if(requestCode == EDITAR_EXISTENTE){

        }
    }

    private String sacarAcentos(String input) {
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }
        return output;
    }

    private List<AdminListGroup> ordenarParquesPorRubro(List<AdminParque> parques) {
        List<AdminListGroup> groups = new ArrayList<>();
        List<String> rubros = new ArrayList<>();
        Map<String, List<AdminParque>> map = new HashMap<>();
        for (AdminParque parque : parques) {
            if(!map.containsKey(parque.getRubro())){
                rubros.add(parque.getRubro());
                map.put(parque.getRubro(), new ArrayList<AdminParque>());
            }

            map.get(parque.getRubro()).add(parque);
        }
        Collections.sort(rubros);
        for (String rubro: rubros) {
            groups.add(new AdminListGroup(rubro, map.get(rubro)));
        }
        return groups;
    }


    /**
     * Se muestran los parques que se pasan por parametro en el ExpandableViewList
     * @param parques
     */
    private void addParquesToDisplay(List<AdminParque> parques){
        List<AdminListGroup> groups = ordenarParquesPorRubro(parques);
        AdminExpandableListAdapter adapter = new AdminExpandableListAdapter(groups);
        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        list.setAdapter(adapter);
    }

    private class AdminExpandableListAdapter extends BaseExpandableListAdapter {

        private List<AdminListGroup> groups;

        private LayoutInflater inflater;

        public AdminExpandableListAdapter(List<AdminListGroup> groups){
            this.groups = groups;
        }

        public void setInflater(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = inflater.inflate(R.layout.admin_group_view, null);
            AdminListGroup g = groups.get(groupPosition);
            ((TextView)convertView.findViewById(R.id.admin_group_rubro)).setText(g.getRubro());
            ((TextView)convertView.findViewById(R.id.admin_group_cantidad)).setText(String.valueOf(g.getParques().size()));
            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.admin_item_view, null);
            }

            convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            final AdminParque p = groups.get(groupPosition).getParques().get(childPosition);
            LinearLayout view_container_maquinas = (LinearLayout) convertView.findViewById(R.id.admin_item_table);
            view_container_maquinas.removeAllViews();
            for (AdminMaquina m : p.getItems()) {
                GridLayout view_maquina = (GridLayout) inflater.inflate(R.layout.admin_item_view_parque, null);
                ((TextView)view_maquina.findViewById(R.id.admin_parque_tipo)).setText(m.getTipo().toUpperCase());
                ((TextView)view_maquina.findViewById(R.id.admin_parque_marca)).setText(m.getMarca());
                ((TextView)view_maquina.findViewById(R.id.admin_parque_modelo)).setText(m.getModelo());
                view_container_maquinas.addView(view_maquina);
            }

            ImageButton eliminar = (ImageButton) convertView.findViewById(R.id.admin_item_eliminar);
            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
          /*          administracionContent.setVisibility(View.GONE);
                    administracionLoader.setVisibility(View.VISIBLE);*/
                    new EliminarParqueMaquina().execute(p);
                }
            });

            ImageButton editar = (ImageButton) convertView.findViewById(R.id.admin_item_editar);
            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Administracion.this, Carga.class);
                    intent.putExtra(Carga.EXTRA_RUBRO, p.getRubro());
                    intent.putParcelableArrayListExtra(Carga.EXTRA_OPCIONES_MAQUINA, getIntent().getParcelableArrayListExtra(Carga.EXTRA_OPCIONES_MAQUINA));
                    intent.putParcelableArrayListExtra(Carga.EXTRA_ARQ_PARQUES, getIntent().getParcelableArrayListExtra(Carga.EXTRA_ARQ_PARQUES));
                    ParqueDetalle parque = crearParqueDetalle(p);
                    intent.putExtra(Carga.EXTRA_PARQUE_A_EDITAR, parque);
                    intent.putParcelableArrayListExtra(Carga.EXTRA_MAQUINAS_A_EDITAR, parque.getMaquinas());
                    startActivityForResult(intent, EDITAR_EXISTENTE);
                }
            });
            return convertView;
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return groups.get(groupPosition).getParques().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return groups.get(groupPosition).getParques().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private ParqueDetalle crearParqueDetalle(AdminParque p) {
            ArrayList<MaquinaDetalle> maquinas = new ArrayList<>();
            for (AdminMaquina maq : p.getItems()) {
                MaquinaDetalle maqDetalle = new MaquinaDetalle();
                maqDetalle.setId(maq.getId());
                maqDetalle.setTipo(maq.getTipo());
                maqDetalle.setMarca(maq.getMarca());
                maqDetalle.setModelo(maq.getModelo());
                maquinas.add(maqDetalle);
            }

            ParqueDetalle parque = new ParqueDetalle(p.getRubro(), maquinas);
            parque.setId(p.getId());
            parque.setUsuario(p.getUsuario());
            parque.setEstado(p.getEstado());
            parque.setLat(p.getLat());
            parque.setLon(p.getLon());
            return parque;
        }

    }

    private class EliminarParqueMaquina extends AsyncTask<AdminParque, Object, AdminParque> {
        @Override
        protected AdminParque doInBackground(AdminParque... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Params.URL_PARQUEMAQUINA_BORRAR);
            try {
                List nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("prestacionId", String.valueOf(params[0].getId())));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                if(Integer.parseInt(responseString) != 0)
                    return null;
            }catch(Exception e){
                return null;
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(AdminParque adminParque) {
            super.onPostExecute(adminParque);
            if(adminParque != null){
                parquesOnDisplay.remove(adminParque);
                addParquesToDisplay(parquesOnDisplay);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage("Ocurrio un error. Por favor, intentelo nuevamente mas tarde");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
            administracionLoader.setVisibility(View.GONE);
            administracionContent.setVisibility(View.VISIBLE);
        }
    }

}
