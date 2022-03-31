package com.example.ar_reshare;

import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.MyViewHolder>  {

    ArrayList<Bitmap> productPicList;

    public SliderAdapter(ArrayList<Bitmap> productPicList) {

        this.productPicList = productPicList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_container,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapter.MyViewHolder holder, int position) {
        holder.view.setImageBitmap(productPicList.get(position));
    }

    @Override
    public int getItemCount() {
        return productPicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView view;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view);
        }
    }





}
