package com.example.fire_and_based;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is the adapter for the attendee list. It displays each attendee for an event.
 * Used by AttendeeListFragment.
 * @author Sumayya
 */
public class AttendeeArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;
    private ImageDownloader imageDownloader = new ImageDownloader();

    /**
     * Constructor for the adapter
     *
     * @param context The context (usually an Activity) in which the adapter is used
     * @param users  The list of attendees to be displayed
     */
    public AttendeeArrayAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    /**
     * Get a View for displaying data at the specified position.
     *
     * @param position    The position of the item in the data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent ViewGroup that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        {
            View view = convertView;

            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.attendee_content, parent,false);
            }

            User user = users.get(position);

            TextView username = view.findViewById(R.id.attendee_username);

            if (username.getText().toString().equals("")) {
                username.setText("Anonymous attendee");
            } else {
                username.setText(user.getUserName());
            }

            TextView checkedInText = view.findViewById(R.id.checked_in_text);
            checkedInText.setVisibility(View.GONE);

            ImageView imagePreview = view.findViewById(R.id.profile_picture_attendee);
            ImageDownloader downloadGuys = new ImageDownloader();
            downloadGuys.getProfilePicBitmap(user, (CircleImageView) imagePreview);

            return view;
        }
    }
}
