package com.example.fire_and_based;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageUploader extends Firebase
{

    //DO NOT USE THIS CLASS
    //DO NOT USE THIS CLASS
    //DO NOT USE THIS CLASS
    //DO NOT USE THIS CLASS
    //DO NOT USE THIS CLASS
    //DO NOT USE THIS CLASS
    //DO NOT USE THIS CLASS
    //DO NOT USE THIS CLASS


    /**
     * CLASS NEEDS TO BE REFACTORED. WILL BE DONE LATER
     */
}

//    private Uri imageData;
//
//  // private ImageView imagePreview;
//    private EditText editImageId;
//    private Button buttonSelect;
//    private Button buttonUpload;
//
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    StorageReference fireRef = FirebaseStorage.getInstance().getReference();
//
//
//
//    ActivityResultLauncher<Intent> customActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//        @Override
//        public void onActivityResult(ActivityResult result)
//        {//if (result.getResultCode() == RESULT_OK)
//            try {
//                if (result.getData() != null)
//                {
//                    imageData = result.getData().getData();
//                    //buttonUpload.setEnabled(true);
//                    //Glide.with(getApplicationContext()).load(imageData).into(imagePreview);
//                }
//            }
//            catch(Exception e)
//            {Toast.makeText(ImageUploader.this, "Please Select An Image", Toast.LENGTH_LONG).show();}
//        }
//    });
//
//
//
//
//
//
//    public Uri imageSelection(ImageView imagePreview)
//    {
//
//
//        Intent imageIntent = new Intent(Intent.ACTION_PICK);
//        imageIntent.setType("image/*");
//        customActivityResultLauncher.launch(imageIntent);
//
//        imagePreview.setImageURI(imageData);
//        return imageData;
//    }
//
//
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.image_uploader);
//
//
//        //imagePreview = findViewById(R.id.image_preview);
//        editImageId = findViewById(R.id.edit_image_id);
//        buttonSelect = findViewById(R.id.button_select_image);
//        buttonUpload = findViewById(R.id.button_upload_image);
//
//
////        buttonSelect.setOnClickListener(new View.OnClickListener()
////        {
////            @Override
////            public void onClick(View v)
////            {
////                Intent imageIntent = new Intent(Intent.ACTION_PICK);
////                imageIntent.setType("image/*");
////                customActivityResultLauncher.launch(imageIntent);
////            }
////        });
//
//        buttonUpload.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                String imageName = editImageId.getText().toString();
//                StorageReference selectionRef = fireRef.child("events/" + imageName);
////                // i just made a fake user to test updating the URL
////                User fakeUser = new User("123", "TestingUser", null, null);
////                FirebaseUtil.addUserToDB(db, fakeUser);
////                String newURL = "images/"+imageName;
////                FirebaseUtil.updateUserProfileImageUrl(db, fakeUser, newURL);
//                selectionRef.putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(ImageUploader.this, "Image Uploaded To Cloud Successfully", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(ImageUploader.this, "Image Upload Error", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
//}
