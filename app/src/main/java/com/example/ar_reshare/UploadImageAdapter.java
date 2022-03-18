package com.example.ar_reshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.util.ArrayList;
import java.util.List;

public class UploadImageAdapter extends RecyclerView.Adapter<UploadImageAdapter.ViewHolder>{

    private SortedList<Integer> uploadedImages;

    public UploadImageAdapter(ArrayList<Integer> images) {
        uploadedImages = new SortedList<>(Integer.class, new SortedList.Callback<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position,count);
            }

            @Override
            public boolean areContentsTheSame(Integer oldItem, Integer newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Integer item1, Integer item2) {
                return item1.equals(item2);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position,count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {

            }
        });

        uploadedImages.addAll(images);
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
        System.out.println(uploadedImages.get(position));
        System.out.println(uploadedImages);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadedImages.removeItemAt(holder.getAdapterPosition());
            }
        });
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
