package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;
import android.zetterstrom.com.forecast.models.DataBlock;
import android.zetterstrom.com.forecast.models.DataPoint;
import android.zetterstrom.com.forecast.models.Forecast;
import android.zetterstrom.com.forecast.models.Unit;

import com.app.android.hwilliams.agroapp.BuildConfig;
import com.app.android.hwilliams.agroapp.R;
import com.app.android.hwilliams.agroapp.admin.AdminParque;
import com.app.android.hwilliams.agroapp.util.ClimaUtils;
import com.app.android.hwilliams.agroapp.util.CotizacionesUtils;
import com.app.android.hwilliams.agroapp.util.Params;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Activity  implements LocationListener{
    public static final String EXTRA_COTIZACIONES = "cotizaciones";
    public static final String EXTRA_MERCADOS = "mercados";
    public static final String EXTRA_DOLAR = "dolar";

    public static final int ACTION_SETTINGS = 4;
    public static final int ACTION_PERFIL = 5;

    private long NEXT_TIME_TO_CKECK = 0;
    private boolean WAS_USER_ASKED = false;
    private LocationManager mLocationManager;
    private Location userLocation;

    List<AdminParque> parquesUsuarios;

    Button buscar_btn,admin_btn;
    TabHost tabHost;
    //Cotizaciones
    Map<String, List<TableRow>> rowMap = new HashMap<>();
    // Clima
    TextView climaActualTemp, climaActualTempAvrg, climaActualViento, climaActualDirViento, homeClimaUpdate, dia1Dia, dia1Temp, dia2Dia, dia2Temp, dia3Dia, dia3Temp, climaUbicacion;
    ImageView climaActualIcon, dia1Icon, dia2Icon, dia3Icon;
    private ProgressBar climaLoader;

    private Map<String, Integer> iconosMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        iconosMap.put("clear-day", R.drawable.clear_day);
        iconosMap.put("clear-night", R.drawable.clear_night);
        iconosMap.put("cloudy", R.drawable.cloudy);
        iconosMap.put("fog", R.drawable.fog);
        iconosMap.put("hail", R.drawable.hail);
        iconosMap.put("partly-cloudy-day", R.drawable.partly_cloudy_day);
        iconosMap.put("partly-cloudy-night", R.drawable.partly_cloudy_night);
        iconosMap.put("rain", R.drawable.rain);
        iconosMap.put("sleet", R.drawable.sleet);
        iconosMap.put("thunderstorm", R.drawable.thunderstorm);
        iconosMap.put("tornado", R.drawable.tornado);
        iconosMap.put("wind", R.drawable.wind);

        // Iniciar tabs
        tabHost = (TabHost) findViewById(R.id.home_tabHost);
        tabHost.setup();
        setUpTabClima();
        setUpTabCotizaciones();
        setUpTabInformacion();
        ClimaUtils.setUpTabsLayout(tabHost);

        buscar_btn = (Button) findViewById(R.id.home_buscar_btn);
        buscar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Busqueda.class);
                Home.this.startActivity(intent);
            }
        });

        parquesUsuarios = getIntent().getParcelableArrayListExtra(Administracion.EXTRA_GROUPS);
        admin_btn = (Button) findViewById(R.id.home_admin_btn);
        admin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Administracion.class);
                intent.putParcelableArrayListExtra(Administracion.EXTRA_GROUPS, (ArrayList<? extends Parcelable>) getParquesUsuarios());
                intent.putParcelableArrayListExtra(Carga.EXTRA_OPCIONES_MAQUINA, getIntent().getParcelableArrayListExtra(Carga.EXTRA_OPCIONES_MAQUINA));
                intent.putParcelableArrayListExtra(Carga.EXTRA_ARQ_PARQUES, getIntent().getParcelableArrayListExtra(Carga.EXTRA_ARQ_PARQUES));
                Home.this.startActivity(intent);
            }
        });

        ImageButton profileImg = (ImageButton) findViewById(R.id.home_profileButton);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Perfil.class);
                startActivityForResult(intent, ACTION_PERFIL);
            }
        });

        climaLoader = (ProgressBar) findViewById(R.id.homeClima_loader);
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, this);
        ForecastConfiguration configuration =
                new ForecastConfiguration.Builder(BuildConfig.API_KEY)
                        .setDefaultUnit(Unit.CA)
                        .setCacheDirectory(getCacheDir())
                        .build();
        ForecastClient.create(configuration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
        boolean isUserLoggedIn = sharedPref.contains(Params.PREF_USERNAME);
        buscar_btn.setEnabled(isUserLoggedIn);
        admin_btn.setEnabled(isUserLoggedIn);
        if(!WAS_USER_ASKED && !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            askUserForSettings();
        }
        actualizarInfoClima();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ACTION_SETTINGS : {
                WAS_USER_ASKED = true;
                break;
            }
            case ACTION_PERFIL : {
                if(data != null && resultCode == RESULT_OK){
                    parquesUsuarios = data.getParcelableArrayListExtra(Administracion.EXTRA_GROUPS);
                }
                break;
            }
        }
    }

    public List<AdminParque> getParquesUsuarios() {
        return parquesUsuarios;
    }

    private void actualizarInfoClima(){
        if(ClimaUtils.isTimeToAskWheather(NEXT_TIME_TO_CKECK)){
            homeClimaUpdate.setVisibility(View.GONE);
            climaLoader.setVisibility(View.VISIBLE);
            Location currentLocation = getLocation();
            ForecastClient.getInstance().getForecast(currentLocation.getLatitude(),currentLocation.getLongitude(), new Callback<Forecast>() {
                @Override
                public void onResponse(Call<Forecast> forecastCall, Response<Forecast> response) {
                    if (response.isSuccessful()) {
                        showClimaInfoOk(response.body());
                    } else {
                        showClimaInfoError();
                    }
                    climaLoader.setVisibility(View.GONE);
                    homeClimaUpdate.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<Forecast> forecastCall, Throwable t) {
                    showClimaInfoError();
                    climaLoader.setVisibility(View.GONE);
                    homeClimaUpdate.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private Location getLocation(){
        if(userLocation != null){
            climaUbicacion.setText("Ubicación actual");
            return userLocation;
        }
        climaUbicacion.setText("Alberti, Buenos Aires");
        return getDefaultLocation();
    }

    private void askUserForSettings(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this);
        dialog.setTitle("Su ubicación no pudo ser obtenida");
        dialog.setMessage("Modifique su configuración de ubicación o utilice la ubicación por defecto (Alberti, Buenos Aires)");
        dialog.setPositiveButton("Cambiar configuración", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(myIntent, ACTION_SETTINGS);
                paramDialogInterface.dismiss();
            }
        });
        dialog.setNegativeButton("Ignorar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                WAS_USER_ASKED = true;
            }
        });
        dialog.show();
    }

    public void showClimaInfoOk(Forecast forecast){
        // Informacion del momento
        DataPoint current = forecast.getCurrently();
        Date update = new Date(current.getTime().getTime());
        DateFormat df = new SimpleDateFormat("HH:mm");
        homeClimaUpdate.setText("Ultima actualización a las " + df.format(update));
        climaActualTemp.setText(ClimaUtils.getTemperatureValue(current.getTemperature(), current.getApparentTemperature()));
        climaActualViento.setText(ClimaUtils.getWindSpeedKmH(current.getWindSpeed()) + " Km/h");
        climaActualDirViento.setText(" " + ClimaUtils.getWindDirection(current.getWindBearing()));
        // TODO conseguir icono
        if(iconosMap.get(current.getIcon().getText()) != null){
            Bitmap bMap = BitmapFactory.decodeResource(getResources(), iconosMap.get(current.getIcon().getText()));
            Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, climaActualTemp.getHeight(), climaActualTemp.getHeight(), true);
            climaActualIcon.setImageBitmap(bMapScaled);
        }

        // Dias siguientes
        DataBlock week = forecast.getDaily();
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_WEEK);
        for (DataPoint day : week.getDataPoints()) {
            cal.setTime(day.getTime());
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            switch (ClimaUtils.getDaysDiff(today, dayOfWeek)){
                case 0:
                    String min = ClimaUtils.getTemperatureValue(day.getTemperatureMin(), day.getApparentTemperatureMin());
                    String max = ClimaUtils.getTemperatureValue(day.getTemperatureMax(), day.getApparentTemperatureMax());
                    ClimaUtils.checkTempMinMaxVisibility(climaActualTempAvrg, min, max);
                    break;
                case 1:
                    ClimaUtils.setUpDay(dia1Dia, dia1Temp, cal, day, dia1Icon, iconosMap, getResources());
                    break;
                case 2:
                    ClimaUtils.setUpDay(dia2Dia, dia2Temp, cal, day, dia2Icon, iconosMap, getResources());
                    break;
                case 3:
                    ClimaUtils.setUpDay(dia3Dia, dia3Temp, cal, day, dia3Icon, iconosMap, getResources());
                    break;
                default:
                    break;
            }
        }
    }

    public void showClimaInfoError(){
        showToast("Error clima");
    }

    private void setUpTabClima() {
        TabHost.TabSpec tabClima = tabHost.newTabSpec("Clima");
        tabClima.setIndicator("Clima");
        tabClima.setContent(R.id.home_tabClima);
        tabHost.addTab(tabClima);
        homeClimaUpdate = (TextView) findViewById(R.id.homeClima_timeUpdate);
        climaActualIcon = (ImageView) findViewById(R.id.homeClima_img);
        climaActualTemp = (TextView) findViewById(R.id.homeClima_temp);
        climaActualTempAvrg = (TextView) findViewById(R.id.homeClima_tempAvrg);
        climaActualViento = (TextView) findViewById(R.id.homeClima_viento);
        climaActualDirViento = (TextView) findViewById(R.id.homeClima_vientoDir);
        climaUbicacion = (TextView) findViewById(R.id.homeClima_ubicacion);

        dia1Dia = (TextView) findViewById(R.id.homeClima_day1_day);
        dia1Temp = (TextView) findViewById(R.id.homeClima_day1_temp);
        dia1Icon = (ImageView) findViewById(R.id.homeClima_day1_img);
        dia2Dia = (TextView) findViewById(R.id.homeClima_day2_day);
        dia2Temp = (TextView) findViewById(R.id.homeClima_day2_temp);
        dia2Icon = (ImageView) findViewById(R.id.homeClima_day2_img);
        dia3Dia = (TextView) findViewById(R.id.homeClima_day3_day);
        dia3Temp = (TextView) findViewById(R.id.homeClima_day3_temp);
        dia3Icon = (ImageView) findViewById(R.id.homeClima_day3_img);
    }

    private void setUpTabCotizaciones() {
        TabHost.TabSpec tabCotizacion = tabHost.newTabSpec("Cotizaciones");
        tabCotizacion.setIndicator("Cotización \n del cereal");
        tabCotizacion.setContent(R.id.home_tabCotizacion);
        tabHost.addTab(tabCotizacion);

        // Cotizacion del dolar
        TextView compraDolar = (TextView) findViewById(R.id.home_dolarCompra);
        TextView ventaDolar = (TextView) findViewById(R.id.home_dolarVenta);
        CotizacionesUtils.setCotizacionDolar(compraDolar, ventaDolar, getIntent().getStringExtra(Home.EXTRA_DOLAR));

        // Opciones de mercados y sus cotizaciones
        LinearLayout containerCotizaciones = (LinearLayout) findViewById(R.id.cotizaciones_container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CotizacionesUtils.populateRowsCotizaciones(containerCotizaciones, getIntent().getStringExtra(Home.EXTRA_COTIZACIONES), inflater, Home.this);
    }

    private void setUpTabInformacion() {
        TabHost.TabSpec tabInfo = tabHost.newTabSpec("Información");
        tabInfo.setIndicator("Precios \norientativos");
        tabInfo.setContent(R.id.home_tabInfo);
        tabHost.addTab(tabInfo);

        LinearLayout containerInformacion = (LinearLayout) findViewById(R.id.home_tabInfo);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String jsonInfo = "[{\"nombre\":\"soja\",\"qqhaes\":[{\"precio\":1001.0,\"qqha\":\"20\"},{\"precio\":1146.0,\"qqha\":\"30\"},{\"precio\":1340.0,\"qqha\":\"40\"},{\"precio\":1615.0,\"qqha\":\"50\"}]},{\"nombre\":\"trigo\",\"qqhaes\":[{\"precio\":801.3,\"qqha\":\"20\"},{\"precio\":918.2,\"qqha\":\"30\"},{\"precio\":1085.1,\"qqha\":\"40\"}]},{\"nombre\":\"maiz\",\"qqhaes\":[{\"precio\":1248.0,\"qqha\":\"50\"},{\"precio\":1403.0,\"qqha\":\"70\"},{\"precio\":1601.0,\"qqha\":\"90\"},{\"precio\":1866.0,\"qqha\":\"110\"},{\"precio\":2235.0,\"qqha\":\"130\"}]}]";
        CotizacionesUtils.populateRowsInformacion(containerInformacion, jsonInfo, inflater,Home.this);
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    // Location Listener
    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        userLocation = getDefaultLocation();
    }

    private Location getDefaultLocation() {
        // defaulLocation = Alberti
        Location defaultLocation = new Location(LocationManager.NETWORK_PROVIDER);
        defaultLocation.setLatitude(-35.0322875);
        defaultLocation.setLongitude(-60.2725113);
//        climaUbicacion.setText("Alberti, Buenos Aires");
        return defaultLocation;
    }

}
