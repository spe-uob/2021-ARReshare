package com.example.ar_reshare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final ArrayList<ModelClass> arrayList;

    public RecyclerAdapter(ArrayList<ModelClass> arrayList){
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_view,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelClass modelClass = arrayList.get(position);
        holder.profileIcon.setImageResource(modelClass.getProfileIcon());
        holder.productImage.setImageResource(modelClass.getProductImage());
        holder.productTitle.setText(modelClass.getProductTitle());
        holder.productDescription.setText(modelClass.getProductDescription());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileIcon;
        ImageView productImage;
        TextView productTitle;
        TextView productDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIcon = itemView.findViewById(R.id.profileIcon);
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productDescription = itemView.findViewById(R.id.productDescription);
        }
    }
}
