package com.example.fire_and_based;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This is the adapter for the array of events.
 * It displays the poster for each event.
 * @author Sumayya
 */
public class EventPosterAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;
    private ImageDownloader imageDownloader = new ImageDownloader();

    /**
     * Constructor for the adapter
     *
     * @param context The context (usually an Activity) in which the adapter is used
     * @param events  The list of events to be displayed
     */
    public EventPosterAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = new ArrayList<>(events);
        this.context = context;
    }


    /**
     * Get a View that displays the data at the specified position.
     * If convertView is null, inflates a new view from the layout.
     *
     * @param position    The position of the item in the data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent ViewGroup to which the view will be attached.
     * @return A View displaying the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_poster, parent, false);
        }

        Event event = events.get(position);

        /**
         * Downloads event banner and displays
         */
        ImageView imagePreview = view.findViewById(R.id.event_poster);
        if (event.getBannerQR() != null) {
            imageDownloader.getBannerBitmap(event, imagePreview);
        }
        String eventName = event.getEventName();
        TextView eventText = view.findViewById(R.id.event_title_text);
        eventText.setText(eventName);





        return view;
    }




}
