package com.example.fire_and_based;

import android.content.Context;
import android.content.Intent;
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

public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    ImageDownloader imageDownloader = new ImageDownloader();

    public EventArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }







    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_list_content, parent,false);
        }

        Event event = events.get(position);

        //Displays Image
        ImageView imagePreview = view.findViewById(R.id.previewImage);
        imageDownloader.getBannerBitmap(event,imagePreview);

        TextView eventName = view.findViewById(R.id.event_title_text);
//        TextView eventDescription = view.findViewById(R.id.event_description_text);

        eventName.setText(event.getEventName());
//        eventDescription.setText(event.getEventDescription());

        return view;
    }
}
