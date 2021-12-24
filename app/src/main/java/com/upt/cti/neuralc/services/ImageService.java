package com.upt.cti.neuralc.services;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    public static void saveToInternalStorage(Bitmap bitmapImage, Context context){
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("xrays", Context.MODE_PRIVATE);

        String timestamp = sdf.format(new Date()) + ".jpg";
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

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return bitmapArray;

    }

    public static List<String> getTitles(Context context) {
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

    public boolean duplicated(Bitmap bitmap, File directory){
        File[] files = directory.listFiles();
        for(File f: files){
            try {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                if(bitmap.equals(b))
                    return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
