package com.example.fire_and_based;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }

        TextView firstNameEdit = view.findViewById(R.id.firstAndLastText);
        TextView userNameEdit = view.findViewById(R.id.username_text);
        TextView emailEdit = view.findViewById(R.id.email_text);
        TextView phoneEdit = view.findViewById(R.id.phone_text);
        TextView homepageEdit = view.findViewById(R.id.homepage_text);
        Button deleteButton = view.findViewById(R.id.remove_profile_button);
        Button backButton = view.findViewById(R.id.back_button);
        ImageView removeProfilePic = view.findViewById(R.id.delete_profile_image);
        CircleImageView profilePictureView = view.findViewById(R.id.profile_image);

        // Retrieves profile picture from db and sets it in view
        ImageDownloader downloader = new ImageDownloader();
        downloader.getProfilePicBitmap(user, profilePictureView);

        // Fields may be "" or null, therefore only change text if exists
        if (!TextUtils.isEmpty(user.getFirstName()))
            firstNameEdit.setText(user.getFirstName());

        if (!TextUtils.isEmpty(user.getUserName()))
            userNameEdit.setText(user.getUserName());

        if (!TextUtils.isEmpty(user.getEmail()))
            emailEdit.setText(user.getEmail());

        if (!TextUtils.isEmpty(user.getPhoneNumber()))
            phoneEdit.setText(user.getPhoneNumber());

        if (!TextUtils.isEmpty(user.getHomepage()))
            homepageEdit.setText(user.getHomepage());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        if (user.getProfilePicture().startsWith("defaultProfiles/"))
            removeProfilePic.setVisibility(View.GONE);
        removeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setProfilePicture("defaultProfiles/" + user.getDeviceID());
                FirebaseUtil.updateUser(db, user, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("AdminProfileFragment", "Successfully removed user's profile picture and set it to default");
                        removeProfilePic.setVisibility(View.GONE);
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AdminProfileFragment", e.toString());
                    }
                });
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
