package com.app.android.hwilliams.agroapp.util;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

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

    private static boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".") && !email.contains(" ");
    }

}
