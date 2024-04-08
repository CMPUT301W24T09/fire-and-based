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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is the adapter for the attendee list.
 * It displays each attendee for an event, with the amount of times they've checked in.
 * Used by AttendeeListFragment.
 * @author Sumayya
 */
public class CheckedInAttendeeAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private List<User> mDataList;
    private List<Long> mLongList;
    private ImageDownloader imageDownloader = new ImageDownloader();

    /**
     * Constructs a new CheckedInAttendeeAdapter.
     *
     * @param context  The context in which the adapter is being used.
     * @param dataList The list of User objects to be displayed.
     * @param longList The list of associated data for each user.
     */
    public CheckedInAttendeeAdapter(Context context, List<User> dataList, List<Long> longList) {
        super(context, 0, dataList);
        mContext = context;
        mDataList = dataList;
        mLongList = longList;
    }


    /**
     * Get a View for displaying data at the specified position.
     *
     * @param position    The position of the item in the data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent ViewGroup that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        {
            View view = convertView;

            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.attendee_content, parent, false);
            }

            User user = mDataList.get(position);
            Long checkedInValue = mLongList.get(position);

            TextView username = view.findViewById(R.id.attendee_username);
            if (username.getText().toString().equals("")) {
                username.setText("Anonymous attendee");
            } else {
                username.setText(user.getUserName());
            }

            TextView checkedInText = view.findViewById(R.id.checked_in_text);
            checkedInText.setText("Checked in " + checkedInValue + " times");

            ImageView imagePreview = view.findViewById(R.id.profile_picture_attendee);
            ImageDownloader downloadGuys = new ImageDownloader();
            downloadGuys.getProfilePicBitmap(user, (CircleImageView) imagePreview);

            return view;
        }
    }
}
