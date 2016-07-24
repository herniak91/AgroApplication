package com.app.android.hwilliams.agroapp.admin;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.app.android.hwilliams.agroapp.R;

import java.util.List;

/**
 * Created by Hernan on 7/23/2016.
 */
public class AdminExpandableList extends BaseExpandableListAdapter {

    private List<AdminListGroup> groups;

    private Activity activity;
    private LayoutInflater inflater;

    public AdminExpandableList(List<AdminListGroup> groups){
        this.groups = groups;
    }

    public void setInflater(LayoutInflater inflater, Activity activity) {
        this.inflater = inflater;
        this.activity = activity;
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.admin_item_view, null);
        convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        AdminParque p = groups.get(groupPosition).getParques().get(childPosition);
        TableLayout table = (TableLayout) convertView.findViewById(R.id.admin_item_table);
        table.removeAllViews();
        for (AdminMaquina m : p.getItems()) {
            TableRow row = new TableRow(table.getContext());
            row.addView(createRowLabel(m.getTipo(), table.getContext()));
            row.addView(createRowLabel(m.getMarca(), table.getContext()));
            table.addView(row);
        }
        return convertView;
    }

    private TextView createRowLabel(String content, Context rowContext){
        TextView view = new TextView(rowContext);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
        view.setPadding(0,2,5,2);
        view.setText(content);
        return view;
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

}
