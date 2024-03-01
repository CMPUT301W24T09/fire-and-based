package com.example.fire_and_based;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeInterface {

    private static final String charset = "UTF-8";

    public static Bitmap createQR(String data, int height, int width) throws WriterException {

        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.encodeBitmap("content", BarcodeFormat.QR_CODE, 400, 400);
        return bitmap;
    }
}
