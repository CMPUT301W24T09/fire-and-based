package com.example.fire_and_based;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
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

import java.util.ArrayList;

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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.announcements_content, parent, false);
            }


        Announcement announcement = announcements.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = announcement.getSender();

        TextView username = view.findViewById(R.id.notif_user);
        ImageView profilePic = view.findViewById(R.id.profile_image);

        ImageDownloader downloadGuy = new ImageDownloader();

        FirebaseUtil.getUserObject(db, userID,
                user -> {
                    String userName = user.getUserName();
                    if(TextUtils.isEmpty(userName))
                    {
                        userName = "Organizer";
                    }

                    username.setText(userName);
                    downloadGuy.getProfilePicBitmap(user, (CircleImageView) profilePic);




                },
                e -> {
                    Log.d(TAG, e.toString());
                });
        TextView description = view.findViewById(R.id.notif_description);
        TextView time = view.findViewById(R.id.notif_time);
        description.setText(announcement.getContent());
        time.setText(String.valueOf(announcement.getTimestamp()));


            return view;
    }
}
