package com.example.fire_and_based;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<String> {

    private Context mContext; // Store the context

    public EventArrayAdapter(Context context, ArrayList<String> events) {
        super(context, 0, events);
        mContext = context; // Assign the context to the member variable
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

        ImageButton arrow = view.findViewById(R.id.arrow_button);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = parent.getContext();
                Intent intent = new Intent(mContext, EventInfoActivity.class);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
