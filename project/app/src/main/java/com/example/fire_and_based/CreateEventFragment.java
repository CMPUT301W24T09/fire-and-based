package com.example.fire_and_based;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
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
import androidx.fragment.app.FragmentTransaction;

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
 * This fragment is hosted by UserActivity
 * This class is the activity for creating a new event.
 * It can be accessed by clicking the create event button on UserActivity.
 * @author Tyler, Ilya, Sumayya, Aiden
 */
public class CreateEventFragment extends Fragment {
    public User user;
    public Date newEventDate;

    public String QRCode = null;
    public String PosterQRCode = null;
    public String imageUrl = null;

    public String timeString;
    public String timeEndString;
    public String dateString;
    public String dateEndString;
    public Long maxAttendeeLong;

    // private ImageView previewBanner;
    public Uri imageUri;

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

    /**
     * Initializes the UI components and handles user interactions for creating an event.
     * @param inflater           The LayoutInflater object.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState The previous saved state of the fragment.
     * @return The View for the fragment's UI, or null.
     */

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
        EditText eventDateEnd = view.findViewById(R.id.event_date_editable_end);
        EditText eventTimeEnd = view.findViewById(R.id.event_time_editable_end);
        com.google.android.material.switchmaterial.SwitchMaterial geoTracking = view.findViewById(R.id.eventEditGeotrackToggleCreate);
        geoTracking.setChecked(true);

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

        eventDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        (view, year, monthOfYear, dayOfMonth) -> {
                            dateEndString = String.format(dayOfMonth + " " + (monthOfYear + 1) + " " + year);
                            eventDateEnd.setText(dayOfMonth + " " + (monthOfYear + 1) + " " + year);
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

        eventTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour = c.get(Calendar.HOUR_OF_DAY); // current hour
                int mMinute = c.get(Calendar.MINUTE); // current minute

                // Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                        (view, hourOfDay, minute) -> {
                            String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                            timeEndString = formattedTime;
                            eventTimeEnd.setText(formattedTime);
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
        Button previewQRImage = view.findViewById(R.id.view_qr_code);
        previewQRImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (QRCode != null && PosterQRCode != null){
                    displayQR(QRCode, PosterQRCode, eventName.getText().toString());
                } else {
                    toast("ERROR: QR Code not set");
                }
            }
        });

        Button generateQRButton = view.findViewById(R.id.generateQR);
        generateQRButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                QRCode = QRCodeGenerator.getValidString();
                PosterQRCode = QRCodeGenerator.getValidString();
                //TODO add a thing that previews the QR Code string? at least for debug?
                Toast.makeText(requireContext(), "Random QR Code Successfully Generated", Toast.LENGTH_SHORT).show();
                displayQR(QRCode, PosterQRCode, eventName.getText().toString());
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

        ImageButton imageButton = view.findViewById(R.id.add_banner_button);
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

                        if(imageWidth < imageRatio)
                        {
                            Toast.makeText(requireContext(), "Banners are 4:1. Try another image", Toast.LENGTH_LONG).show();
                            imageUri = null;
                            //previewBanner.setImageURI(null);
                            previewBanner.setImageResource(android.R.color.white);}
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
            String eventDateEndString = eventDateEnd.getText().toString();
            String eventTimeString = eventTime.getText().toString();
            String eventTimeEndString = eventTimeEnd.getText().toString();
            String eventLocationString = eventLocation.getText().toString();
            String eventMaxAttendeesString = eventMaxAttendees.getText().toString();

            Boolean isTracking = geoTracking.isChecked();



                if (eventNameString.length() < 5) {
                Toast.makeText(requireContext(), "Title not long enough", Toast.LENGTH_SHORT).show();
            } else if (eventDescriptionString.length() < 5) {
                Toast.makeText(requireContext(), "Desciption not long enough", Toast.LENGTH_SHORT).show();
            } else if (eventDateString.length() < 1) {
                Toast.makeText(requireContext(), "You need an start date", Toast.LENGTH_SHORT).show();
            } else if (eventDateEndString.length() < 1) {
                Toast.makeText(requireContext(), "You need an end date", Toast.LENGTH_SHORT).show();
            } else if (eventTimeString.length() < 1) {
                Toast.makeText(requireContext(), "You need an event time", Toast.LENGTH_SHORT).show();

            } else if (eventTimeEndString.length() < 1){
                Toast.makeText(requireContext(), "You need an event ending time", Toast.LENGTH_SHORT).show();
            } else if (eventLocationString.length() < 5) {
                Toast.makeText(requireContext(), "Your event needs a location", Toast.LENGTH_SHORT).show();

            } else if (QRCode == null) {
                //TODO change above to toast function as well for readability?
                toast("You must generate or upload a QR Code");
                // add handling for qr code and banner as well
                // add more handling here if needed
            } else {

                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Create New Event") // Set the dialog title
                            .setMessage("You cannot change the event name after this screen. Are you sure you want to make this event?")
                            .setPositiveButton("Make Event", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // User clicked Yes, call getLocation()
                                    // convert the date to time since 1970 jan 1 to store in the database
                                    String combinedDateTime = dateString + " " + timeString;
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd M yyyy HH:mm");  // this is used for just how the string is formatted we could change this easily if we need to
                                    String endDateTime = dateEndString + " " + timeEndString;

                                    try {

                                        // Parse the string into a Date object
                                        Date date = sdf.parse(combinedDateTime);
                                        long timeSince1970 = date.getTime();  // this is the time we store n the database -> put in the event object when its created

                                        Date endDate = sdf.parse(endDateTime);
                                        long endTimeSince1970 = endDate.getTime();

                                        if (eventMaxAttendeesString.length() > 0) {
                                            maxAttendeeLong = Long.parseLong(eventMaxAttendeesString);
                                        } else {
                                            maxAttendeeLong = (long) -1;
                                        }


                                        if (!(imageUri == null)) {
                                            imageUrl = "events/" + QRCode;
                                            //IMAGE UPLOAD TO FIREBASE
                                            StorageReference selectionRef = fireRef.child(imageUrl);
                                            Toast.makeText(getContext(), "MADE IT TO HERE ", Toast.LENGTH_SHORT).show();
                                            selectionRef.putFile(imageUri);

                                        }


//                    imageUrl = "TESTING IMAGE URL";
                                        Event newEvent = new Event(eventNameString, eventDescriptionString, imageUrl, QRCode, timeSince1970, endTimeSince1970, eventLocationString, PosterQRCode, null, maxAttendeeLong, 0L, isTracking);
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                                        FirebaseUtil.addEventToDB(db, newEvent, new FirebaseUtil.AddEventCallback() {
                                            @Override
                                            public void onEventAdded() {
                                                toast("Event successfully added!");
                                               // Log.println(Log.DEBUG, "EventCreation", "New event with id: " + QRCode + " added");

                                                FirebaseUtil.addEventAndOrganizer(db, newEvent.getQRcode(), user.getDeviceID(), aVoid -> {
                                                    Log.d("Firebase Success", "User is organizer of event now");
                                                    getParentFragmentManager().popBackStack();
                                                    EventDetailsFragment fragment = new EventDetailsFragment();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putParcelable("user", user);
                                                    bundle.putParcelable("event", newEvent);
                                                    bundle.putString("mode", "Organizing");
                                                    fragment.setArguments(bundle);
                                                    getParentFragmentManager().beginTransaction()
                                                            .replace(R.id.fragment_container_view, fragment)
                                                            .setReorderingAllowed(true)
                                                            .addToBackStack(null)
                                                            .commit();
                                                }, e -> {
                                                    Log.e("FirebaseError", "Error setting up organizer of event " + e.getMessage());
                                                });

                                                //TODO exit out maybe?
                                            }

                                            @Override
                                            public void onEventExists() {
                                                toast("ERROR: Event with the same ID already exists in the database.");
                                               // Log.println(Log.DEBUG, "EventCreation", "Event with id: " + QRCode + " found a duplicate");
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                toast("An internal error occurred, please try again later");
                                               // Log.println(Log.ERROR, "EventCreation", e.toString());
                                            }
                                        });


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


//                                    Toast.makeText(requireContext(), Long.toString(timeSince1970), Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        // Handle the possibility that parsing fails
                                        Toast.makeText(requireContext(), (CharSequence) e, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // User clicked No, do nothing or handle as needed
                                }
                            })
                            .show(); // Display the dialog






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

    /**
     * Retrieves the dimensions (width and height) of an image file from the given Uri.
     *
     * @param uri The Uri of the image file.
     * @return A Pair object containing the width and height of the image.
     */

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

    /**
     * ActivityResultLauncher for QR code scanning.
     */
    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
        new ScanContract(),
        result ->
        {
            if (result.getContents() != null)
            {
                // TODO remove debug confirmation maybe
                toast("Scanned: " + result.getContents());
                QRCode = QRCodeGenerator.getValidChars(result.getContents());
                PosterQRCode = QRCodeGenerator.getValidString();
                //TODO again up to you if you want to display this
                displayQR(QRCode, PosterQRCode, "");
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

    /**
     * Called when the fragment's view is destroyed.
     * Restores visibility of the bottom navigation bar.
     */
    public void onDestroyView()
    {
        NavigationBarView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.setVisibility(View.VISIBLE);
        super.onDestroyView();
    }

    /**
     * Displays a QR code using a dialog fragment.
     *
     * @param QRCode      The QR code string for the event.
     * @param PosterQRCode The QR code string for the poster.
     * @param eventName   The name of the event.
     */
    public void displayQR(String QRCode, String PosterQRCode, String eventName) {
        QRCodeDisplayFragment fragment = new QRCodeDisplayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("eventQR", QRCode);
        bundle.putString("eventName", eventName);
        bundle.putString("posterQR", PosterQRCode);
        fragment.setArguments(bundle);
        fragment.show(getParentFragmentManager(), "QRCodeDisplayFragment");
    }


}
