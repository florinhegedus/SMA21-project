package com.upt.cti.neuralc.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.upt.cti.neuralc.R;
import com.upt.cti.neuralc.adapters.CustomAdapter;
import com.upt.cti.neuralc.services.ImageService;

import java.util.List;

public class XRaysActivity extends AppCompatActivity {

    private Context applicationContext;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Bitmap> images;
    private List<String> titles;
    private List<String> descriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xrays);

        applicationContext = getApplicationContext();
        images = ImageService.loadImagesFromStorage(applicationContext);
        titles = ImageService.getDiagnostics(applicationContext);
        descriptions = ImageService.getDescriptions(applicationContext);



        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter(this, images, titles, descriptions);
        recyclerView.setAdapter(adapter);
    }

}