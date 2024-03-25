package com.example.fire_and_based;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import javax.annotation.Nullable;

public class ImageUploader extends AppCompatActivity
{
    public ImageView imagePreview;
    public Uri imageUri;
    public String imageUrl;

    StorageReference fireRef = FirebaseStorage.getInstance().getReference();


    ImageUploader(ImageView previewImage,String urlImage)
    {
        this.imagePreview = previewImage;
        //this.imageUri = uriImage;
        this.imageUrl = urlImage;
    }


    ActivityResultLauncher<Intent> customActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                public void onActivityResult(ActivityResult result)
        {//if (result.getResultCode() == RESULT_OK)
            try
            {
                if (result.getData() != null)
                {
                    imageUri = result.getData().getData();
                    Glide.with(getApplicationContext()).load(imageUri).into(imagePreview);
                }
            }
            catch(Exception e)
            {
              //  Toast.makeText(this.class, "Please Select An Image", Toast.LENGTH_LONG).show();
            }
        }
    });

    public void uploadImage()
    {
        Intent imageIntent = new Intent(Intent.ACTION_PICK);
        imageIntent.setType("image/*");
        customActivityResultLauncher.launch(imageIntent);

        //prep image for storage
        StorageReference selectionRef = fireRef.child(imageUrl);
        //store image
        selectionRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // Toast.makeText(EventCreation.this, "Image Uploaded To Cloud Successfully", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(EventCreation.this, "Image Upload Error", Toast.LENGTH_LONG).show();
            }
        });
    }






}