package com.app.android.hwilliams.agroapp.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.android.hwilliams.agroapp.util.Params;
import com.app.android.hwilliams.agroapp.R;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Home extends FragmentActivity {
    Button buscar_btn,admin_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FragmentManager fragManager = getFragmentManager();

        buscar_btn = (Button) findViewById(R.id.home_buscar_btn);
        buscar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Busqueda.class);
                Home.this.startActivity(intent);
            }
        });


        admin_btn = (Button) findViewById(R.id.home_admin_btn);
        admin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Administracion.class);
                Home.this.startActivity(intent);
            }
        });

        ImageButton profileImg = (ImageButton) findViewById(R.id.home_profileButton);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Perfil.class);
                Home.this.startActivity(intent);
            }
        });

        try {
            new HomeRequest().execute(new URL(Params.URL_APP + "Home/getInfo"));
        } catch (MalformedURLException e) {
            showToast("Error: " + e.getMessage());
        }

    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private class HomeRequest extends AsyncTask <URL, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(URL... params) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) params[0].openConnection();
            } catch (IOException e) {
                showToast("Error: " + e.getMessage());
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            showToast("Got home data");
        }
    }
}
