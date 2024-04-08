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

public class ProfilePictureAdapter extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;
    private ImageDownloader imageDownloader = new ImageDownloader();

    /**
     * Constructor for the adapter
     *
     * @param context The context (usually an Activity) in which the adapter is used
     * @param users  The list of events to be displayed
     */
    public ProfilePictureAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    /**
     * Returns the view for the item at the specified position in the adapter.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return The view corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        {
            View view = convertView;

            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.profile_picture_content, parent,false);
            }

            User user = users.get(position);

            ImageView imagePreview = view.findViewById(R.id.profile_pic);
            if (user.getProfilePicture() != null) {
                imageDownloader.getProfilePicBitmapNotCircle(user, imagePreview);
            }

            return view;
        }
    }
}
