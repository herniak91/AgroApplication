package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.admin.AdminExpandableListAdapter;
import com.app.android.hwilliams.agroapp.admin.AdminListGroup;
import com.app.android.hwilliams.agroapp.admin.AdminParque;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Administracion extends Activity {
    public static final String EXTRA_GROUPS = "groups";

    ExpandableListView list;
    int previousGroup = -1;
    int cargaItemSelected = -1;

    Button cargar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administracion);
        list = (ExpandableListView) findViewById(R.id.admin_expandableList);

        List<AdminParque> parques = getIntent().getParcelableArrayListExtra(EXTRA_GROUPS);
        List<AdminListGroup> groups = ordenarParquesPorRubro(parques);
        AdminExpandableListAdapter adapter = new AdminExpandableListAdapter(groups);
        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        list.setAdapter(adapter);
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
        startActivity(intent);
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
}
