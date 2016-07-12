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
import com.app.android.hwilliams.agroapp.util.Params;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class Perfil extends ActionBarActivity {

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
                if(fieldsCheck()){
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
                enableFields();
            }
        });

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
            showToast("Contraseña actualizada");
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

    private boolean fieldsCheck() {
        if("".matches(nombreText.getText().toString())){
            showToast("Es necesario ingresar un nombre");
            nombreText.requestFocus();
            return false;
        }
        if("".matches(apellidoText.getText().toString())){
            showToast("Es necesario ingresar un apellido");
            apellidoText.requestFocus();
            return false;
        }
        if("".matches(telefonoText.getText().toString())){
            showToast("Es necesario ingresar un teléfono");
            telefonoText.requestFocus();
            return false;
        }
        if( !"".matches(emailText.getText().toString()) && !isEmailValid(emailText.getText().toString()) ){
            showToast("Ingrese un email valido");
            emailText.requestFocus();
            return false;
        }
        return true;
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
        enableFields();
        siguienteBtn.setVisibility(View.VISIBLE);
    }

    private void enableFields(){
        nombreText.setEnabled(true);
        apellidoText.setEnabled(true);
        telefonoText.setEnabled(true);
        emailText.setEnabled(true);
    }

    private void showLoginView() {
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

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".") && !email.contains(" ");
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}
