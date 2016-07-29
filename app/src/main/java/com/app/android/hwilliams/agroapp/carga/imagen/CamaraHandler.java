package com.app.android.hwilliams.agroapp.carga.imagen;

import android.hardware.Camera;

/**
 * Created by Hernan on 7/28/2016.
 */
public class CamaraHandler implements Camera.PictureCallback {
    private byte[] picture;

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        picture = data;
    }

    public byte[] getPicture() {
        return picture;
    }
}
