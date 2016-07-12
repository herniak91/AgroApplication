package com.app.android.hwilliams.agroapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.util.Params;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;

public class ApplicationLoader extends ActionBarActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_loader);
        progressBar = (ProgressBar) findViewById(R.id.appLoader_progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isConnected = isConnected();
        if(isConnected){
            progressBar.setVisibility(View.VISIBLE);
            new CheckServerAvailable().execute();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sin conexión");
            builder.setMessage("Conecte su dispositivo a Internet e intentelo nuevamente");
            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ApplicationLoader.this.finishAffinity();
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null)
            return false;
        return true;
    }

    private void startApplication() {
        Intent intent = new Intent(this, Home.class);
        this.startActivity(intent);
    }

    private class CheckServerAvailable extends AsyncTask<Object,Integer,Integer>{

        @Override
        protected Integer doInBackground(Object... params) {
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection  = 5000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection );
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(Params.URL_APP);
            try {
                httpclient.execute(httppost);
                return 0;
            } catch (Exception e) {
                return 1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer.equals(0)){
                progressBar.setVisibility(View.GONE);
                startApplication();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationLoader.this);
                builder.setTitle("Problema de conexión");
                builder.setMessage("Lamentablemente la aplicacion no puede conectarse en este momento. Intentelo nuevamente mas tarde");
                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ApplicationLoader.this.finishAffinity();
                    }
                });
                builder.show();
            }

        }
    }
}
