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

public class AnnouncementArrayAdapter extends ArrayAdapter<Announcement> {
    private ArrayList<Announcement> announcements;
    private Context context;

    /**
     * Constructor for the adapter
     *
     * @param context       The context (usually an Activity) in which the adapter is used
     * @param announcements The list of announcements to be displayed
     * @author Sumayya
     */
    public AnnouncementArrayAdapter(Context context, ArrayList<Announcement> announcements) {
        super(context, 0, announcements);
        this.announcements = announcements;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.announcements_content, parent, false);
            }

            Announcement announcement = announcements.get(position);

            TextView username = view.findViewById(R.id.notif_user);
            username.setText(announcement.getSender());

            TextView description = view.findViewById(R.id.notif_description);
            description.setText(announcement.getContent());

            TextView time = view.findViewById(R.id.notif_time);
            time.setText(String.valueOf(announcement.getTimestamp()));

            return view;
    }
}
