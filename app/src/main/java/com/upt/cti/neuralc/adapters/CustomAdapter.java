package com.upt.cti.neuralc.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.upt.cti.neuralc.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    Context context;
    List<Bitmap> bitmaps;
    List<String> titles;
    List<String> descriptions;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView titleText;
        TextView descriptionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
        }
    }

    public CustomAdapter(Context context, List<Bitmap> bitmaps, List<String> titles, List<String> descriptions) {
        this.context = context;
        this.bitmaps = bitmaps;
        this.titles = titles;
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
        holder.titleText.setText(titles.get(position));
        holder.descriptionText.setText(descriptions.get(position));
        holder.image.setImageBitmap(bitmaps.get(position));

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
}
