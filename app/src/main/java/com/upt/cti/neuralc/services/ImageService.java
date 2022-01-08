package com.upt.cti.neuralc.services;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ImageService {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public static void saveToInternalStorage(Bitmap bitmapImage, Context context, int prediction){
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("xrays", Context.MODE_PRIVATE);

        String timestamp = sdf.format(new Date()) + String.valueOf(prediction) + ".jpg";
        File mypath = new File(directory,timestamp);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static ArrayList<Bitmap> loadImagesFromStorage(Context context)
    {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("xrays", Context.MODE_PRIVATE);
        File[] files = directory.listFiles();
        ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
        for(File f: files){
            try {
                bitmapArray.add(BitmapFactory.decodeStream(new FileInputStream(f)));
                uploadImage(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return bitmapArray;

    }

    public static List<String> getDiagnostics(Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("xrays", Context.MODE_PRIVATE);
        File[] files = directory.listFiles();
        ArrayList<String> titles = new ArrayList<String>();
        for(File f: files){
            titles.add("Diagnostic: parodontoza");
        }
        return titles;
    }

    public static List<String> getDescriptions(Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("xrays", Context.MODE_PRIVATE);
        File[] files = directory.listFiles();
        List<String> descriptions = new ArrayList<String>();
        for(File f: files){
            descriptions.add(f.getName());

        }
        return descriptions;
    }

    public static void renameFile(Context context, String name, int option){
        ContextWrapper cw = new ContextWrapper(context);
        File oldFile = new File(cw.getDir("xrays", Context.MODE_PRIVATE), name);
        String newName = name.substring(0, 14);
        File newFile = new File(cw.getDir("xrays", Context.MODE_PRIVATE), newName + String.valueOf(option) + ".jpg");
        oldFile.renameTo(newFile);
        Log.d("renaming file", oldFile.getName());
    }

    public static void uploadImage(File to_upload){
        FirebaseStorage storage;
        StorageReference storageReference;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Uri file = Uri.fromFile(to_upload);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userName = user.getEmail();

        String name = file.getLastPathSegment();
        Log.d("Uploading to cloud: ", name);
        int diagnostic = Character.getNumericValue(name.charAt(14));
        String path = "not_classified/";
        switch(diagnostic) {
            case 0: path = "parodonthosis"; break;
            case 1: path = "healthy"; break;
            case 2: path = "parodonthosis"; break;
            case 3: path = "healthy"; break;
            case 4: path = "not_classified"; break;
            case 5: path = "not_classified"; break;
        }
        name = name.substring(0,14) + ".jpg";
        StorageReference riversRef = storageReference.child(userName+ "/" + path + "/" + name);
        deleteImage(userName, path, name);
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

    public static void deleteImage(String userName, String path, String name){
        String[] paths = {"healthy", "parodonthosis", "not_classified"};
        FirebaseStorage storage;
        StorageReference storageReference;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        for(String p: paths){
            if(p != path){
                StorageReference toDelete = storageReference.child(userName + "/" + p + "/" + name);
                toDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Image deleted", userName + "/" + p + "/" + name);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                    }
                });
            }
        }
    }
}
