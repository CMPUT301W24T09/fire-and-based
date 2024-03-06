package com.example.fire_and_based;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.WriterException;

public class QRCodeViewer extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        /*
        Requires the intent to have a Bundle of the event name and qr code string
        Example call:
        Intent intent = new Intent(this, QRCodeViewer.class);
        Bundle extras = new Bundle();
        extras.putString("name", "Fun Event");
        extras.putString("code", "qwerty");
        intent.putExtras(extras);
        startActivity(intent);
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_qr); // Assuming you have an XML layout file named "activity_my"


        Button okButton = findViewById(R.id.view_qr_ok);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRCodeViewer.this, EventListActivity.class);
                startActivity(intent);
            }

        });



        // Retrieve the extras from the Intent
        Bundle receivedExtras = getIntent().getExtras();
        if (receivedExtras == null) {
            logError("Extras Bundle is null");
            return;
        }

        String name = receivedExtras.getString("name");
        String code = receivedExtras.getString("code");

        if (name != null){
            ((TextView)findViewById(R.id.qr_display_event_name_text)).setText(name);
        } else {
            logError("Event Name is null");
            return;
        }

        Bitmap bitmap = null;
        if (code != null){
            try {
                bitmap = QRCodeGenerator.QRImageFromString(code, 600, 600);
            } catch (WriterException e){
                logError("QR Generator encountered exception " + e);
            }
        } else {
            logError("QR Code data is null");
            return;
        }

        ((ImageView)findViewById(R.id.qr_display_qr_imageview)).setImageBitmap(bitmap);

    }

    private void logError(String error){
        Log.println(Log.ERROR, "QRCodeDisplay", error);
    }
}

