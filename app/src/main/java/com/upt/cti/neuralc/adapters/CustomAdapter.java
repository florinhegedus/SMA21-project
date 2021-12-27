package com.upt.cti.neuralc.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.upt.cti.neuralc.R;
import com.upt.cti.neuralc.services.ImageService;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    Context context;
    List<Bitmap> bitmaps;
    List<String> diagnostics;
    List<String> descriptions;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView titleText;
        TextView descriptionText;
        TextView userVerdict;
        Button positive;
        Button negative;
        String fileName;
        int verdicts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            userVerdict = itemView.findViewById(R.id.userVerdict);
            positive = itemView.findViewById(R.id.positive);
            negative = itemView.findViewById(R.id.negative);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("item", "clicked item");
                }
            });

            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("button", descriptionText.getText().toString() + " true");
                    userVerdict.setText("Your verdict: parodonthosis");
                    ImageService.renameFile(itemView.getContext(), fileName, 0);
                }
            });

            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("button", descriptionText.getText().toString() + " false");
                    userVerdict.setText("Your verdict: healthy");
                    ImageService.renameFile(itemView.getContext(), fileName, 1);
                }
            });
        }
    }

    public CustomAdapter(Context context, List<Bitmap> bitmaps, List<String> diagnostics, List<String> descriptions) {
        this.context = context;
        this.bitmaps = bitmaps;
        this.diagnostics = diagnostics;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
        holder.titleText.setText(diagnostics.get(position));
        SimpleDateFormat oldSDF = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat newSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            holder.descriptionText.setText(newSDF.format(oldSDF.parse(descriptions.get(position).substring(0, 14))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.image.setImageBitmap(bitmaps.get(position));
        holder.fileName = descriptions.get(position);
        holder.verdicts = Character.getNumericValue(holder.fileName.charAt(14));
        switch(holder.verdicts){
            case 0: holder.userVerdict.setText("Your verdict: parodonthosis"); break;
            case 1: holder.userVerdict.setText("Your verdict: healthy"); break;
            case 2: holder.userVerdict.setText("Your verdict: parodonthosis"); break;
            case 3: holder.userVerdict.setText("Your verdict: healthy"); break;
            default: holder.userVerdict.setText("Your verdict: none");
        }


    }

    @Override
    public int getItemCount() {
        return diagnostics.size();
    }
}
