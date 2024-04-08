package com.example.fire_and_based;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This fragment is hosted by ImagesListFragment.
 * It displays the all the event banners.
 * @author Sumayya, Aiden
 */
public class EventPostersFragment extends Fragment {
    protected ListView eventList;
    protected EventPosterAdapter eventAdapter;
    protected ArrayList<Event> dataList;

    /**
     * Inflates the layout for the event posters fragment and populates it with event data.
     *
     * @param inflater           The LayoutInflater object.
     * @param container          The parent view to attach the fragment's UI.
     * @param savedInstanceState The saved state of the fragment.
     * @return The root view of the inflated layout.
     */


    protected int lastClickedIndex;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_posters_fragment, container, false);

        dataList = new ArrayList<>();
        eventList = view.findViewById(R.id.event_poster_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUtil.getAllEvents(db, events -> {
            dataList.clear();
            for (Event event : events) {
                if (event.getEventBanner() != null)
                {
                    //Means it has non-null banner add it to the list
                    dataList.add(event);
                }
            }
            eventAdapter = new EventPosterAdapter(requireContext(), dataList);
            eventList.setAdapter(eventAdapter);
        });

        ImageDownloader downloader = new ImageDownloader();

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastClickedIndex = position;
                Event clickedEvent = dataList.get(lastClickedIndex);


                new AlertDialog.Builder(view.getContext())
                        .setTitle("Deleting Event Poster") // Set the dialog title
                        .setMessage("Deleting this image will leave the event with a blank banner") // Set the dialog message
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked Delete, delete image
                                downloader.deleteBanner(clickedEvent);
                                dataList.remove(clickedEvent);
                                eventAdapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked cancel, do nothing
                            }
                        })
                        .show(); // Display the dialog
            }
        });
        return view;
    }
}
