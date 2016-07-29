package com.app.android.hwilliams.agroapp.activity;

import android.app.Activity;
import android.os.Bundle;

import com.app.android.hwilliams.agroapp.R;

public class Detalle extends Activity {
    public static final String EXTRA_MAQUINAS = "parque";
    public static final String EXTRA_RUBRO = "rubro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
    }
}
