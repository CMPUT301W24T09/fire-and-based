package com.example.fire_and_based;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * An ArrayAdapter for displaying announcements.
 * @author Sumayya
 */

public class AnnouncementArrayAdapter extends ArrayAdapter<Announcement> {
    private ArrayList<Announcement> announcements;
    private Context context;

    /**
     * Constructor for the adapter
     *
     * @param context       The context (usually an Activity) in which the adapter is used
     * @param announcements The list of announcements to be displayed
     */
    public AnnouncementArrayAdapter(Context context, ArrayList<Announcement> announcements) {
        super(context, 0, announcements);
        this.announcements = announcements;
        this.context = context;
    }

    /**
     * Retrieves a View to display data for the specified position.
     * If convertView is null, inflates a new view from the layout.
     * Binds data from Announcement object to the views.
     * @param position The position of the item in the data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent ViewGroup to which the view will be attached.
     * @return A View displaying the data at the specified position.
     */

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.announcements_content, parent, false);
        }


        final Calendar c = Calendar.getInstance();



        Announcement announcement = announcements.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userID = announcement.getSender();



        // get the XML elements
        TextView username = view.findViewById(R.id.notif_user);
        CircleImageView profilePic = view.findViewById(R.id.profile_picture);
        TextView description = view.findViewById(R.id.notif_description);

        TextView time = view.findViewById(R.id.notif_time);


        long startTimeLong = announcement.getTimestamp();
        startTimeLong = startTimeLong * 1000;  // i think there is inconsitency
//        String[] DateTime = convertTimestampToCalendarDateAndTime(startTimeLong);
//        String MonthYearDay = DateTime[0];

        String diffOption = announcement.dateFromLong(startTimeLong);
        String[] dateTimeConverted = convertTimestampToCalendarDateAndTime(startTimeLong);
        String announcementDate = dateTimeConverted[0];
        String announcementTime = dateTimeConverted[1];



        time.setText(diffOption + " " + announcementTime);

//        time.setText(announcementDate + "\n" + announcementTime);


        description.setText(announcement.getContent());


        ImageDownloader downloadGuy = new ImageDownloader();

        FirebaseUtil.getUserObject(db, userID,
                user -> {
                    String userName = user.getUserName();
                    if(TextUtils.isEmpty(userName))
                    {
                        userName = "Organizer";
                    }
                    username.setText(userName);



                    // CARSON SOMEONE GOTTA FIX THIS SHIT BRO
                    downloadGuy.getProfilePicBitmap(user, profilePic);
//                    Bitmap imageBitMap = downloadGuy.returnProfileBitmap(user);
//                    profilePic.setImageBitmap(imageBitMap);

                },
                e -> {
                    Log.d(TAG, e.toString());
                });

        return view;
    }


    /**
     * Converts a timestamp to calendar date and time strings.
     *
     * @param timestamp The timestamp to convert.
     * @return An array containing date and time strings.
     */
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
        String aMpM = "AM";
        if (hour > 12){
            hour = hour % 12;
            aMpM = "PM";
        }
        // Format the date string
        String date = String.format("%02d %02d %d", day, month, year);
        // Format the time string
        String time = String.format("%02d:%02d", hour, minute);
        time = time + " " + aMpM;

        // Return an array with both date and time
        return new String[]{date, time};
    }
}