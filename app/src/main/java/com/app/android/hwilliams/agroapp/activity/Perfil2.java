package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.activity.superclass.Profile;
import com.app.android.hwilliams.agroapp.util.JsonPost;
import com.app.android.hwilliams.agroapp.util.Params;
import com.app.android.hwilliams.agroapp.util.PerfilUtils;

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

public class Perfil2 extends Activity {

    LinearLayout user_container, username_container, password_container;
    TextView nombre, apellido, telefono, email, username, password1, password2, password2Label;
    CheckBox passwordShowBox;
    ImageButton editUsername;
    Button editar, guardar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil2);

        user_container = (LinearLayout) findViewById(R.id.perfil_user_data_container);
        nombre = (TextView) findViewById(R.id.perfil2_nombre);
        apellido = (TextView) findViewById(R.id.perfil2_apellido);
        telefono = (TextView) findViewById(R.id.perfil2_telefono);
        email = (TextView) findViewById(R.id.perfil2_email);

        username_container = (LinearLayout) findViewById(R.id.perfil_username_container);
        username = (TextView) findViewById(R.id.perfil2_username);
        editUsername = (ImageButton) findViewById(R.id.perfil2_edit_username);
        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setEnabled(true);
            }
        });

        password_container = (LinearLayout) findViewById(R.id.perfil_password_container);
        password1 = (TextView) findViewById(R.id.perfil2_password1);
        password2 = (TextView) findViewById(R.id.perfil2_password2);
        password2Label =(TextView) findViewById(R.id.perfil2_password2Label);
        passwordShowBox = (CheckBox) findViewById(R.id.perfil2_password_show);
        passwordShowBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        editar = (Button) findViewById(R.id.perfil2_editar);
        guardar = (Button) findViewById(R.id.perfil2_guardar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isUserLoggedIn()){
            cargarInformacionUsuario();
        }else{
            showNewUserActions();
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPref = getPreferencesFile();
        return sharedPref.contains(Params.PREF_USERNAME);
    }

    /**
     * Cargar informacion de usuario y mostrar los contenedores de los campos
     */
    private void cargarInformacionUsuario() {
        SharedPreferences sharedPref = getPreferencesFile();
        nombre.setText(sharedPref.getString(Params.PREF_NOMBRE, ""));
        apellido.setText(sharedPref.getString(Params.PREF_APELLIDO, ""));
        telefono.setText(sharedPref.getString(Params.PREF_TEL, ""));
        email.setText(sharedPref.getString(Params.PREF_EMAIL, ""));
        username.setText(sharedPref.getString(Params.PREF_USERNAME, ""));

        user_container.setVisibility(View.VISIBLE);
        username_container.setVisibility(View.VISIBLE);
        password_container.setVisibility(View.GONE);

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpUserModification();
            }
        });
    }

    private SharedPreferences getPreferencesFile(){
        return getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
    }

    private void showNewUserActions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login");
        builder.setNegativeButton("Crear Usuario", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setUpUserCreation();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Ingresar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setUpLogin();
            }
        });
        builder.show();
    }

    private void setUpLogin(){
        user_container.setVisibility(View.GONE);
        username_container.setVisibility(View.VISIBLE);
        editUsername.setVisibility(View.GONE);
        password_container.setVisibility(View.VISIBLE);
        setpassword2Visivility(View.GONE);

        editar.setVisibility(View.GONE);
        guardar.setText("Ingresar");
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFieldsLoginValid()){
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("username", username.getText().toString());
                        obj.put("password", password1.getText().toString());
                    } catch (Exception e) {
                        showErrorDialog("No se puede acceder al sistema en este momento. Por favor, intentelo nuevamente mas tarde");
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(JsonPost.URL, Params.URL_PERFIL_LOGIN);
                    map.put(JsonPost.JSON, obj.toString());
                    new ProfilePost().execute(map);
                }

            }
        });
    }

    private boolean areFieldsUserCreationValid() {
        try{
            checkEmptyField(nombre, "Usuario");
            checkEmptyField(apellido, "Apellido");
            checkEmptyField(telefono, "Telefono");
            checkEmptyField(username, "Usuario");
            checkEmptyField(password1, "Contraseña");
            checkEmptyField(password2, "Contraseña");
        }catch(Exception e){
            return false;
        }
        boolean passwordValid = PerfilUtils.isPasswordValidForCreation(password1.getText().toString(), password2.getText().toString());
        boolean emailValid = PerfilUtils.isEmailValid(email.getText().toString());
        return passwordValid && emailValid;
    }

    private void checkEmptyField(TextView tv, String name){
        if(tv.getText().toString().isEmpty()){
            showToast(name + " no ingresado");
            tv.requestFocus();
            throw new RuntimeException();
        }
    }

    private void setpassword2Visivility(int visibility){
        password2.setVisibility(visibility);
        password2Label.setVisibility(visibility);
    }

    private boolean isFieldsLoginValid() {
        try{
            checkEmptyField(username, "Usuario");
            checkEmptyField(password1, "Contraseña");
        }catch(Exception e){
            return false;
        }
        return true;
    }

    private void setUpUserCreation(){
        nombre.setText("");
        apellido.setText("");
        telefono.setText("");
        email.setText("");
        username.setText("");
        password1.setText("");
        password2.setText("");

        user_container.setVisibility(View.VISIBLE);
        username_container.setVisibility(View.VISIBLE);
        password_container.setVisibility(View.VISIBLE);
        setpassword2Visivility(View.VISIBLE);
        guardar.setText("Ingresar");
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(areFieldsUserCreationValid()){
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("nombre", nombre.getText().toString());
                        obj.put("apellido", apellido.getText().toString());
                        obj.put("telefono", telefono.getText().toString());
                        obj.put("email", email.getText().toString());
                        obj.put("username", username.getText().toString());
                        obj.put("password", password1.getText().toString());
                    } catch (Exception e) {
                        showErrorDialog("No se puede acceder al sistema en este momento. Por favor, intentelo nuevamente mas tarde");
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(JsonPost.URL, Params.URL_PERFIL_CREAR);
                    map.put(JsonPost.JSON, obj.toString());
                    new ProfilePost().execute(map);
                }

            }
        });
    }

    private void setUpUserModification(){
        nombre.setEnabled(true);
        apellido.setEnabled(true);
        telefono.setEnabled(true);
        email.setEnabled(true);
        username.setEnabled(true);
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    v.setEnabled(false);
                    new CheckUsername().execute(username.getText().toString());
                }
            }
        });

        user_container.setVisibility(View.VISIBLE);
        username_container.setVisibility(View.VISIBLE);
        password_container.setVisibility(View.GONE);
    }

    private void fixUsername() {
        username.setEnabled(false);
    }




    /* CLASES PRIVADAS */

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
            }else{
                fixUsername();
            }
        }
    }

    private class ProfilePost extends AsyncTask<Map, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(Map... params) {
            return new JsonPost().postData((String) params[0].get(JsonPost.URL), (String) params[0].get(JsonPost.JSON));
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            String errorMsg = "No se puede realizar esta accion en este momento. Intentelo nuevamente mas tarde";
            try {
                if (jsonObject.getInt("code") != 0) {
                    errorMsg = jsonObject.getString("response");
                    throw new JSONException("");
                }else{
                    JSONObject response = jsonObject.getJSONObject("response");
                    saveUserProfile(response.getJSONObject("user") );
                    Intent returnIntent = new Intent();
                    returnIntent.putParcelableArrayListExtra(Administracion.EXTRA_GROUPS, (ArrayList<? extends Parcelable>) PerfilUtils.getAdminData(response.get("admin").toString()));
                    setResult(Activity.RESULT_OK, returnIntent);
         //           finish();
                }
            } catch (JSONException e) {
                showErrorDialog(errorMsg);
            }
        }
    }

    private void saveUserProfile(JSONObject response) throws JSONException{
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Params.PREF_USERNAME, response.getString("username"));
        editor.putString(Params.PREF_NOMBRE, response.getString("nombre"));
        editor.putString(Params.PREF_APELLIDO, response.getString("apellido"));
        editor.putString(Params.PREF_TEL, response.getString("telefono"));
        editor.putString(Params.PREF_EMAIL, response.getString("email"));
        editor.putString(Params.PREF_PASS, response.getString("password"));
        editor.commit();
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    protected void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message == null ? "Ocurrio un error. Por favor, intentelo nuevamente mas tarde" : message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
