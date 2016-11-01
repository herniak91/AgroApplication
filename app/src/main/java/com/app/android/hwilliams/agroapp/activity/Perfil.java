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

public class Perfil extends Activity {

    LinearLayout user_data_container, username_container, password_confirm_container, login_action_container, action_container;

    TextView nombre, apellido, telefono, email, username, password1, password2;
    CheckBox passwordShowBox;
    ImageButton editUsername;
    Button editarButton, guardarButton, salirButton, login, signUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        user_data_container = (LinearLayout) findViewById(R.id.perfil_user_data_container);
        nombre = (TextView) findViewById(R.id.perfil2_nombre);
        apellido = (TextView) findViewById(R.id.perfil2_apellido);
        telefono = (TextView) findViewById(R.id.perfil2_telefono);
        email = (TextView) findViewById(R.id.perfil2_email);

        username_container = (LinearLayout) findViewById(R.id.perfil_username_container);
        username = (TextView) findViewById(R.id.perfil2_username);
        password1 = (TextView) findViewById(R.id.perfil2_password1);

        password_confirm_container = (LinearLayout) findViewById(R.id.perfil_password_container);
        password2 = (TextView) findViewById(R.id.perfil2_password2);
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

        login_action_container = (LinearLayout) findViewById(R.id.perfil_password_login_action);
        login = (Button) findViewById(R.id.login_action);
        login.setOnClickListener(new View.OnClickListener() {
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

        signUp = (Button) findViewById(R.id.signUp_action);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpUserCreation();
                showUserCreation();
                login.setVisibility(View.GONE);
            }
        });

        action_container = (LinearLayout) findViewById(R.id.perfil_password_actions);

        editarButton = (Button) findViewById(R.id.perfil2_editar);
        guardarButton = (Button) findViewById(R.id.perfil2_guardar);
        salirButton = (Button) findViewById(R.id.perfil2_logout);
        salirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Perfil.this);
                builder.setTitle("Salir del sistema");
                builder.setMessage("Podra acceder solo a ciertas partes de la aplicacion. Si quiere utilizarla en su totalidad, debera ingresar al sistema nuevamente");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getPreferencesFile().edit();
                        editor.remove(Params.PREF_USERNAME);
                        editor.remove(Params.PREF_PASS);
                        editor.remove(Params.PREF_NOMBRE);
                        editor.remove(Params.PREF_APELLIDO);
                        editor.remove(Params.PREF_TEL);
                        editor.remove(Params.PREF_EMAIL);
                        editor.apply();
                        clearFields();
                        displayOnStartActions();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayOnStartActions();
    }

    private void displayOnStartActions(){
        if(isUserLoggedIn()){
            cargarInformacionUsuario();
            showUserLoggedIn();
        }else{
            // accion por default
            showUserNotLoggedIn();
        }
    }

    private void showUserNotLoggedIn(){
        user_data_container.setVisibility(View.GONE);
        username_container.setVisibility(View.VISIBLE);
        password_confirm_container.setVisibility(View.GONE);
        login_action_container.setVisibility(View.VISIBLE);
        action_container.setVisibility(View.GONE);
    }

    private void showUserLoggedIn(){
        user_data_container.setVisibility(View.VISIBLE);
        username_container.setVisibility(View.VISIBLE);
        password_confirm_container.setVisibility(View.GONE);
        login_action_container.setVisibility(View.GONE);
        action_container.setVisibility(View.GONE);
    }

    private void showUserCreation(){
        user_data_container.setVisibility(View.VISIBLE);
        username_container.setVisibility(View.VISIBLE);
        password_confirm_container.setVisibility(View.VISIBLE);
        login_action_container.setVisibility(View.VISIBLE);
        action_container.setVisibility(View.GONE);
    }

    private void clearFields(){
        nombre.setText("");
        apellido.setText("");
        telefono.setText("");
        email.setText("");
        username.setText("");
        password1.setText("");
        password2.setText("");
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

        nombre.setEnabled(false);
        apellido.setEnabled(false);
        telefono.setEnabled(false);
        email.setEnabled(false);
        username.setEnabled(false);

        editarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpUserModification();
            }
        });
    }

    private SharedPreferences getPreferencesFile(){
        return getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
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
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !"".equalsIgnoreCase(username.getText().toString())) {
                    v.setEnabled(false);
                    new CheckUsername().execute(username.getText().toString());
                }
            }
        });
        password1.setText("");
        password2.setText("");

        guardarButton.setText("Crear Usuario");
        guardarButton.setOnClickListener(new View.OnClickListener() {
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
        username.setEnabled(false);

        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(areFieldsUserEditValid()){
                    SharedPreferences sharedPref = getPreferencesFile();
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("nombre", nombre.getText().toString());
                        obj.put("apellido", apellido.getText().toString());
                        obj.put("telefono", telefono.getText().toString());
                        obj.put("email", email.getText().toString());
                        obj.put("username", username.getText().toString());
                        obj.put("password", sharedPref.getString(Params.PREF_PASS, ""));
                    } catch (Exception e) {
                        showErrorDialog("No se puede acceder al sistema en este momento. Por favor, intentelo nuevamente mas tarde");
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put(JsonPost.URL, Params.URL_PERFIL_ACTUALIZAR);
                    map.put(JsonPost.JSON, obj.toString());
                    new ActualizarUserInfo().execute(map);
                }
            }
        });
    }

    private boolean areFieldsUserEditValid() {
        try{
            checkEmptyField(nombre, "Usuario");
            checkEmptyField(apellido, "Apellido");
            checkEmptyField(telefono, "Telefono");
            checkEmptyField(username, "Usuario");
        }catch(Exception e){
            return false;
        }
        boolean emailValid = PerfilUtils.isEmailValid(email.getText().toString());
        return emailValid;
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
                    refreshInformationAfterPost();
         //           finish();
                }
            } catch (JSONException e) {
                showErrorDialog(errorMsg);
            }
        }
    }

    private class ActualizarUserInfo extends AsyncTask<Map, Integer, JSONObject> {
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
                    refreshInformationAfterPost();
                }
            } catch (JSONException e) {
                showErrorDialog(errorMsg);
            }
        }
    }



    private void refreshInformationAfterPost() {
        displayOnStartActions();
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
