package com.app.android.hwilliams.agroapp.activity;

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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class Perfil extends ActionBarActivity {

    EditText nombreText, apellidoText, telefonoText, emailText;
    Button siguienteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        bindFields();
        ImageButton profileImg = (ImageButton) findViewById(R.id.home_profileButton);
        profileImg.setClickable(false);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
        if (!sharedPref.contains("username")) {
            showLoginView();
        } else {
            populateFields(sharedPref);
        }

    }

    private void bindFields() {
        nombreText = (EditText) findViewById(R.id.perfil_nameText);
        apellidoText = (EditText) findViewById(R.id.perfil_apellidoText);
        telefonoText = (EditText) findViewById(R.id.perfil_telefonoText);
        emailText = (EditText) findViewById(R.id.perfil_emailText);
        siguienteBtn = (Button) findViewById(R.id.perfil_bottom_btn);
        siguienteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fieldsCheck()){
                    Intent intent = new Intent(Perfil.this, PerfilPassword.class);
                    intent.putExtra("usuario",nombreText.getText().toString());
                    intent.putExtra("apellido", apellidoText.getText().toString());
                    Perfil.this.startActivity(intent);
                }

            }
        });
    }

    private boolean fieldsCheck() {
        if("".matches(nombreText.getText().toString())){
            showToast("Es necesario ingresar un nombre");
            return false;
        }
        if("".matches(apellidoText.getText().toString())){
            showToast("Es necesario ingresar un apellido");
            return false;
        }
        if("".matches(telefonoText.getText().toString())){
            showToast("Es necesario ingresar un teléfono");
            return false;
        }
        return true;
    }

    private void populateFields(SharedPreferences sharedPref) {
        nombreText.setText(sharedPref.getString("usuarioNombre", ""));
        apellidoText.setText(sharedPref.getString("usuarioApellido", ""));
        telefonoText.setText(sharedPref.getString("usuarioTelefono", ""));
        emailText.setText(sharedPref.getString("usuarioEmail", ""));
    }

    private void showLoginView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login");
        builder.setNegativeButton("Crear Usuario", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Ingresar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToast("Login contra el server");
            }
        });
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
