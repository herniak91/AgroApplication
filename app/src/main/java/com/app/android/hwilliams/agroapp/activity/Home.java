package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
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
import com.app.android.hwilliams.agroapp.util.ClimaUtils;
import com.app.android.hwilliams.agroapp.util.CotizacionesUtils;
import com.app.android.hwilliams.agroapp.util.Params;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Activity {
    public static final String EXTRA_COTIZACIONES = "cotizaciones";
    public static final String EXTRA_MERCADOS = "mercados";
    public static final String EXTRA_DOLAR = "dolar";

    private long NEXT_TIME_TO_CKECK = 0;
    private LocationManager mLocationManager;
    Location currentLocation;

    Button buscar_btn,admin_btn;
    TabHost tabHost;
    //Cotizaciones
    Spinner mercadosOpt;
    Map<String, List<TableRow>> rowMap = new HashMap<>();
    // Clima
    TextView climaActualTemp, climaActualTempAvrg, climaActualViento, climaActualDirViento, climaActualHumedad, homeClimaUpdate;
    ImageView climaActualIcon;
    private ProgressBar climaLoader;

    TextView dia1Dia, dia1Temp, dia2Dia, dia2Temp, dia3Dia, dia3Temp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Iniciar tabs
        tabHost = (TabHost) findViewById(R.id.home_tabHost);
        tabHost.setup();
        setUpTabClima();
        setUpTabCotizaciones();
        setUpTabInformacion();
        setUpTabsLayout();


        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_file_name), Context.MODE_PRIVATE);
        boolean isUserLoggedIn = sharedPref.contains(Params.PREF_USERNAME);
        buscar_btn = (Button) findViewById(R.id.home_buscar_btn);
        buscar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Busqueda.class);
                Home.this.startActivity(intent);
            }
        });
        buscar_btn.setEnabled(isUserLoggedIn);

        admin_btn = (Button) findViewById(R.id.home_admin_btn);
        admin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Administracion.class);
                intent.putParcelableArrayListExtra(Administracion.EXTRA_GROUPS, getIntent().getParcelableArrayListExtra(Administracion.EXTRA_GROUPS));
                Home.this.startActivity(intent);
            }
        });
        admin_btn.setEnabled(isUserLoggedIn);

        ImageButton profileImg = (ImageButton) findViewById(R.id.home_profileButton);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Perfil.class);
                Home.this.startActivity(intent);
            }
        });

        climaLoader = (ProgressBar) findViewById(R.id.homeClima_loader);
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
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
        if(ClimaUtils.isTimeToAskWheather(NEXT_TIME_TO_CKECK)){
            homeClimaUpdate.setVisibility(View.GONE);
            climaLoader.setVisibility(View.VISIBLE);
            currentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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

    public void showClimaInfoOk(Forecast forecast){
        // Informacion del momento
        DataPoint current = forecast.getCurrently();
        Date update = new Date(current.getTime().getTime());
        DateFormat df = new SimpleDateFormat("HH:mm");
        homeClimaUpdate.setText(df.format(update));
        climaActualTemp.setText(ClimaUtils.getTemperatureValue(current.getTemperature(), current.getApparentTemperature()));
        String min = ClimaUtils.getTemperatureValue(current.getTemperatureMin(), current.getApparentTemperatureMin());
        String max = ClimaUtils.getTemperatureValue(current.getTemperatureMax(), current.getApparentTemperatureMax());
        ClimaUtils.checkTempMinMaxVisibility(climaActualTempAvrg, min, max);
        climaActualViento.setText(ClimaUtils.getWindSpeedKmH(current.getWindSpeed()) + " Km/h");
        climaActualDirViento.setText(" " + ClimaUtils.getWindDirection(current.getWindBearing()));
        climaActualHumedad.setText(ClimaUtils.getHumedad(current.getHumidity()));
        // TODO conseguir icono


        // Dias siguientes
        DataBlock week = forecast.getDaily();
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_WEEK);
        for (DataPoint day : week.getDataPoints()) {
            cal.setTime(day.getTime());
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            switch (ClimaUtils.getDaysDiff(today, dayOfWeek)){
                case 1:
                    ClimaUtils.setUpDay(dia1Dia, dia1Temp, cal, day);
                    break;
                case 2:
                    ClimaUtils.setUpDay(dia2Dia, dia2Temp, cal, day);
                    break;
                case 3:
                    ClimaUtils.setUpDay(dia3Dia, dia3Temp, cal, day);
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
        climaActualHumedad = (TextView) findViewById(R.id.homeClima_humedad);

        dia1Dia = (TextView) findViewById(R.id.homeClima_day1_day);
        dia1Temp = (TextView) findViewById(R.id.homeClima_day1_temp);
        dia2Dia = (TextView) findViewById(R.id.homeClima_day2_day);
        dia2Temp = (TextView) findViewById(R.id.homeClima_day2_temp);
        dia3Dia = (TextView) findViewById(R.id.homeClima_day3_day);
        dia3Temp = (TextView) findViewById(R.id.homeClima_day3_temp);
    }

    private void setUpTabCotizaciones() {
        TabHost.TabSpec tabCotizacion = tabHost.newTabSpec("Cotizaciones");
        tabCotizacion.setIndicator("Cotizaciones");
        tabCotizacion.setContent(R.id.home_tabCotizacion);
        tabHost.addTab(tabCotizacion);

        // Cotizacion del dolar
        TextView compraDolar = (TextView) findViewById(R.id.home_dolarCompra);
        TextView ventaDolar = (TextView) findViewById(R.id.home_dolarVenta);
        CotizacionesUtils.setCotizacionDolar(compraDolar, ventaDolar, getIntent().getStringExtra(Home.EXTRA_DOLAR));

        // Opciones de mercados y sus cotizaciones
        TableLayout tableCotizaciones = (TableLayout) findViewById(R.id.home_tablaCotizacion);
        CotizacionesUtils.populateRows(tableCotizaciones, getIntent().getStringExtra(Home.EXTRA_COTIZACIONES), rowMap);

        mercadosOpt = (Spinner) findViewById(R.id.spinner_mercados);
        List<String> mercadosList = CotizacionesUtils.getListaMercados(getIntent().getStringExtra(Home.EXTRA_MERCADOS));
        mercadosOpt.setAdapter(new ArrayAdapter<String>(Home.this, R.layout.support_simple_spinner_dropdown_item, mercadosList));
        mercadosOpt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                for (Map.Entry<String, List<TableRow>> entry: rowMap.entrySet()) {
                    int visibility = entry.getKey().equalsIgnoreCase(selected) ? View.VISIBLE : View.GONE;
                    for (TableRow row: entry.getValue()) {
                        row.setVisibility(visibility);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Never happens
            }
        });
    }

    private void setUpTabInformacion() {
        TabHost.TabSpec tabInfo = tabHost.newTabSpec("Información");
        tabInfo.setIndicator("Información");
        tabInfo.setContent(R.id.home_tabInfo);
        tabHost.addTab(tabInfo);
    }

    private void setUpTabsLayout() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tabHost.getTabWidget().getChildTabViewAt(0).setLayoutParams(params);
        tabHost.getTabWidget().getChildTabViewAt(1).setLayoutParams(params);
        tabHost.getTabWidget().getChildTabViewAt(2).setLayoutParams(params);
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
