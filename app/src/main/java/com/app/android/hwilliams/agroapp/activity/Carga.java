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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.carga.bean.CarroTolva;
import com.app.android.hwilliams.agroapp.carga.bean.MaquinaBasica;
import com.app.android.hwilliams.agroapp.carga.bean.Sembradora;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;
import com.app.android.hwilliams.agroapp.carga.parcelable.MarcaParcelable;

import java.util.ArrayList;
import java.util.List;

public class Carga extends Activity {
    public static final String EXTRA_RUBRO = "rubro";
    public static final String EXTRA_OPCIONES = "opciones";
    private final int VISTA_PREVIA = 1;

    TextView rubro;
    Button siguiente;
    LinearLayout container;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_2);
        rubro = (TextView) findViewById(R.id.carga_rubro);
        final List<MaquinaParcelable> groups = getGroups();

        container = (LinearLayout) findViewById(R.id.carga_container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (final MaquinaParcelable maquina : groups) {
            final View body = getMaquinaByTipo(getApplicationContext(), inflater, maquina);
            body.setVisibility(View.GONE);

            View header = inflater.inflate(R.layout.carga_group_view, null);
            final TextView tipo = (TextView)header.findViewById(R.id.carga_group_check_tipo);
            tipo.setText(maquina.getTipo());
            tipo.setEnabled(false);
            tipo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newVisivility = body.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                    if(newVisivility == View.VISIBLE)
                        hideAllViews();
                    body.setVisibility(newVisivility);
                }
            });
            CheckBox yes = (CheckBox) header.findViewById(R.id.carga_group_check_yes);
            yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        hideAllViews();
                        body.setVisibility(View.VISIBLE);
                        tipo.setEnabled(true);
                    }else{
                        body.setVisibility(View.GONE);
                        tipo.setEnabled(false);
                    }
                }
            });
            container.addView(header);
            container.addView(body);
        }

        siguiente = (Button) findViewById(R.id.carga_siguiente);
        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fieldCheck()){
                    startDetalleActivity();
                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Carga.this);
                    dialog.setTitle("Un momento!").setMessage("Alguna de la maquinaria que va a cargar no tiene información. ¿Desea continuar de cualquier manera?");
                    dialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDetalleActivity();
                        }
                    }).setNegativeButton("Revisar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

            }
        });
    }

    private MaquinaBasica getMaquinaByTipo(Context context, LayoutInflater inflater, final MaquinaParcelable maquina){
        if("sembradora".equalsIgnoreCase(maquina.getTipo()))
            return new Sembradora(context,inflater, maquina);
        if("carrroTolva".equalsIgnoreCase(maquina.getTipo()))
            return new CarroTolva(context,inflater, maquina);
        return new MaquinaBasica(getApplicationContext(), inflater, R.layout.carga_item_basico, maquina);
    }

    private boolean fieldCheck() {
        boolean anySelected = false;
        for(int i = 0; i < container.getChildCount(); i=i+2){
            LinearLayout header = (LinearLayout) container.getChildAt(i);
            CheckBox yes = (CheckBox) header.findViewById(R.id.carga_group_check_yes);
            if(yes.isChecked()){
                anySelected = true;
                MaquinaBasica maquina = (MaquinaBasica) container.getChildAt(i+1);
                if(maquina.isMissingInformation())
                    return false;
            }
        }
        if(!anySelected)
            return false;
        return true;
    }

    private void startDetalleActivity(){
        Intent intent = new Intent(Carga.this, Detalle.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            super.onBackPressed();
        }
    }

    private void hideAllViews(){
        for(int i = 1; i < container.getChildCount(); i=i+2){
            container.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private List<MaquinaParcelable> getGroups() {
        List<MaquinaParcelable> groups = new ArrayList<>();
        ArrayList<String> modelos1 = new ArrayList<>();
        modelos1.add("copado1");
        modelos1.add("copado 2");
        modelos1.add("super copado");
        MarcaParcelable marca1 = new MarcaParcelable("MarcaParcelable copada",modelos1);
        ArrayList<String> modelos2 = new ArrayList<>();
        modelos2.add("berreta1");
        modelos2.add("berreta 2");
        modelos2.add("super berreta");
        MarcaParcelable marca2 = new MarcaParcelable("MarcaParcelable berreta",modelos2);
        ArrayList<MarcaParcelable> marcas = new ArrayList<>();
        marcas.add(marca1);
        marcas.add(marca2);
        groups.add(new MaquinaParcelable("Tractor", marcas));

        ArrayList<String> modelos3 = new ArrayList<>();
        modelos3.add("copado1");
        modelos3.add("copado 2");
        modelos3.add("super copado");
        MarcaParcelable marca3 = new MarcaParcelable("MarcaParcelable copada",modelos3);
        ArrayList<String> modelos4 = new ArrayList<>();
        modelos4.add("berreta1");
        modelos4.add("berreta 2");
        modelos4.add("super berreta");
        MarcaParcelable marca4 = new MarcaParcelable("MarcaParcelable berreta",modelos4);
        ArrayList<MarcaParcelable> marcas2 = new ArrayList<>();
        marcas.add(marca3);
        marcas.add(marca4);
        groups.add(new MaquinaParcelable("Sembradora", marcas2));
        return groups;
    }

}
