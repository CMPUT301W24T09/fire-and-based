package com.example.fire_and_based;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.WriterException;

public class TestQRCodeGenerator extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_qr_code_generator); // Assuming you have an XML layout file named "activity_my"

        // Set an OnClickListener for the "scan_qr_button"
        findViewById(R.id.generate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qrCodeString = ((EditText)findViewById(R.id.qr_content)).getText().toString();
                displayQR(qrCodeString, "Test Event Name lol");
            }
        });
    }

    private void displayQR(String content, String name){
        Intent intent = new Intent(this, QRCodeViewer.class);
        Bundle extras = new Bundle();
        extras.putString("name", name);
        extras.putString("code", content);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
