package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.app.android.hwilliams.agroapp.activity.superclass.Profile;
import com.app.android.hwilliams.agroapp.util.JsonPost;
import com.app.android.hwilliams.agroapp.util.Params;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfilPassword extends Profile {

    public static final Integer USER_CREATION = 1;
    public static final Integer USER_LOGIN = 2;
    public static final Integer PASSWORD_CHANGE = 3;

    EditText password1, password2;
    TextView usuario;
    CheckBox showPassword;
    Button finalizar;
    String nombre, apellido, email, telefono;
    ImageButton editarUsuario;
    Integer requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_password);
        /*ImageButton profileImg = (ImageButton) findViewById(R.id.home_profileButton);
        profileImg.setClickable(false);*/

        usuario = (TextView) findViewById(R.id.perfilPass_usuario);
        password1 = (EditText) findViewById(R.id.perfilPass_pass1);
        password2 = (EditText) findViewById(R.id.perfilPass_pass2);
        finalizar = (Button) findViewById(R.id.perfilPass_final_btn);

        showPassword = (CheckBox) findViewById(R.id.perfilPassword_showPass);
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    password1.setInputType(129);
                    password2.setInputType(129);
                }
            }
        });

        editarUsuario = (ImageButton) findViewById(R.id.perfilPass_editUser);
        editarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario.setEnabled(!usuario.isEnabled());
            }
        });

        requestCode = getIntent().getIntExtra("requestCode", 0);
        if (requestCode == USER_CREATION || requestCode == 0)
            setUpUserCreation();
        if (requestCode == USER_LOGIN)
            setUpUserLogin();
        if (requestCode == PASSWORD_CHANGE)
            setUpPasswordChange();
    }

    private void setUpUserCreation() {
        String usernameTmp = getIntent().getStringExtra("usuario").trim().replaceAll("\\s", ".").toLowerCase();
        String apellidoTmp = getIntent().getStringExtra("apellido").trim().toLowerCase();
        usuario.setText(usernameTmp + "." + apellidoTmp);
        usuario.setTypeface(usuario.getTypeface(), Typeface.BOLD);
        usuario.setEnabled(false);
        usuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    v.setEnabled(false);
                    checkUsernameAvailable();
                }
            }
        });

        /*nombre = getIntent().getStringExtra("usuario");
        apellido = getIntent().getStringExtra("apellido");
        telefono = getIntent().getStringExtra("telefono");
        email = getIntent().getStringExtra("email");*/
        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordValid(password1.getText().toString())) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("nombre", getIntent().getStringExtra("usuario"));
                        obj.put("apellido", getIntent().getStringExtra("apellido"));
                        obj.put("telefono", getIntent().getStringExtra("telefono"));
                        obj.put("email", getIntent().getStringExtra("email"));
                        obj.put("username", usuario.getText().toString());
                        obj.put("password", password1.getText().toString());
                    } catch (Exception e) {
                        showErrorDialog("No se puede acceder al sistema en este momento. Por favor, intentelo nuevamente mas tarde");
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(JsonPost.URL, Params.URL_PERFIL_CREAR);
                    map.put(JsonPost.JSON, obj.toString());
                    new AsyncPost().execute(map);
                }
            }
        });
    }

    private void setUpUserLogin() {
        editarUsuario.setVisibility(View.GONE);
        findViewById(R.id.text_confirmePassword).setVisibility(View.GONE);
        password2.setVisibility(View.GONE);
        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordValidForLogin(password1.getText().toString())) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("username", usuario.getText().toString());
                        obj.put("password", password1.getText().toString());
                    } catch (Exception e) {
                        showErrorDialog("No se puede acceder al sistema en este momento. Por favor, intentelo nuevamente mas tarde");
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(JsonPost.URL, Params.URL_PERFIL_LOGIN);
                    map.put(JsonPost.JSON, obj.toString());
                    new AsyncPost().execute(map);
                }
            }
        });
    }

    private void setUpPasswordChange() {
        editarUsuario.setVisibility(View.GONE);
        /*nombre = getIntent().getStringExtra("usuario");
        apellido = getIntent().getStringExtra("apellido");
        telefono = getIntent().getStringExtra("telefono");
        email = getIntent().getStringExtra("email");*/
        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordValid(password1.getText().toString())) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("username", usuario.getText().toString());
                        obj.put("password", password1.getText().toString());
                    } catch (Exception e) {
                        showErrorDialog("Error actualizando contraseña. Intentelo nuevamente mas tarde");
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(JsonPost.URL, Params.URL_PERFIL_ACTUALIZAR);
                    map.put(JsonPost.JSON, obj.toString());
                    new AsyncPost().execute(map);
                }
            }
        });
    }

    private void checkUsernameAvailable() {
        new CheckUsername().execute(usuario.getText().toString());
    }

    private class CheckUsername extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Params.URL_PERFIL_CHECK_USERNAME);
            try {
                List nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("username", params[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                return Integer.parseInt(responseString);
            } catch (Exception e) {
                return 2;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 1) {
                showToast("Usuario invalido. Por favor, ingrese otro");
                usuario.setEnabled(true);
            }
        }
    }

    private boolean isPasswordValid(String password) {
        return passwordsCheck() && checkPasswordLenght(password) && noSpecialChars(password);
    }

    private boolean isPasswordValidForLogin(String password){
        return checkPasswordLenght(password) && noSpecialChars(password);
    }

    private boolean checkPasswordLenght(String password) {
        if (password.length() > 7)
            return true;
        showToast("La contraseña debe tener al menos 8 caracteres");
        return false;
    }

    private boolean noSpecialChars(String password) {
        if (password.matches("[a-zA-Z0-9.? ]*"))
            return true;
        showToast("La contraseña debe estar formada por letras y numeros solamente");
        return false;
    }

    private boolean passwordsCheck() {
        if (!password1.getText().toString().matches(password2.getText().toString())) {
            showToast("Las contraseñas no coinciden");
            return false;
        }
        return true;
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}
