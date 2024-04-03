package com.example.fire_and_based;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditEventFragment extends Fragment {

    private Event event;
    private User user;
    private Uri imageUri;
    private Boolean imageChanged = false;
    private ImageView previewBanner;
    private ActivityResultLauncher<Intent> customActivityResultLauncher;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_event_fragment, container, false);
        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
            user = getArguments().getParcelable("user");
        }

        TextView eventTitle = view.findViewById(R.id.eventEditTitle);
        TextView cancelButton = view.findViewById(R.id.eventEditCancelButton);
        // all form fields just read them it makes sense i promise :3
        EditText eventDescription = view.findViewById(R.id.eventEditDescription);
        EditText eventStartDate = view.findViewById(R.id.eventDateStartEdit);
        EditText eventEndDate = view.findViewById(R.id.eventDateEndEdit);
        EditText eventStartTime = view.findViewById(R.id.eventTimeEdit);
        EditText eventLocation = view.findViewById(R.id.eventEditLocation);
        EditText eventAttendeeAmount = view.findViewById(R.id.eventEditAttendeeAmount);
        Button saveButton = view.findViewById(R.id.eventEditSaveButton);
        TextView deleteButton = view.findViewById(R.id.eventEditDeleteButton);

        // setting all the forms
        eventTitle.setText(event.getEventName());
        eventDescription.setText(event.getEventDescription());
        Long eventStartDateLong = event.getEventStart();          // sets start date after converting
        String[] dateTime = convertTimestampToCalendarDateAndTime(eventStartDateLong);
        String startCalendar = dateTime[0]; // date
        String startTime = dateTime[1]; // time
        eventStartDate.setText(startCalendar);
        eventStartTime.setText(startTime);
        Long eventEndDateLong = event.getEventEnd();         // sets end date after converting
        String endCalendar = convertTimestampToCalendarDateAndTime(eventEndDateLong)[0];
        eventEndDate.setText(endCalendar);
        eventLocation.setText(event.getLocation());
        eventAttendeeAmount.setText(event.getMaxAttendees().toString());

        // gets the image view in the xml, uses image downloader to display banner given and even and field
        ImageView eventBannerImage = view.findViewById(R.id.eventEditImage);
        ImageDownloader ImageDownloader = new ImageDownloader();
        ImageDownloader.getBannerBitmap(event, eventBannerImage);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getParentFragmentManager();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUtil.deleteEvent(db, event, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Event deleted from database :) ", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < 2; i++) {
                            fragmentManager.popBackStackImmediate();
                        }
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Cannot delete event from database", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        // onclick for the event date that opens a calendar ui for them to select a date idk how to function this tbh and idc XD
        eventStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        (view, year, monthOfYear, dayOfMonth) -> {
                            String dateString = String.format(dayOfMonth + " " + (monthOfYear + 1) + " " + year);
                            eventStartDate.setText(dayOfMonth + " " + (monthOfYear + 1) + " " + year);
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        eventEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        (view, year, monthOfYear, dayOfMonth) -> {
                            String dateString = String.format(dayOfMonth + " " + (monthOfYear + 1) + " " + year);
                            eventEndDate.setText(dayOfMonth + " " + (monthOfYear + 1) + " " + year);
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        // something something image uploader hmmmmm....... gj aiden :3
        ActivityResultLauncher<Intent> customActivityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result)
                            {//if (result.getResultCode() == RESULT_OK)
                                try {
                                    if (result.getData() != null)
                                    {
                                        imageUri = result.getData().getData();
                                        Toast.makeText(requireContext(), "Uri Selected", Toast.LENGTH_LONG).show();
                                        //buttonUpload.setEnabled(true);
                                        //Glide.with(context).load(imageUri).into(previewBanner);
                                        eventBannerImage.setImageURI(imageUri);
                                        imageChanged = true;
                                    }
                                }
                                catch(Exception e)
                                {Toast.makeText(requireContext(), "Please Select An Image", Toast.LENGTH_LONG).show();}
                            }
                        });

        eventBannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Idrk how this stuff works I also copied from Aiden
                Intent imageIntent = new Intent(Intent.ACTION_PICK);
                imageIntent.setType("image/*");
                customActivityResultLauncher.launch(imageIntent);
            }});

        // Setup the ActivityResultLauncher




        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventDescriptionString = eventDescription.getText().toString();
                String eventStartDateString = eventStartDate.getText().toString();
                String eventStartTimeString = eventStartTime.getText().toString();
                String eventEndDateString = eventEndDate.getText().toString();
                String eventLocationString = eventLocation.getText().toString();
                String eventMaxAttendeeAmountString = eventAttendeeAmount.getText().toString();

                if (!checkValidFields(eventDescriptionString, eventStartDateString, eventStartTimeString, eventEndDateString, eventLocationString, eventMaxAttendeeAmountString)){
                    Toast.makeText(getContext(), "Fields must all be satisfied", Toast.LENGTH_SHORT).show();
                } else {


                    // convert the date to time since 1970 jan 1 to store in the database
                    String combinedDateTime = eventStartDateString + " " + eventStartTimeString;
                    String combinedDateTimeEnd = eventEndDateString + " " + eventStartTimeString;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd M yyyy HH:mm");  // this is used for just how the string is formatted we could change this easily if we need to

                    try {

                        // Parse the string into a Date object
                        Date date = sdf.parse(combinedDateTime);
                        long eventStartLong = date.getTime();  // this is the time we store n the database -> put in the event object when its created
                        Date endDate = sdf.parse(combinedDateTimeEnd);
                        long eventEndLong  = endDate.getTime();

                        long eventMaxAttendeesLong = Long.parseLong(eventMaxAttendeeAmountString);



//                        String eventDescriptionString = eventDescription.getText().toString();
//                        String eventStartDateString = eventStartDate.getText().toString();
//                        String eventStartTimeString = eventStartTime.getText().toString();
//                        String eventEndDateString = eventEndDate.getText().toString();
//                        String eventLocationString = eventLocation.getText().toString();
//                        String eventMaxAttendeeAmountString = eventAttendeeAmount.getText().toString();
                        // we ball
                        event.setEventDescription(eventDescriptionString);
                        event.setEventStart(eventStartLong);
                        event.setEventEnd(eventEndLong);
                        event.setLocation(eventLocationString);
                        event.setMaxAttendees(eventMaxAttendeesLong);

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseUtil.updateEvent(db, event, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (!imageChanged){
                                    Toast.makeText(getContext(), "Event Details Updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    String QRCode = event.getQRcode();
                                    String imageUrl = "events/" + QRCode;

                                    // IMAGE UPLOAD TO FIREBASE
                                    StorageReference fireRef = FirebaseStorage.getInstance().getReference();
                                    StorageReference selectionRef = fireRef.child(imageUrl);
                                    selectionRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(requireContext(), "Image Uploaded To Cloud Successfully", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(requireContext(), "Image Upload Error", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                EventDetailsFragment fragment = new EventDetailsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("user", user);
                                bundle.putParcelable("event", event);
                                bundle.putString("mode", "Organizing");
                                fragment.setArguments(bundle);
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container_view, fragment)
                                        .setReorderingAllowed(true)
                                        .addToBackStack(null)
                                        .commit();
                            }

                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed in updating the event", Toast.LENGTH_SHORT).show();

                            }
                        });

                    } catch (ParseException e) {
                        // Handle the possibility that parsing fails
                        Toast.makeText(requireContext(), "Error with the time conversion...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });





        return view;
    }

    // checks for valid fields function
    private boolean checkValidFields(String eventDescriptionString, String eventStartDateString, String eventStartTimeString, String eventEndDateString, String eventLocationString, String eventMaxAttendeeAmountString) {
        if (eventDescriptionString == "" || eventDescriptionString == null){ return false;
        } else if (eventStartDateString == "" || eventStartDateString == null) { return false;
        } else if (eventStartTimeString == "" || eventStartTimeString == null) { return false;
        } else if (eventEndDateString == "" || eventEndDateString == null){ return false;
        } else if (eventLocationString == "" || eventLocationString == null) { return false;
        } else if (eventMaxAttendeeAmountString == "" || eventMaxAttendeeAmountString == null){return false;
        } else { return true;}
    }


    public static String[] convertTimestampToCalendarDateAndTime(Long timestamp) {
        // Create a Calendar instance and set the time using the given timestamp
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        // Extract the year, month, day, hour, and minute from the calendar
        int year = calendar.get(Calendar.YEAR);
        // Add 1 to the month because Calendar.MONTH returns 0 for January
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Format the date string
        String date = String.format("%02d %02d %d", day, month, year);
        // Format the time string
        String time = String.format("%02d:%02d", hour, minute);

        // Return an array with both date and time
        return new String[] { date, time };
    }



}