package com.example.fire_and_based;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageUploader extends Firebase
{
    private Uri imageData;


    private ImageView imagePreview;
    private EditText editImageId;
    private Button buttonSelect;
    private Button buttonUpload;

    private ProgressBar uploadProgress;




    private final ActivityResultLauncher<Intent> customActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        if (result.getData() != null)
                        {
                            imageData = result.getData().getData();
                            buttonUpload.setEnabled(true);
                            Glide.with(getApplicationContext()).load(imageData).into(imagePreview);
                        }
                    }
                    else
                    {
                        Toast.makeText(ImageUploader.this, "Select Image You Bastard (sorry im bipolar)", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_uploader);


        StorageReference fireRef = FirebaseStorage.getInstance().getReference();


        imagePreview = findViewById(R.id.image_preview);
        editImageId = findViewById(R.id.edit_image_id);
        buttonSelect = findViewById(R.id.button_select_image);
        buttonUpload = findViewById(R.id.button_upload_image);
       // viewUploads = findViewById(R.id.text_upload_status);
        uploadProgress = findViewById(R.id.progressBar);







        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imageIntent = new Intent(Intent.ACTION_PICK);
                //ensure we only see images in file chooser
                imageIntent.setType("image/*");
                customActivityResultLauncher.launch(imageIntent);
//YUCK
//                ActivityResultLauncher<PickVisualMediaRequest> pickMedia
//                        = registerForActivityResult(new ActivityResultContracts.PickVisualMedia()
//                        , imageData ->
//                {
//                });
//
//                pickMedia.launch(new PickVisualMediaRequest.Builder()
//                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
//                        .build());
//
//
//                String imageDataString = imageData.toString();
//                StorageReference selectionRef = fireRef.child(imageDataString);
//

            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String imageName = editImageId.getText().toString();

                StorageReference selectionRef = fireRef.child("images/"+imageName);

                //b.collection("events").document("froggy").set(imageData);

                selectionRef.putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ImageUploader.this, "Image Uploaded To Cloud Successfully", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ImageUploader.this, "Image Upload Error", Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        uploadProgress.setMax(Math.toIntExact(snapshot.getTotalByteCount()));
                        uploadProgress.setProgress(Math.toIntExact(snapshot.getBytesTransferred()));
                    }
                });

            }
        });








    }
}