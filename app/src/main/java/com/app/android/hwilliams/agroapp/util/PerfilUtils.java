package com.app.android.hwilliams.agroapp.util;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.app.android.hwilliams.agroapp.admin.AdminMaquina;
import com.app.android.hwilliams.agroapp.admin.AdminParque;
import com.app.android.hwilliams.agroapp.carga.parcelable.MaquinaParcelable;
import com.app.android.hwilliams.agroapp.carga.parcelable.MarcaParcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hernan on 7/23/2016.
 */
public class PerfilUtils {

    public static boolean fieldsCheck(TextView nombre, TextView apellido, TextView telefono, TextView email, Context context) {
        if("".matches(nombre.getText().toString())){
            CommonsUtils.showToast("Es necesario ingresar un nombre", context);
            nombre.requestFocus();
            return false;
        }
        if("".matches(apellido.getText().toString())){
            CommonsUtils.showToast("Es necesario ingresar un apellido", context);
            apellido.requestFocus();
            return false;
        }
        if("".matches(telefono.getText().toString())){
            CommonsUtils.showToast("Es necesario ingresar un teléfono", context);
            telefono.requestFocus();
            return false;
        }
        if( !"".matches(email.getText().toString()) && !isEmailValid(email.getText().toString()) ){
            CommonsUtils.showToast("Ingrese un email valido", context);
            email.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".") && !email.contains(" ");
    }

    public static List<AdminParque> getAdminData(String jsonAdmin) throws JSONException {
        List<AdminParque> groupList = new ArrayList<>();
        if( !(jsonAdmin == null || jsonAdmin.equalsIgnoreCase("null") || jsonAdmin.equalsIgnoreCase(""))){
            JSONArray list = new JSONArray(jsonAdmin);
            for (int i = 0; i < list.length(); i++){
                JSONObject obj = list.getJSONObject(i);
                JSONObject groupJson = obj.getJSONObject("parque");
                List<AdminMaquina> items = new ArrayList<>();
                JSONArray itemsJsonArray = obj.getJSONArray("maquinas");
                for (int a = 0; a < itemsJsonArray.length(); a++){
                    JSONObject maquinaJson = itemsJsonArray.getJSONObject(a);
                    items.add(new AdminMaquina(maquinaJson.getInt("id"), maquinaJson.getString("tipo"), maquinaJson.getString("marca"), maquinaJson.getString("modelo"), maquinaJson.getString("atributos")));
                }
                AdminParque groupObj = new AdminParque(groupJson.getInt("id"), groupJson.getString("estado"), groupJson.getString("rubro"), items);
                groupList.add(groupObj);
            }
        }
        return groupList;
    }

    public static boolean isPasswordValidForCreation(String password, String password2) {
        return passwordsCheck(password, password2) && checkPasswordLenght(password) && noSpecialChars(password);
    }

    private static boolean passwordsCheck(String password, String password2) {
        if (!password.matches(password2)) {
            return false;
        }
        return true;
    }

    public static boolean isPasswordValidForLogin(String password){
        return checkPasswordLenght(password) && noSpecialChars(password);
    }

    public static boolean checkPasswordLenght(String password) {
        if (password.length() > 7)
            return true;
        return false;
    }

    public static boolean noSpecialChars(String password) {
        if (password.matches("[a-zA-Z0-9.? ]*"))
            return true;
        return false;
    }

}
