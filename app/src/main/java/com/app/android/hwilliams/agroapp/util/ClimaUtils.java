package com.app.android.hwilliams.agroapp.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.zetterstrom.com.forecast.models.DataPoint;
import android.zetterstrom.com.forecast.models.Icon;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        if(arr.length == 3)
            return String.valueOf(arr[2]) + "0%";
        if(arr.length == 4)
            return String.valueOf(arr[2]) + String.valueOf(arr[3]) + "%";
        return "";
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
            view.setText("Mínima: " + tempMin + " - Máxima : " + tempMax);
        }
    }

    public static void setUpDay(TextView dia, TextView diaTemp, Calendar cal, DataPoint day, ImageView icon, Map<String, Integer> iconosMap, Resources resources){
        dia.setText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US ));
        String min = ClimaUtils.getTemperatureValue(day.getTemperatureMin(), day.getApparentTemperatureMin());
        String max = ClimaUtils.getTemperatureValue(day.getTemperatureMax(), day.getApparentTemperatureMax());
        if (min.equalsIgnoreCase("") || max.equalsIgnoreCase("")){
            diaTemp.setVisibility(View.GONE);
        }else{
            diaTemp.setText(min + " - " + max);
        }
        if(iconosMap.get(day.getIcon().getText()) != null){
            Bitmap bMap = BitmapFactory.decodeResource(resources, iconosMap.get(day.getIcon().getText()));
            Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, icon.getWidth(), icon.getWidth(), true);
            icon.setImageBitmap(bMapScaled);
        }
    }

}
