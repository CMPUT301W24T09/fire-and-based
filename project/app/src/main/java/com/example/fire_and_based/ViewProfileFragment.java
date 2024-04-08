package com.example.fire_and_based;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is a fragment hosted by UserActivity.
 * Requires a user to be passed in as an argument as a Parcelable with a key "currentUser"
 * @author Sumayya, Carson
 */
public class ViewProfileFragment extends Fragment {
    private User user;

    /**
     * Inflates the layout for the view profile fragment and initializes its views with user data.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The root view of the inflated layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_profile_fragment, container, false);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }

        TextView nameText = view.findViewById(R.id.firstAndLastText);
        TextView usernameText = view.findViewById(R.id.username_text);
        TextView emailText = view.findViewById(R.id.email_text);
        TextView phoneText = view.findViewById(R.id.phone_text);
        TextView homepageText = view.findViewById(R.id.homepage_text);
        TextView editButton = view.findViewById(R.id.edit_profile_button);
        CircleImageView profileImageView = view.findViewById(R.id.profile_image);

        // If user has no name, no need for TextView to take up space
        if (TextUtils.isEmpty(user.getFirstName()) && TextUtils.isEmpty(user.getLastName()))
            nameText.setVisibility(View.GONE);
        // Some values are null, some are "", just to be safe use replace
        nameText.setText(String.format("%s %s", user.getFirstName(), user.getLastName()).replace("null", ""));

        // If empty, want to keep placeholder text so skip changing text
        if (!TextUtils.isEmpty(user.getUserName()))
            usernameText.setText(user.getUserName());

        if (!TextUtils.isEmpty(user.getEmail()))
            emailText.setText(user.getEmail());

        if (!TextUtils.isEmpty(user.getPhoneNumber()))
            phoneText.setText(user.getPhoneNumber());

        if (!TextUtils.isEmpty(user.getHomepage()))
            homepageText.setText(user.getHomepage());

        // Retrieves and sets profile picture in view
        ImageDownloader downloader = new ImageDownloader();
        downloader.getProfilePicBitmap(user, profileImageView);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment fragment = new EditProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                fragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, fragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}
