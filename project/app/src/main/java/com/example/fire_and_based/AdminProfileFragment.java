package com.example.fire_and_based;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This fragment is hosted by AdminActivity.
 * Requires a user to be passed in as an argument as a Parcelable with a key "user"
 * @author Sumayya
 */
public class AdminProfileFragment extends Fragment {
    private User user;

    /**
     * Inflates the layout for the admin profile fragment and initializes UI elements.
     * Retrieves user data passed as argument and populates the UI fields accordingly.
     * Admins can view and edit their profile information, as well as delete their account.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     * @return The root view of the inflated layout for the fragment
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_profile_fragment, container, false);

        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }

        EditText firstNameEdit = view.findViewById(R.id.first_name);
        EditText lastNameEdit = view.findViewById(R.id.last_name);
        EditText userNameEdit = view.findViewById(R.id.username);
        EditText emailEdit = view.findViewById(R.id.email);
        EditText phoneEdit = view.findViewById(R.id.phone);
        EditText homepageEdit = view.findViewById(R.id.homepage);
        TextView deleteButton = view.findViewById(R.id.delete_text_view);
        TextView cancelButton = view.findViewById(R.id.admin_cancel_text_view);
        CircleImageView profilePictureView = view.findViewById(R.id.admin_edit_profile_image);

        // Retrieves profile picture from db and sets it in view
        ImageDownloader downloader = new ImageDownloader();
        downloader.getProfilePicBitmap(user, profilePictureView);

        // Fields may be "" or null, therefore only change text if exists
        if (!TextUtils.isEmpty(user.getFirstName()))
            firstNameEdit.setText(user.getFirstName());

        if (!TextUtils.isEmpty(user.getLastName()))
            lastNameEdit.setText(user.getLastName());

        if (!TextUtils.isEmpty(user.getUserName()))
            userNameEdit.setText(user.getUserName());

        if (!TextUtils.isEmpty(user.getEmail()))
            emailEdit.setText(user.getEmail());

        if (!TextUtils.isEmpty(user.getPhoneNumber()))
            phoneEdit.setText(user.getPhoneNumber());

        if (!TextUtils.isEmpty(user.getHomepage()))
            homepageEdit.setText(user.getHomepage());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUtil.deleteUser(db, user, aVoid -> {
                    getParentFragmentManager().popBackStack();
                }, e -> {
                    Toast.makeText(requireContext(), "Unable to delete user", Toast.LENGTH_SHORT).show();
                });
            }
        });

        return view;
    }
}
