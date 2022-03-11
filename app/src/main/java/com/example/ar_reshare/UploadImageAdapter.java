package com.example.ar_reshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UploadImageAdapter extends RecyclerView.Adapter<UploadImageAdapter.ViewHolder>{

    private ArrayList<Integer> uploadedImages = new ArrayList<>();

    public UploadImageAdapter(ArrayList<Integer> uploadedImages) {
        this.uploadedImages = uploadedImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_image_list_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageResource(uploadedImages.get(position));
    }

    @Override
    public int getItemCount() {
        return uploadedImages.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.uploadedImage);
            deleteButton = itemView.findViewById(R.id.deleteImage);
        }
    }
}
