package com.example.fire_and_based;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.WriterException;

public class QRCodeGeneratorIUTemp extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_generator); // Assuming you have an XML layout file named "activity_my"

        // Set an OnClickListener for the "scan_qr_button"
        findViewById(R.id.generate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qrCodeString = ((EditText)findViewById(R.id.qr_content)).getText().toString();

                Bitmap bitmap;
                try {
                    bitmap = QRCodeGenerator.QRImageFromString(qrCodeString, 400, 400);
                } catch (WriterException e){
                    Log.println(Log.ERROR, "QRCodeGenerator", "Failed to generate QR Code bitmap:" + e);
                    return;
                }

                ((ImageView)findViewById(R.id.qr_image_view)).setImageBitmap(bitmap);


            }
        });
    }
}
