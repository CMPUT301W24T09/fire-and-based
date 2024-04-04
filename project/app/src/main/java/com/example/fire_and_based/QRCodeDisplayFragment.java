package com.example.fire_and_based;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.WriterException;

/**
 * Fragment for displaying a scannable QR Code
 */
public class QRCodeDisplayFragment extends DialogFragment {

    public Bitmap bitmap = null;

    public String eventName;
    public String EventQRCode;
    public String PosterQRCode;
    public int veryLightGray = Color.parseColor("#bfbbbb"); // Example of a very light gray, you can adjust the hex value as needed



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the fragment to use a full-screen dialog style
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    /**
     * After the view is created, set up the fragment's UI.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.qr_code_display_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView okButton = view.findViewById(R.id.view_qr_ok);
        okButton.setOnClickListener(v -> {
            dismiss(); // Use dismiss for dialog fragments instead of onBackPressed
        });

        MaterialButton eventQRButton = view.findViewById(R.id.Event_QR_Code);
        MaterialButton posterQRButton = view.findViewById(R.id.Poster_QR_Code);
        TextView eventNameTextView = view.findViewById(R.id.qr_display_event_name_text); // Assuming you have this TextView in your layout
        TextView qrDescription = view.findViewById(R.id.QRexplained);
        String eventQRText = "This QR is used for checking into the Event when scanned.";
        String posterQRText = "This QR is used to bring up the Event page when scanned.";
        // Corrected to use the same keys
        Bundle receivedArguments = getArguments();
        if (receivedArguments != null) {
            eventName = receivedArguments.getString("eventName");
            EventQRCode = receivedArguments.getString("eventQR");
            PosterQRCode = receivedArguments.getString("posterQR");

            // Display the event name if it's not null
            if (eventName == ""){
                eventNameTextView.setText("Scan QR Code");
            } else {
                eventNameTextView.setText(eventName);
            }


            if (EventQRCode != null) {
                try {
                    bitmap = QRCodeGenerator.QRImageFromString(EventQRCode, 600, 600);
                    ((ImageView) view.findViewById(R.id.qr_code_image_view)).setImageBitmap(bitmap);
                    int veryLightGray = Color.parseColor("#bfbbbb");
                    eventQRButton.setBackgroundTintList(ColorStateList.valueOf(veryLightGray));
                    eventNameTextView.setText("Event QR");
                    qrDescription.setText(eventQRText);

                } catch (WriterException e) {
                    logError("QR Generator encountered exception " + e);
                }
            } else {
                logError("QR Code data is null");
            }
        } else {
            logError("Arguments Bundle is null");
        }



        eventQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bitmap = QRCodeGenerator.QRImageFromString(EventQRCode, 600, 600);
                    ((ImageView) view.findViewById(R.id.qr_code_image_view)).setImageBitmap(bitmap);
                    int veryLightGray = Color.parseColor("#bfbbbb");
                    int white = Color.parseColor("#ffffff");
                    eventQRButton.setBackgroundTintList(ColorStateList.valueOf(veryLightGray));
                    eventNameTextView.setText("Event QR");
                    posterQRButton.setBackgroundTintList(ColorStateList.valueOf(white));
                    qrDescription.setText(eventQRText);

                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        posterQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bitmap = QRCodeGenerator.QRImageFromString(PosterQRCode, 600, 600);
                    ((ImageView) view.findViewById(R.id.qr_code_image_view)).setImageBitmap(bitmap);

                    int veryLightGray = Color.parseColor("#bfbbbb");
                    int white = Color.parseColor("#ffffff");
                    eventQRButton.setBackgroundTintList(ColorStateList.valueOf(white));
                    eventNameTextView.setText("Poster QR");
                    posterQRButton.setBackgroundTintList(ColorStateList.valueOf(veryLightGray));
                    qrDescription.setText(posterQRText);

                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
            }
        });



    }



    /**
     * Logs an error encountered by this class.
     *
     * @param error The string of the error to be logged
     */
    private void logError(String error) {
        if (getContext() != null) {
            Log.println(Log.ERROR, "QRCodeDisplayFragment", error);
        }
    }
}
