package com.example.fire_and_based;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is the adapter for the events. It displays each event in the list.
 * Used by EventListFragment.
 * @author Sumayya
 */
public class EventArrayAdapter extends ArrayAdapter<Event> implements Filterable {
    private ArrayList<Event> events;
    private ArrayList<Event> filteredEvents;
    private Context context;

    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageDownloader imageDownloader = new ImageDownloader();

    /**
     * Constructor for the adapter
     *
     * @param context The context (usually an Activity) in which the adapter is used
     * @param events  The list of events to be displayed
     */
    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = new ArrayList<>(events);
        this.filteredEvents = new ArrayList<>(events);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.event_list_content, parent,false);
            }

            Event event = filteredEvents.get(position);

            /**
             * Downloads event banner and displays
             */
            ImageView imagePreview = view.findViewById(R.id.event_banner_preview);
            if (event.getBannerQR() != null)
            {
                imageDownloader.getBannerBitmap(event,imagePreview);
            }

            TextView eventName = view.findViewById(R.id.event_title_text);

            eventName.setText(event.getEventName());

            TextView eventDate = view.findViewById(R.id.dateText);

            Long startLong = event.getEventStart();
            String startString = event.dateFromLong(startLong);
            eventDate.setText(startString);




            TextView checkCount = view.findViewById(R.id.checkCount);
//
//            String checkNum = String.valueOf(FirebaseUtil.getEventUserPool(db,event.getQRcode()));
//            String totalNum = String.valueOf(event.getMaxAttendees());
//            checkCount.setText(checkNum+"/"+totalNum);


            String checkNum = String.valueOf(event.getCurrentAttendees());
            String totalNum = String.valueOf(event.getMaxAttendees());
            checkCount.setText(checkNum + "/" + totalNum);

            //ArrayList<Integer> checkNums = event.getMilestones();

//
//            String docID = FirebaseUtil.cleanDocumentId(event.getQRcode());
//            List<String> userIDs;
//            db.collection("events").document(docID).get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                userIDs = (List<String>) documentSnapshot.get("attendees");
//            }
//            String checkNum = userIDs.size();
//

            return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<Event> filteredList = new ArrayList<>();
                String filterPattern = constraint.toString().toLowerCase().trim();

                // Iterate through a copy of the original event list and add matching items to the filtered list
                if (filterPattern.isEmpty()) {
                    filteredList.addAll(events); // Assuming events is the original list
                } else {
                    // Iterate through the original event list and add matching items to the filtered list
                    for (Event event : events) {
                        if (event.getEventName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(event);
                        }
                    }
                }
                filterResults.values = filteredList;
                filterResults.count = filteredList.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredEvents = (ArrayList<Event>) results.values;
                clear();
                addAll(filteredEvents);
                notifyDataSetChanged();
            }
        };
    }
}
