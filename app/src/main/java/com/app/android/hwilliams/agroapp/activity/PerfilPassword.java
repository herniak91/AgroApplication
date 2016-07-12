package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class PerfilPassword extends ActionBarActivity {

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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_password);
        usuario = (TextView) findViewById(R.id.perfilPass_usuario);
        password1 = (EditText) findViewById(R.id.perfilPass_pass1);
        password2 = (EditText) findViewById(R.id.perfilPass_pass2);
        showPassword = (CheckBox) findViewById(R.id.perfilPassword_showPass);
        finalizar = (Button) findViewById(R.id.perfilPass_final_btn);

        ImageButton profileImg = (ImageButton) findViewById(R.id.home_profileButton);
        profileImg.setClickable(false);
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

        requestCode = getIntent().getIntExtra("requestCode", 0);
        if (requestCode == USER_CREATION || requestCode == 0)
            setUpUserCreation();
        if (requestCode == USER_LOGIN)
            setUpUserLogin();
        if (requestCode == PASSWORD_CHANGE)
            setUpPasswordChange();

        editarUsuario = (ImageButton) findViewById(R.id.perfilPass_editUser);
        editarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario.setEnabled(!usuario.isEnabled());
            }
        });
        usuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    v.setEnabled(false);
                    checkUsernameAvailable();
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setUpUserCreation() {
        nombre = getIntent().getStringExtra("usuario");
        apellido = getIntent().getStringExtra("apellido");
        telefono = getIntent().getStringExtra("telefono");
        email = getIntent().getStringExtra("email");
        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordValid(password1.getText().toString())) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("nombre", nombre);
                        obj.put("apellido", apellido);
                        obj.put("telefono", telefono);
                        obj.put("email", email);
                        obj.put("username", usuario.getText().toString());
                        obj.put("password", password1.getText().toString());
                    } catch (Exception e) {
                        showErrorDialog("Error ingresando al sistema. Intentelo nuevamente mas tarde");
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
                if (isPasswordValid(password1.getText().toString())) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("username", usuario.getText().toString());
                        obj.put("password", password1.getText().toString());
                    } catch (Exception e) {
                        showErrorDialog("Error ingresando al sistema. Intentelo nuevamente mas tarde");
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
        nombre = getIntent().getStringExtra("usuario");
        apellido = getIntent().getStringExtra("apellido");
        telefono = getIntent().getStringExtra("telefono");
        email = getIntent().getStringExtra("email");
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

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        String username = getIntent().getStringExtra("usuario").trim().replaceAll("\\s", ".").toLowerCase();
        String apellido = getIntent().getStringExtra("apellido").trim().toLowerCase();
        usuario.setText(username + "." + apellido);
        usuario.setEnabled(false);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PerfilPassword Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.app.android.hwilliams.agroapp.activity/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PerfilPassword Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.app.android.hwilliams.agroapp.activity/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class AsyncPost extends AsyncTask<Map, Integer, JSONObject> {

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
                    saveProfileProperties(jsonObject);
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

            } catch (JSONException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PerfilPassword.this);
                builder.setMessage(errorMsg);
                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PerfilPassword.this.finishAffinity();
                    }
                });
                builder.show();
            }

        }
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

    private void saveProfileProperties(JSONObject json) throws JSONException {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        JSONObject response = (JSONObject) json.get("response");
        editor.putString(Params.PREF_USERNAME, response.getString("username"));
        editor.putString(Params.PREF_NOMBRE, response.getString("nombre"));
        editor.putString(Params.PREF_APELLIDO, response.getString("apellido"));
        editor.putString(Params.PREF_TEL, response.getString("telefono"));
        editor.putString(Params.PREF_EMAIL, response.getString("email"));
        editor.putString(Params.PREF_PASS, response.getString("password"));
        editor.commit();
    }

    private boolean isPasswordValid(String password) {
        return passwordsCheck() && checkPasswordLenght(password) && noSpecialChars(password);
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

    private void showErrorDialog(String message) {
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
