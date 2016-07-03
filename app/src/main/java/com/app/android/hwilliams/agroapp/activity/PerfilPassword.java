package com.app.android.hwilliams.agroapp.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.android.hwilliams.agroapp.R;

public class PerfilPassword extends ActionBarActivity {

    EditText password1, password2;
    TextView usuario;
    CheckBox showPassword;
    Button finalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_password);
        bindFields();
        ImageButton profileImg = (ImageButton) findViewById(R.id.home_profileButton);
        profileImg.setClickable(false);
        usuario.setText(getIntent().getStringExtra("usuario") + "." + getIntent().getStringExtra("apellido"));

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    password1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    password1.setInputType(129);
                    password2.setInputType(129);
                }
            }
        });

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fieldsCheck()){
                    showToast("Proceso de creacion de usuario iniciado");
                }
            }
        });
    }

    private void bindFields() {
        usuario = (TextView) findViewById(R.id.perfilPass_usuario);
        password1 = (EditText) findViewById(R.id.perfilPass_pass1);
        password2 = (EditText) findViewById(R.id.perfilPass_pass2);
        showPassword = (CheckBox) findViewById(R.id.perfilPassword_showPass);
        finalizar = (Button) findViewById(R.id.perfilPass_final_btn);
    }

    private boolean fieldsCheck() {
        if(!password1.getText().toString().matches(password2.getText().toString())){
            showToast("Las contraseñas no coinciden");
            return false;
        }
        return true;
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
