package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.admin.AdminExpandableList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administracion);
        list = (ExpandableListView) findViewById(R.id.admin_expandableList);

        List<AdminParque> parques = getIntent().getParcelableArrayListExtra(EXTRA_GROUPS);
        List<AdminListGroup> groups = organizarParques(parques);
        AdminExpandableList adapter = new AdminExpandableList(groups);
        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

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
    }

    private List<AdminListGroup> organizarParques(List<AdminParque> parques) {
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
