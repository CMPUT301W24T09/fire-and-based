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

    /**
     * Generates a QR code bitmap image from a string.
     *
     * @param data   The data to encode into the QR code.
     * @param height The height of the generated QR code bitmap.
     * @param width  The width of the generated QR code bitmap.
     * @return The QR code bitmap image.
     * @throws WriterException If an error occurs during encoding.
     */
    public static Bitmap QRImageFromString(String data, int height, int width) throws WriterException {

        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, width, height);
        return bitmap;
    }

    /**
     * Saves a QR code bitmap image to a file.
     *
     * @param data   The data to encode into the QR code.
     * @param height The height of the generated QR code bitmap.
     * @param width  The width of the generated QR code bitmap.
     */
    public static void saveQRToFile(String data, int height, int width){
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

    }


    static String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~%";

    /**
     * Returns a string containing only valid characters from the input string.
     *
     * @param input The input string to filter.
     * @return A string containing only valid characters from the input string, replacing invalid characters with '~'.
     */
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

    /**
     * Generates a random string containing valid characters.
     *
     * @return A random string containing valid characters.
     */
    public static String getValidString(){
        Random random = new Random();
        StringBuilder str = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            str.append(validChars.charAt(random.nextInt(validChars.length())));
        }
        return "fire_and_based_event_" + str;
    }


}
