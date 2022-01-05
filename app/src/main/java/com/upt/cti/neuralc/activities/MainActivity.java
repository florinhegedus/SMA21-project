package com.upt.cti.neuralc.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.upt.cti.neuralc.R;
import com.upt.cti.neuralc.services.ImageService;
import com.upt.cti.neuralc.services.Preprocessing;

import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private Context applicationContext;
    Bitmap imageBitmap = null;
    Module module = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            module = LiteModuleLoader.load(assetFilePath(this, "paronet_optimied.ptl"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        Button selectButton = (Button) findViewById(R.id.selectButton);
        Button takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
        Button predictButton = (Button) findViewById(R.id.predictButton);
        Button xRaysButton = (Button) findViewById(R.id.xRaysButton);
        TextView diagnostic = (TextView) findViewById(R.id.diagnostic);
        applicationContext = getApplicationContext();

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagnostic.setVisibility(View.INVISIBLE);
                openGallery();
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagnostic.setVisibility(View.INVISIBLE);
                dispatchTakePictureIntent();
            }
        });

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(imageBitmap != null) {
                    diagnostic.setVisibility(View.VISIBLE);
                    double prediction = Math.random();
                    if(prediction < 0.5) {
                        diagnostic.setText("Result: healthy (" + String.valueOf(prediction).substring(0, 4) + ")");
                        ImageService.saveToInternalStorage(imageBitmap, applicationContext, 5);
                    } else {
                        diagnostic.setText("Result: parodonthosis (" + String.valueOf(prediction).substring(0, 4) + ")");
                        ImageService.saveToInternalStorage(imageBitmap, applicationContext, 4);
                    }
                }
            }
        });

        xRaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, XRaysActivity.class);
                startActivity(intent);
            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
        startActivityForResult(chooser, PICK_IMAGE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 400, 400, false);
            //imageBitmap = Preprocessing.toGrayscale(imageBitmap);
            imageView.setImageBitmap(imageBitmap);
        }
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 400, 400, false);
                //imageBitmap = Preprocessing.toGrayscale(imageBitmap);
                imageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }


}