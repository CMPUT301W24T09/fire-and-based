package com.example.fire_and_based;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Fragment for displaying a scannable QR Code
 * @author Tyler
 */
public class QRCodeDisplayFragment extends DialogFragment {

    public Bitmap bitmap = null;
    public String eventName;
    public String EventQRCode;
    public String PosterQRCode;
    public int veryLightGray = Color.parseColor("#bfbbbb"); // Example of a very light gray, you can adjust the hex value as needed

    /**
     * Configures the fragment to use a full-screen dialog style.
     *
     * @param savedInstanceState The saved instance state of the fragment.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the fragment to use a full-screen dialog style
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    /**
     * After the view is created, set up the fragment's UI.
     * Inflates the layout for the QR code display fragment.
     *
     * @param inflater           LayoutInflater object for inflating views.
     * @param container          Parent view to which the fragment's UI should be attached.
     * @param savedInstanceState Previously saved state, if any.
     * @return The root view of the inflated layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.qr_code_display_fragment, container, false);
    }

    /**
     * Initializes the views and handles actions for the QR code display fragment.
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     */
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


        MaterialButton saveImageButton = view.findViewById(R.id.save_image_button);
        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    saveImageToGallery(getContext(), bitmap, eventName + " " + eventNameTextView.getText().toString());
                } else {
                    logError("Bitmap is null. Cannot save image.");
                }
            }
        });


        ImageView qrCodeImage = view.findViewById(R.id.qr_code_image_view);
        MaterialButton shareImageButton = view.findViewById(R.id.share_qr_code_button);
        shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Assuming qrCodeImage is your ImageView from which you're getting the Bitmap
                try {
                    BitmapDrawable drawable = (BitmapDrawable) qrCodeImage.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    // Your existing code to save and share the bitmap...
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error sharing image", Toast.LENGTH_SHORT).show();
                }

                // Saving the Bitmap to cache directory
                File cachePath = new File(getContext().getCacheDir(), "images");
                cachePath.mkdirs();
                File file = new File(cachePath, "image_to_share.png");
                try (FileOutputStream stream = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Getting URI for the saved file using FileProvider
                Uri photoURI = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);

                if (photoURI != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Temporary permission for receiving app to read this file
                    shareIntent.setType(getContext().getContentResolver().getType(photoURI)); // Correctly setting the type based on the URI
                    shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI); // Using photoURI here
                    startActivity(Intent.createChooser(shareIntent, "Choose an app"));
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


    /**
     * Saves a bitmap image with text to the gallery.
     *
     * @param context The context used to access the content resolver.
     * @param bitmap  The bitmap image to be saved.
     * @param text    The text to be drawn on the bitmap image.
     */
    private void saveImageToGallery(Context context, Bitmap bitmap, String text) {
        // Create a mutable bitmap to draw text on
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        // Create a Canvas object to draw on the bitmap
        Canvas canvas = new Canvas(mutableBitmap);

        // Create a Paint object to set the text color, size, etc.
        Paint paint = new Paint();
        paint.setColor(Color.BLACK); // Text color
        paint.setTextSize(40); // Text size
        paint.setAntiAlias(true); // Smooth edges

        // Calculate text position (centered horizontally, near the top vertically)
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (canvas.getWidth() - bounds.width()) / 2;
        int y = bounds.height() + 20; // Adjust as needed

        // Draw the text on the canvas
        canvas.drawText(text, x, y, paint);

        // Save the modified bitmap to the gallery
        // (The rest of the code remains the same)

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "QRCode");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if (outputStream != null) {
                outputStream.close();
                // Notify the user that the image has been saved successfully
                Toast.makeText(context, "Image saved to Gallery", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            logError("Failed to save image: " + e.getMessage());
        }
    }

}
