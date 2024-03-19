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

/**
 * This class is the adapter for the events. It displays each event in the list.
 * Used by EventListFragment.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    ImageDownloader imageDownloader = new ImageDownloader();

    /**
     * Constructor for the adapter
     *
     * @param context The context (usually an Activity) in which the adapter is used
     * @param events  The list of events to be displayed
     */
    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        {
            View view = convertView;

            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.event_list_content, parent,false);
            }

            Event event = events.get(position);

            /**
             * Downloads event banner and displays
             */
            ImageView imagePreview = view.findViewById(R.id.event_banner_preview);
            if (event.getEventBanner() != null)
            {
                imageDownloader.getBannerBitmap(event,imagePreview);
            }

            TextView eventName = view.findViewById(R.id.event_title_text);

            eventName.setText(event.getEventName());

            return view;
        }
    }
}