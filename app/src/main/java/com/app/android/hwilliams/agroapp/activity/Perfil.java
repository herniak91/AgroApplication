package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.activity.superclass.Profile;
import com.app.android.hwilliams.agroapp.util.CommonsUtils;
import com.app.android.hwilliams.agroapp.util.JsonPost;
import com.app.android.hwilliams.agroapp.util.Params;
import com.app.android.hwilliams.agroapp.util.PerfilUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Perfil extends Profile {

    EditText nombreText, apellidoText, telefonoText, emailText;
    Button siguienteBtn, editar, cambiarPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        nombreText = (EditText) findViewById(R.id.perfil_nameText);
        apellidoText = (EditText) findViewById(R.id.perfil_apellidoText);
        telefonoText = (EditText) findViewById(R.id.perfil_telefonoText);
        emailText = (EditText) findViewById(R.id.perfil_emailText);
        siguienteBtn = (Button) findViewById(R.id.perfil_bottom_btn);
        siguienteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PerfilUtils.fieldsCheck(nombreText, apellidoText, telefonoText, emailText, getApplicationContext())){
                    Intent intent = new Intent(Perfil.this, PerfilPassword.class);
                    intent.putExtra("usuario",nombreText.getText().toString());
                    intent.putExtra("apellido", apellidoText.getText().toString());
                    intent.putExtra("telefono", telefonoText.getText().toString());
                    intent.putExtra("email", emailText.getText().toString());
                    Perfil.this.startActivityForResult(intent, PerfilPassword.USER_CREATION);
                }

            }
        });

        ImageButton profileImg = (ImageButton) findViewById(R.id.home_profileButton);
        profileImg.setClickable(false);

        editar = (Button) findViewById(R.id.perfil_editar);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!areFieldsEnabled()){
                    enableFields(true);
                    editar.setText("Finalizar");
                    cambiarPass.setVisibility(View.GONE);
                }else{
                    enableFields(false);
                    editar.setText("Editar");
                    cambiarPass.setVisibility(View.VISIBLE);
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("username", getUsername());
                        obj.put("nombre", nombreText.getText().toString());
                        obj.put("apellido", apellidoText.getText().toString());
                        obj.put("telefono", telefonoText.getText().toString());
                        obj.put("email", emailText.getText().toString());
                    } catch (Exception e) {
                        showErrorDialog("Error actualizando los datos. Intentelo nuevamente mas tarde");
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(JsonPost.URL, Params.URL_PERFIL_ACTUALIZAR);
                    map.put(JsonPost.JSON, obj.toString());
                    new AsyncPost().execute(map);
                }

            }
        });

        cambiarPass = (Button) findViewById(R.id.perfil_ChangePass);
        cambiarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
                Intent intent = new Intent(Perfil.this, PerfilPassword.class);
                intent.putExtra("username",sharedPref.getString(Params.PREF_USERNAME, "noUsername"));
                Perfil.this.startActivityForResult(intent, PerfilPassword.PASSWORD_CHANGE);
            }
        });
    }

    private boolean areFieldsEnabled() {
        return nombreText.isEnabled();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
        if (!sharedPref.contains(Params.PREF_USERNAME)) {
            showLoginView();
        }else{
            populateFields(sharedPref);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( (requestCode == PerfilPassword.USER_LOGIN || requestCode == PerfilPassword.USER_CREATION) && resultCode == Activity.RESULT_OK){
            populateFields(getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE));
        }
        if(requestCode == PerfilPassword.PASSWORD_CHANGE && resultCode == Activity.RESULT_OK){
            CommonsUtils.showToast("Contraseña actualizada", getApplicationContext());
        }
        if( resultCode != Activity.RESULT_OK){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Ocurrio un error. Por favor, intentelo nuevamente mas tarde");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    private void populateFields(SharedPreferences sharedPref) {
        siguienteBtn.setVisibility(View.GONE);
        nombreText.setText(sharedPref.getString(Params.PREF_NOMBRE, ""));
        nombreText.setEnabled(false);
        apellidoText.setText(sharedPref.getString(Params.PREF_APELLIDO, ""));
        apellidoText.setEnabled(false);
        telefonoText.setText(sharedPref.getString(Params.PREF_TEL, ""));
        telefonoText.setEnabled(false);
        emailText.setText(sharedPref.getString(Params.PREF_EMAIL, ""));
        emailText.setEnabled(false);
        editar.setVisibility(View.VISIBLE);
        cambiarPass.setVisibility(View.VISIBLE);
    }

    private void clearFields(){
        editar.setVisibility(View.GONE);
        cambiarPass.setVisibility(View.GONE);
        nombreText.setText("");
        apellidoText.setText("");
        telefonoText.setText("");
        emailText.setText("");
        enableFields(true);
        siguienteBtn.setVisibility(View.VISIBLE);
    }

    private void enableFields(boolean status){
        nombreText.setEnabled(status);
        apellidoText.setEnabled(status);
        telefonoText.setEnabled(status);
        emailText.setEnabled(status);
    }

    private void showLoginView() {
        editar.setVisibility(View.GONE);
        cambiarPass.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login");
        builder.setNegativeButton("Crear Usuario", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearFields();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Ingresar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
                Intent intent = new Intent(Perfil.this, PerfilPassword.class);
                dialog.dismiss();
                Perfil.this.startActivityForResult(intent, PerfilPassword.USER_LOGIN);
            }
        });
        builder.show();
    }

}
