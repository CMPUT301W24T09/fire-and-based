package com.example.fire_and_based;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.ScanContract;

public class EventCheckIn extends AppCompatActivity {

    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(EventCheckIn.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EventCheckIn.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_check_in); // Assuming you have an XML layout file named "activity_my"

        // Set an OnClickListener for the "scan_qr_button"
        findViewById(R.id.scan_qr_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQRScanner(); // Call the method to launch the barcode scanner
            }
        });
    }

    // Method to launch the barcode scanner
    private void launchQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);

        // Launch the barcode scanner
        qrLauncher.launch(options);
    }
}