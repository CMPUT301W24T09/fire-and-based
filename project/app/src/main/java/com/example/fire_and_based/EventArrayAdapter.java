package com.example.fire_and_based;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying {@link Event} objects in a ListView.
 * This adapter is designed to handle an array of event objects, creating views for each one
 * that display the event's name and other relevant information.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    
    ImageDownloader imageDownloader = new ImageDownloader();
   /**
   * Constructs a new {@code EventArrayAdapter}.
   *
   * @param context The current context.
   * @param events  An ArrayList of {@link Event} objects to be displayed.
   */
    public EventArrayAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the data set of the data item whose view we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        // Inflate a new view if one isn't provided
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_list_content, parent,false);
        }

        // Get the Event object located at this position in the list
        Event event = events.get(position);

        /**
         * Downloads event banner and displays
         */
        ImageView imagePreview = view.findViewById(R.id.previewImage);
        if (event.getEventBanner() != null)
        {
            imageDownloader.getBannerBitmap(event,imagePreview);
        }

        // Find the TextView in the event_content.xml layout with the ID event_name_text
        TextView eventName = view.findViewById(R.id.event_title_text);
//        TextView eventDescription = view.findViewById(R.id.event_description_text);

          // Set the text of eventName TextView to the event's name
        eventName.setText(event.getEventName());

        // Optionally, you can set more event details if available and needed
        // TextView eventDescription = view.findViewById(R.id.event_description_text);
        // eventDescription.setText(event.getEventDescription());

        return view;
    }
}
