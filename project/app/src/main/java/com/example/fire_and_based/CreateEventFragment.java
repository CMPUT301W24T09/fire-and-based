package com.example.fire_and_based;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

/**
 * This class is the activity for creating a new event.
 * It can be accessed by clicking the create event button on UserActivity.
 * @author Tyler, Ilya, Sumayya
 * To-do:
 * 1. AIDEN: run the app and click the blue plus in middle of screen -> you see the white rectangle at the top, its an imageview, i want you to set an onclick for image uploading there
 * 2. ILYA: run the app and see the two QR code buttons, there is zero code for them and no id's either go to create_event_and_edit.xml and connect those buttons to the qr code upload / generate functionality
 * 3. Not attached to activity when you press create event, app crashes
 * 4. Need to allow creation of event without making a poster
 */
public class CreateEventFragment extends Fragment {
    private User user;
    public Date newEventDate;

    private String QRCode = null;
    private String imageUrl = null;

    public String timeString;
    public String dateString;
    public Long maxAttendeeLong;


    // private ImageView previewBanner;
    private Uri imageUri;
    Context context = getContext();

    StorageReference fireRef = FirebaseStorage.getInstance().getReference();

//    ActivityResultLauncher<Intent> customActivityResultLauncher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                    new ActivityResultCallback<ActivityResult>() {
//        @Override
//        public void onActivityResult(ActivityResult result)
//        {//if (result.getResultCode() == RESULT_OK)
//            try {
//                if (result.getData() != null)
//                {
//                    imageUri = result.getData().getData();
//                    //buttonUpload.setEnabled(true);
//                    Glide.with(context).load(imageUri).into(previewBanner);
//                }
//            }
//            catch(Exception e)
//            {Toast.makeText(requireContext(), "Please Select An Image", Toast.LENGTH_LONG).show();}
//        }
//    });



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event_fragment, container, false);

        NavigationBarView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.setVisibility(View.GONE);

        // get user being passed into activity


        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }

        // get all textfields
        EditText eventName = view.findViewById(R.id.event_name_editable);
        EditText eventDescription = view.findViewById(R.id.event_description_editable);
        EditText eventDate = view.findViewById(R.id.event_date_editable); // Make sure to replace 'your_date_button_id' with the actual ID of your button in the layout
        EditText eventTime = view.findViewById(R.id.event_time_editable); // Initialize it as per your actual layout component
        EditText eventLocation = view.findViewById(R.id.event_location_editable);
        EditText eventMaxAttendees = view.findViewById(R.id.event_maximum_attendees_editable);


        // set textfield to the current date ( for making things more obvious and easier for them to click )
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        dateString = String.format("%d %d %d", mDay, mMonth + 1, mYear);
        eventDate.setText(String.format("%d %d %d", mDay, mMonth + 1, mYear)); // Adding 1 to month as Calendar.MONTH is zero-based


        // onclick for the event date that opens a calendar ui for them to select a date
        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        (view, year, monthOfYear, dayOfMonth) -> {
                            dateString = String.format(dayOfMonth + " " + (monthOfYear + 1) + " " + year);
                            eventDate.setText(dayOfMonth + " " + (monthOfYear + 1) + " " + year);
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        // Onclick for the event time to open a clock ui for them to select a time
        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour = c.get(Calendar.HOUR_OF_DAY); // current hour
                int mMinute = c.get(Calendar.MINUTE); // current minute

                // Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                        (view, hourOfDay, minute) -> {
                            String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                            timeString = formattedTime;
                            eventTime.setText(formattedTime);
                        }, mHour, mMinute, false); // 'false' for 12-hour clock or 'true' for 24-hour clock
                timePickerDialog.show();
            }
        });


        // Cancel Button in the top left -> goes back to browse IDK where else to go kekw - seems ok for now
        TextView cancelButton = view.findViewById(R.id.cancel_event_creation);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });


        //TODO The QR Code viewer class got deleted, up to you if/how you want to display it
//        previewQRImage = findViewById(R.id.create_event_preview_qr_image);
//        previewQRImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (QRCode != null){
//                    displayQR(QRCode, eventName.getText().toString());
//                } else {
//                    toast("ERROR: QR Code not set");
//                }
//            }
//        });

        Button generateQRButton = view.findViewById(R.id.generateQR);
        generateQRButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                QRCode = QRCodeGenerator.getValidString();

                //TODO add a thing that previews the QR Code string? at least for debug?
                Toast.makeText(requireContext(), "Random QR Code Successfully Generated", Toast.LENGTH_SHORT).show();

                //showQRString.setText(getString(R.string.qr_code_display).replace("%s", qrCode));
            }
        });
        Button reuseQRButton = view.findViewById(R.id.reuseQR);
        reuseQRButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                launchQRScanner();
            }
        });

        ImageView imageButton = view.findViewById(R.id.add_banner_button);
        ImageView previewBanner = view.findViewById(R.id.roundedImageView);


        ActivityResultLauncher<Intent> customActivityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                    @Override
            public void onActivityResult(ActivityResult result)
            {//if (result.getResultCode() == RESULT_OK)
                try
                {
                    if (result.getData() != null)
                    {
                        imageUri = result.getData().getData();
                        previewBanner.setImageURI(imageUri);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;


                        InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                        BitmapFactory.decodeStream(inputStream, null, options);
                        if (inputStream != null) {
                            inputStream.close();}
                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;
                        Log.d("ImageDimensions", "Image Height: " + imageHeight);
                        Log.d("ImageDimensions", "Image Width: " + imageWidth);
                        //EVENT BANNERS SHOULD BE 640 x 480 pixels MINIMUM
                        int imageRatio = imageHeight * 4; // Width should be height x 4

//                        if(imageWidth < imageRatio)
//                        {
//                            Toast.makeText(requireContext(), "Banners are 4:1. Try another image", Toast.LENGTH_LONG).show();
//                            imageUri = null;
//                            //previewBanner.setImageURI(null);
//                            previewBanner.setImageResource(android.R.color.white);}
                    }
                }
                catch(Exception e)
                {Toast.makeText(requireContext(), "Please Select An Image", Toast.LENGTH_LONG).show();}}});
        imageButton.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View v) {
                Intent imageIntent = new Intent(Intent.ACTION_PICK);
                imageIntent.setType("image/*");
                customActivityResultLauncher.launch(imageIntent);}});



                // Create Event Submission
        Button createEventButton = view.findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
            // HANDLE SUBMIT
            // CREATE EVENT
            // CHECK THAT EACH PARAMETER IS PROPER
            String eventNameString = eventName.getText().toString();
            String eventDescriptionString = eventDescription.getText().toString();
            String eventDateString = eventDate.getText().toString();
            String eventTimeString = eventTime.getText().toString();
            String eventLocationString = eventLocation.getText().toString();
            String eventMaxAttendeesString = eventMaxAttendees.getText().toString();


            if (eventNameString.length() < 5) {
                Toast.makeText(requireContext(), "Title not long enough", Toast.LENGTH_SHORT).show();
            } else if (eventDescriptionString.length() < 5) {
                Toast.makeText(requireContext(), "Desciption not long enough", Toast.LENGTH_SHORT).show();
            } else if (eventDateString.length() < 1) {
                Toast.makeText(requireContext(), "You need an event Date", Toast.LENGTH_SHORT).show();
            } else if (eventTimeString.length() < 1) {
                Toast.makeText(requireContext(), "You need an event time", Toast.LENGTH_SHORT).show();
            } else if (eventLocationString.length() < 5) {
                Toast.makeText(requireContext(), "Your event needs a location", Toast.LENGTH_SHORT).show();

            } else if (QRCode == null) {
                //TODO change above to toast function as well for readability?
                toast("You must generate or scan a QR Code");
                // add handling for qr code and banner as well
                // add more handling here if needed

            } else {

                // convert the date to time since 1970 jan 1 to store in the database
                String combinedDateTime = dateString + " " + timeString;
                SimpleDateFormat sdf = new SimpleDateFormat("dd M yyyy HH:mm");  // this is used for just how the string is formatted we could change this easily if we need to

                try {

                    // Parse the string into a Date object
                    Date date = sdf.parse(combinedDateTime);
                    long timeSince1970 = date.getTime();  // this is the time we store n the database -> put in the event object when its created
                    if (eventMaxAttendeesString.length() > 0) {
                        maxAttendeeLong = Long.parseLong(eventMaxAttendeesString);
                    } else {
                        maxAttendeeLong = (long) -1;
                    }

//                    String imageUrl = null;

                    if (!(imageUri == null)) {
                        imageUrl = "events/" + QRCode;
                        //IMAGE UPLOAD TO FIREBASE
                        StorageReference selectionRef = fireRef.child(imageUrl);
                        selectionRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(requireContext(), "Image Uploaded To Cloud Successfully", Toast.LENGTH_LONG).show();

                                Event newEvent = new Event(eventNameString, eventDescriptionString, imageUrl, QRCode, timeSince1970, timeSince1970, eventLocationString, imageUrl, null, maxAttendeeLong, false);
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                FirebaseUtil.addEventToDB(db, newEvent, new FirebaseUtil.AddEventCallback() {
                                    @Override
                                    public void onEventAdded() {
                                        toast("Event successfully added!");
                                        Log.println(Log.DEBUG, "EventCreation", "New event with id: " + QRCode + " added");
                                        getParentFragmentManager().popBackStack();

                                        FirebaseUtil.addEventAndOrganizer(db, newEvent.getQRcode(), user.getDeviceID(), aVoid -> {
                                            Log.d("Firebase Success", "User registered successfully");
                                        }, e -> {
                                            Log.e("FirebaseError", "Error setting up organizer of event " + e.getMessage());
                                        });

                                        //TODO exit out maybe?
                                    }

                                    @Override
                                    public void onEventExists() {
                                        toast("ERROR: Event with the same ID already exists in the database.");
                                        Log.println(Log.DEBUG, "EventCreation", "Event with id: " + QRCode + " found a duplicate");
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        toast("An internal error occurred, please try again later");
                                        Log.println(Log.ERROR, "EventCreation", e.toString());
                                    }
                                });



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Image Upload Error", Toast.LENGTH_LONG).show();
                            }
                        });
                    }


                    // EVENT CREATION GOES HERE
                    // we can create the event object


                                    //this.eventName = eventName;
                                    //this.eventDescription = eventDescription;
                                    //this.eventBanner = eventBanner;
                                    //this.QRcode = QRcode;


                                    // then pass to database
                                    // user was passed into this activity it is a public User object named 'user'
                                    // use that to write to database and properly store in user events
                                    // also make sure that in the event the user is an organizer :salute:


                                    Toast.makeText(requireContext(), Long.toString(timeSince1970), Toast.LENGTH_SHORT).show();
                                } catch (ParseException e) {
                                    // Handle the possibility that parsing fails
                                    Toast.makeText(requireContext(), dateString + " " + timeString, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    return view;
    }


//The QR Code viewer class got deleted, up to you if/how you want to display it
//    /**
//     * Brings up a screen with the event name and a scannable QR code to join it
//     *
//     * @param  content the QR Code ID string of an event
//     * @param  name the name of the event
//     */
//    private void displayQR(String content, String name){
//        Intent intent = new Intent(this, QRCodeViewer.class);
//        Bundle extras = new Bundle();
//        extras.putString("name", name);
//        extras.putString("code", content);
//        intent.putExtras(extras);
//        startActivity(intent);
//    }



    private Pair<Integer, Integer> getDropboxIMGSize(Uri uri)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(Objects.requireNonNull(uri.getPath())).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        return new Pair<>(imageWidth, imageHeight);

    }
//    Pair<Integer, Integer> size = getDropboxIMGSize(imageUri);
//    int width = size.first;
//    int height = size.second;
    /**
     * Makes a toast popup
     *
     * @param text the text in the toast
     */
    private void toast(String text)
    {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show();
    }

    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
        new ScanContract(),
        result ->
        {
            if (result.getContents() != null)
            {
                // TODO remove debug confirmation maybe
                toast("Scanned: " + result.getContents());
                QRCode = QRCodeGenerator.getValidChars(result.getContents());
                //TODO again up to you if you want to display this
                //showQRString.setText(getString(R.string.qr_code_display).replace("%s", qrCode));
            }
        }
    );

            /**
             * Prepares the QR Code scanner
             */
    private void launchQRScanner()
    {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setBeepEnabled(false);
        options.setOrientationLocked(false);
        // Launch the barcode scanner
        qrLauncher.launch(options);
    }

    public void onDestroyView()
    {
        NavigationBarView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.setVisibility(View.VISIBLE);
        super.onDestroyView();
    }

}
