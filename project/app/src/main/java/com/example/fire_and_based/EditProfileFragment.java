package com.example.fire_and_based;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
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

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileFragment extends Fragment {
    private User user;
    private Uri imageUri;
    Context context = getContext();
    StorageReference fireRef = FirebaseStorage.getInstance().getReference();
    boolean pictureChanged = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile_fragment, container, false);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }

        // All required for functionality
        EditText firstNameEdit = view.findViewById(R.id.editTextFirst);
        EditText lastNameEdit = view.findViewById(R.id.editTextLast);
        EditText userNameEdit = view.findViewById(R.id.editTextUsername);
        EditText emailEdit = view.findViewById(R.id.editTextEmail);
        EditText phoneEdit = view.findViewById(R.id.editTextPhone);
        EditText homepageEdit = view.findViewById(R.id.editTextHomepage);
        TextView saveButton = view.findViewById(R.id.save_text_view);
        TextView cancelButton = view.findViewById(R.id.cancel_text_view);
        CircleImageView profilePictureView = view.findViewById(R.id.edit_profile_image);
        ImageView profilePictureEditButton = view.findViewById(R.id.edit_profile_picture_button);

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

        // Idrk how this stuff works I copied from Aiden
        // Real
        ActivityResultLauncher<Intent> customActivityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result)
                            {//if (result.getResultCode() == RESULT_OK)
                                try {
                                    if (result.getData() != null)
                                    {
                                        imageUri = result.getData().getData();
                                        Toast.makeText(requireContext(), "Uri Selected", Toast.LENGTH_LONG).show();
                                        //buttonUpload.setEnabled(true);
                                        //Glide.with(context).load(imageUri).into(previewBanner);
                                        profilePictureView.setImageURI(imageUri);
                                        pictureChanged = true;
                                    }
                                }
                                catch(Exception e)
                                {Toast.makeText(requireContext(), "Please Select An Image", Toast.LENGTH_LONG).show();}
                            }
                        });

        // Probably just make the profile picture itself clickable instead
        profilePictureEditButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Idrk how this stuff works I also copied from Aiden
                Intent imageIntent = new Intent(Intent.ACTION_PICK);
                imageIntent.setType("image/*");
                customActivityResultLauncher.launch(imageIntent);
            }});

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFirst = firstNameEdit.getText().toString();
                String newLast = lastNameEdit.getText().toString();
                String newUsername = userNameEdit.getText().toString();
                String newEmail = emailEdit.getText().toString();
                String newPhone = phoneEdit.getText().toString();
                String newHomepage = homepageEdit.getText().toString();

                if (validEntries(newFirst, newLast, newUsername, newEmail, newPhone, newHomepage)) {
                    // Updates user info
                    user.setFirstName(newFirst);
                    user.setLastName(newLast);
                    user.setUserName(newUsername);
                    user.setEmail(newEmail);
                    user.setPhoneNumber(newPhone);
                    user.setHomepage(newHomepage);

                    // I believe this is where profile pictures are stored?
                    String imageUrl = "profiles/" + user.getDeviceID();
                    StorageReference selectionRef = fireRef.child(imageUrl);

                    HashMap<String, Object> data = new HashMap<>();

                    data.put("firstName", user.getFirstName());
                    data.put("lastName", user.getLastName());
                    data.put("userName", user.getUserName());
                    data.put("email", user.getEmail());
                    data.put("phoneNumber", user.getPhoneNumber());
                    data.put("homepage", user.getHomepage());

                    // Updates user info
                    FirebaseUtil.updateUser(FirebaseFirestore.getInstance(), user, data, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // For now we exit from edit details page when valid save (probably should change later, not sure)

                            // Uploads new profile picture, if it was changed
                            if (pictureChanged) {
                                user.setProfilePicture(imageUrl);
                                selectionRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(requireContext(), "Successfully updated profile details", Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "User profile details successfully updated");
                                        getParentFragmentManager().popBackStack();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(requireContext(), "Image Upload Error", Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "User profile details successfully updated");
                                        getParentFragmentManager().popBackStack();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(requireContext(), "Successfully updated profile details", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "User profile details successfully updated");
                                getParentFragmentManager().popBackStack();
                            }
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error: User profile details failed to update");
                        }
                    });
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getParentFragmentManager().popBackStack();
            }
        });
        return view;
    }

    private boolean validEntries(String first, String last, String username, String email, String phone, String homepage) {
        if (!validName(first))
            Toast.makeText(requireContext(), "Invalid First Name", Toast.LENGTH_SHORT).show();
        else if (!validName(last))
            Toast.makeText(requireContext(), "Invalid Last Name", Toast.LENGTH_SHORT).show();
        else if (!validUsername(username))
            Toast.makeText(requireContext(), "Username must be no more than 20 characters", Toast.LENGTH_SHORT).show();
        else if (!validEmail(email))
            Toast.makeText(requireContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
        else if (!validPhone(phone))
            Toast.makeText(requireContext(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
        else if (!validHomepage(homepage))
            Toast.makeText(requireContext(), "Invalid Homepage", Toast.LENGTH_SHORT).show();

        return validName(first) && validName(last) && validEmail(email) && validPhone(phone) && validHomepage(homepage);
    }

    public static boolean validEmail(String email) {
        if (TextUtils.isEmpty(email))
            return true;
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validPhone(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber))
            return true;
        String phoneRegex = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
    public static boolean validName(String name) {
        if (TextUtils.isEmpty(name))
            return true;
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean validUsername(String username) {
        return username.length() < 20;
    }

    public boolean validHomepage(String homepage) {
        if (TextUtils.isEmpty(homepage)) {
            return true;
        }
        return URLUtil.isValidUrl(homepage);
    }
}