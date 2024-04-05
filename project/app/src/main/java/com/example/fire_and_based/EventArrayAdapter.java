package com.example.fire_and_based;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
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
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.event_list_content, parent,false);
            }

            Event event = events.get(position);

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
}
