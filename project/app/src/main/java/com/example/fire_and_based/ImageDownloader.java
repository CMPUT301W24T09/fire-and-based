package com.example.fire_and_based;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageDownloader
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference fireRef = storage.getReference();


    //DISPLAYS BANNER IMAGES
    public void getBannerBitmap(Event thisEvent, ImageView imagePreview)
    {
        //Bitmap imageMap;
        FirebaseUtil.getEventBannerUrl(db, thisEvent, new FirebaseUtil.EventBannerCallback()
        {
            public void onBannerUrlFetched(String bannerUrl)
            {
                //url of banner has been retrieved, find the storageRef
                StorageReference uriRef = fireRef.child(bannerUrl);
                uriRef.getBytes(1000000).addOnSuccessListener(new OnSuccessListener<byte[]>()
                {
                    @Override
                    public void onSuccess(byte[] bytes)
                    {
                        Bitmap imageMap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imagePreview.setImageBitmap(imageMap);
                    }
                });
            }
            public void onError(Exception e) {}
        });
    }

    //DISPLAYS PROFILE PICTURES USING AN IDENTICAL METHOD
    public void getProfileBitmap(User thisUser, ImageView imagePreview)
    {
        //Bitmap imageMap;
        FirebaseUtil.getUserProfileUrl(db, thisUser, new FirebaseUtil.UserPictureCallback()
        {
            public void onProfilePictureUrlFetched(String profileUrl)
            {
                //url of banner has been retrieved, find the storageRef
                StorageReference uriRef = fireRef.child(profileUrl);
                uriRef.getBytes(10000000).addOnSuccessListener(new OnSuccessListener<byte[]>()
                {
                    @Override
                    public void onSuccess(byte[] bytes)
                    {
                        Bitmap imageMap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imagePreview.setImageBitmap(imageMap);
                    }
                });
            }
            public void onError(Exception e) {}
        });
    }


}
