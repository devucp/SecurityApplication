package com.example.securityapplication.Helper;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.securityapplication.GoogleFirebaseSignIn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class InternalStorage {

    private File imagePath;
    private static volatile InternalStorage internalStorageInstance;
    final static private String TAG = "InternalStorage";
    private Context context;

    public static InternalStorage getInstance() {
        //Double check locking pattern
        if (internalStorageInstance == null) { //Check for the first time..if there is no instance available... create new one
            synchronized (GoogleFirebaseSignIn.class) { //Check for the second time to make ThreadSafe
                //if there is no instance available... create new one
                if (internalStorageInstance == null) {
                    internalStorageInstance = new InternalStorage();
                    Log.d(TAG,"Created new FirebaseHelperInstance");
                }
            }
        }
        else {
            Log.d(TAG,"FirebaseHelperInstance Exists");
        }
        return internalStorageInstance;
    }

    //Make singleton from serialize and deserialize operation.
    protected InternalStorage readResolve() {
        return getInstance();
    }

    public void initContext(Context context){this.context = context;}


    public String saveImageToInternalStorage(Bitmap bitmapImage, String email){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(email+"imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpeg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public File getImagePathFromStorage(String email) {

        try {
            File directory = new ContextWrapper(context).getDir(email+"imageDir", Context.MODE_PRIVATE);
            return new File(directory.getAbsolutePath(), "profile.jpeg");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
