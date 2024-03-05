package com.example.fire_and_based;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<String> {

    public EventArrayAdapter(Context context, ArrayList<String> events) {
        super(context, 0, events);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_content, parent, false);
        } else {
            view = convertView;
        }

        String string = getItem(position);
        TextView eventTitle = view.findViewById(R.id.event_title);

        eventTitle.setText(string);

        return view;
    }
}
