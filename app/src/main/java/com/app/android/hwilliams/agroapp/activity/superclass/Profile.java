package com.app.android.hwilliams.agroapp.activity.superclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.util.JsonPost;
import com.app.android.hwilliams.agroapp.util.Params;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Profile extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected String getUsername(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
        return sharedPref.getString(Params.PREF_USERNAME, "");
    }

    protected void saveUserProfile(JSONObject response) throws JSONException{
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
                    saveUserProfile((JSONObject) jsonObject.get("response"));
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            } catch (JSONException e) {
                endActivityByError(errorMsg);
            }

        }
    }

    public void endActivityByError(String errorMsg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(errorMsg);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                Dialog dialog  = (Dialog) dialogInterface;
                Context context = dialog.getContext();
//                dialog.getOwnerActivity().finishAffinity();
            }

        });
        builder.show();
    }

}
