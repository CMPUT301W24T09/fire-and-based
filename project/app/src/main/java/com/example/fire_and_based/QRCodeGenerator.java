package com.example.fire_and_based;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
/**
 * Logic for generating a qr code from an image and saving one to a file
 * Current issues: saving to an image not yet implemented
 * @author   Ilya
 */
public class QRCodeGenerator {

    private static final String charset = "UTF-8";

    public static Bitmap QRImageFromString(String data, int height, int width) throws WriterException {

        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, width, height);
        return bitmap;
    }

    public static void saveQRToFile(String data, int height, int width){
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

    }
}
