package com.example.fire_and_based;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Used for accessing image files in the database
 * @author Aiden
 */
public class ImageDownloader
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference fireRef = storage.getReference();

//    private LruCache<String, Bitmap> memoryCache;
//
//
//    protected void onCreate(Bundle savedInstanceState) {
//
//        // Get max available VM memory, exceeding this amount will throw an
//        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
//        // int in its constructor.
//        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//
//        // Use 1/8th of the available memory for this memory cache.
//        final int cacheSize = maxMemory / 8;
//
//        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
//            @Override
//            protected int sizeOf(String key, Bitmap bitmap) {
//                // The cache size will be measured in kilobytes rather than
//                // number of items.
//                return bitmap.getByteCount() / 1024;
//            }
//        };
//    }

    /**
     * Gets the bitmap of the banner for a particular event and displays it to the given ImageView
     * @param thisEvent the event
     * @param imagePreview guess
     */
    public void getBannerBitmap(Event thisEvent, ImageView imagePreview)
    {
        //Bitmap imageMap;
        String bannerUrl = thisEvent.getEventBanner();
        StorageReference uriRef;
        if (bannerUrl == null) {
            return;
        }
        uriRef = fireRef.child(bannerUrl);
        uriRef.getBytes(10000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap imageMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                //since this is the first retrieval, add to memory cache
                //addBitmapToMemoryCache(bannerUrl,imageMap);
                //memoryCache.put(bannerUrl, imageMap);

                imagePreview.setImageBitmap(imageMap);
            }
        });

    }
    /**
     * Gets the bitmap of the profile for a particular user and displays it to the given ImageView
     * @param thisUser the user
     * @param profilePreview guess
     */
        public void getProfilePicBitmap(User thisUser, CircleImageView profilePreview) {
            //Bitmap imageMap;

            //String profileUrl = "profiles/" + thisUser.getDeviceID();
            String profileUrl = thisUser.getCustomPicUrl();
            Log.d(TAG, profileUrl);
            StorageReference uriRef = fireRef.child(profileUrl);
            uriRef.getBytes(10000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap imageMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    //since this is the first retrieval, add to memory cache
                    //addBitmapToMemoryCache(bannerUrl,imageMap);
                    //memoryCache.put(bannerUrl, imageMap);
                    profilePreview.setImageBitmap(imageMap);
                }
            });
        }
    /**
     * Deletes the custom profile pic from storage and sets the default to the custom
     * @param thisUser the user
     */
        //SUMAYA USE THIS OK? OK.
        public void deleteProfilePic(User thisUser)
        {
            //We can only default the custom pics. We default back to default default default default
            String customProfileUrl = thisUser.getCustomPicUrl();
            StorageReference uriRef = fireRef.child(customProfileUrl);
            uriRef.delete();
            thisUser.setCustomPicUrl(thisUser.getDefaultPicUrl());
        }

    /**
     * Deletes the banner from storage and sets the default to the null
     * @param thisEvent the event
     */
        public void deleteEventPic(Event thisEvent)
        {
            String eventBannerUrl = thisEvent.getEventBanner();
            StorageReference uriRef = fireRef.child(eventBannerUrl);
            uriRef.delete();
            thisEvent.setEventBanner(null);
        }





//        FirebaseUtil.getEventBanner(db, thisEvent, new FirebaseUtil.EventBannerCallback()
//        {
//            public void onBannerUrlFetched(String bannerUrl)
//            {
        //url of banner has been retrieved, find the storageRef

        //check if url already a key in the cache
//                if (!(memoryCache.get(bannerUrl) == null))
//                {
//                    Bitmap imageMap = memoryCache.get(bannerUrl);
//                    imagePreview.setImageBitmap(imageMap);
//
//                }
//                else //download the image for the first time
//                {

        //}

//            }
//            public void onError(Exception e) {}
//        });
//    }

//    //DISPLAYS PROFILE PICTURES USING AN IDENTICAL METHOD
//    public void getProfileBitmap(User thisUser, ImageView imagePreview)
//    {
//        //Bitmap imageMap;
//        FirebaseUtil.getUserProfileUrl(db, thisUser, new FirebaseUtil.UserPictureCallback()
//        {
//            public void onProfilePictureUrlFetched(String profileUrl)
//            {
//                //url of banner has been retrieved, find the storageRef
//                StorageReference uriRef = fireRef.child(profileUrl);
//                uriRef.getBytes(10000000).addOnSuccessListener(new OnSuccessListener<byte[]>()
//                {
//                    @Override
//                    public void onSuccess(byte[] bytes)
//                    {
//                        Bitmap imageMap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                        imagePreview.setImageBitmap(imageMap);
//                    }
//                });
//            }
//            public void onError(Exception e) {}
//        });
//    }


}
