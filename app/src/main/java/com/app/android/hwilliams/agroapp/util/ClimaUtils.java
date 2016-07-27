package com.app.android.hwilliams.agroapp.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.zetterstrom.com.forecast.models.DataPoint;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Hernan on 7/23/2016.
 */
public class ClimaUtils {
    private static final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
    private static final int CLIMA_MINUTOS_INTERVALO = 10;
    private static DecimalFormat twoDForm = new DecimalFormat("#.##");

    public static void setUpTabsLayout(TabHost tabHost) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tabHost.getTabWidget().getChildTabViewAt(0).setLayoutParams(params);
        tabHost.getTabWidget().getChildTabViewAt(1).setLayoutParams(params);
        tabHost.getTabWidget().getChildTabViewAt(2).setLayoutParams(params);
    }

    public static boolean isTimeToAskWheather(long nextTimeToCheck) {
        Date now = new Date();
        if(now.getTime() > nextTimeToCheck){
            nextTimeToCheck = new Date(now.getTime() + (CLIMA_MINUTOS_INTERVALO * ONE_MINUTE_IN_MILLIS)).getTime();
            return true;
        }
        return false;
    }

    public static String getTemperatureValue(Object primary, Object secondary){
        String tempStr = getNotNullField(primary, secondary);
        if(tempStr.equalsIgnoreCase(""))
            return tempStr;
        return String.valueOf(Math.round(Double.parseDouble(tempStr))) + "º";
    }

    public static String getHumedad(Double humidity){
        String h = getNotNullField(humidity, null);
        if(h.equalsIgnoreCase(""))
            return h;
        String d = twoDForm.format(Double.parseDouble(h));
        char[] arr = d.toCharArray();
        return String.valueOf(arr[2]) + String.valueOf(arr[3]) + "%";
    }

    public static String getWindSpeedKmH(Double windSpeed){
        String ms = getNotNullField(windSpeed, null);
        if(ms.equalsIgnoreCase(""))
            return ms;
        return String.valueOf(Math.round(Double.parseDouble(ms)));
    }

    public static String getWindDirection(Double degreesDouble){
        String deegres = getNotNullField(degreesDouble, null);
        if(deegres.equalsIgnoreCase(""))
            return deegres;
        int number = (int) Math.round(Double.parseDouble(deegres));
        if(number > 0 || number < 0)
            return "S";
        if(number >= 0 || number <= 0)
            return "SO";
        if(number > 0 || number < 0)
            return "O";
        if(number >= 0 || number <= 0)
            return "NO";
        if(number > 0 || number < 0)
            return "N";
        if(number >= 0 || number <= 0)
            return "NE";
        if(number > 0 || number < 0)
            return "E";
        if(number >= 0 || number <= 0)
            return "SE";
        return "";
    }

    public static int getDaysDiff(int today, int next){
        if(next - today >= 0)
            return next - today;
        return next + 7 - today;
    }

    private static String getNotNullField(Object primary, Object secondary){
        if(primary != null){
            return primary.toString();
        }else{
            if(secondary != null){
                return secondary.toString();
            }else{
                return"";
            }
        }
    }

    public static void checkTempMinMaxVisibility(TextView view, String tempMin, String tempMax) {
        if (tempMin.equalsIgnoreCase("") || tempMax.equalsIgnoreCase("")){
            view.setVisibility(View.GONE);
        }else{
            view.setText(tempMin + " - " + tempMax);
        }
    }

    public static void setUpDay(TextView dia1Dia, TextView diaTemp, Calendar cal, DataPoint day){
        dia1Dia.setText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US ));
        String min = ClimaUtils.getTemperatureValue(day.getTemperatureMin(), day.getApparentTemperatureMin());
        String max = ClimaUtils.getTemperatureValue(day.getTemperatureMax(), day.getApparentTemperatureMax());
        ClimaUtils.checkTempMinMaxVisibility(diaTemp, min, max);
    }
}
