package com.example.fire_and_based;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Random;

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


    static String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~%";
    public static String getValidChars(String input){
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (validChars.indexOf(c) >= 0) {
                result.append(c);
            } else {
                result.append('~');
            }
        }
        return result.toString();
    }
    public static String getValidString(){
        Random random = new Random();
        StringBuilder str = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            str.append(validChars.charAt(random.nextInt(validChars.length())));
        }
        return "fire_and_based_event_" + str;
    }


}
