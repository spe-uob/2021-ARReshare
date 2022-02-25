package com.example.ar_reshare;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {

    private static final int PROFILE_LINK = 0;
    private static final int PRODUCT_LINK = 1;
    private static final int MESSAGE_LINK = 2;

    private final ArrayList<Product> arrayList;

    public FeedRecyclerAdapter(ArrayList<Product> arrayList){
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
        Product product = arrayList.get(position);
        holder.profileIcon.setImageResource(product.getContributor().getProfileIcon());
        holder.contributor.setText(product.getContributor().getName());
        holder.productImage.setImageResource(product.getImages().get(0));
        holder.productTitle.setText(product.getName());
        holder.productDescription.setText(product.getDescription());

        ClickHandler profileClickHandler = new ClickHandler(product, PROFILE_LINK);
        holder.profileIcon.setOnClickListener(profileClickHandler);
        holder.contributor.setOnClickListener(profileClickHandler);

        ClickHandler productClickHandler = new ClickHandler(product, PRODUCT_LINK);
        holder.productImage.setOnClickListener(productClickHandler);
        holder.productTitle.setOnClickListener(productClickHandler);
        holder.productTitle.setBackgroundColor((int) product.getCategory().getCategoryColour());
        holder.productDescription.setOnClickListener(productClickHandler);
        holder.productDescription.setBackgroundColor((int) product.getCategory().getCategoryColour());

        ClickHandler messageClickHandler = new ClickHandler(product, MESSAGE_LINK);
        holder.messageButton.setOnClickListener(messageClickHandler);

        holder.bookmarkButton.setTag(0);
        holder.bookmarkButton.setOnClickListener(v -> {
            if (holder.bookmarkButton.getTag().equals(0)) {
                holder.bookmarkButton.setImageResource(R.drawable.ic_baseline_bookmark_24);
                holder.bookmarkButton.setTag(1);
            } else {
                holder.bookmarkButton.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                holder.bookmarkButton.setTag(0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileIcon;
        TextView contributor;
        ImageView productImage;
        TextView productTitle;
        TextView productDescription;
        ImageView messageButton;
        ImageView bookmarkButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIcon = itemView.findViewById(R.id.profileIcon);
            contributor = itemView.findViewById(R.id.contributor);
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productDescription = itemView.findViewById(R.id.productDescription);
            messageButton = itemView.findViewById(R.id.messageButton);
            bookmarkButton = itemView.findViewById(R.id.bookmarkButton);
        }
    }

    private static class ClickHandler implements View.OnClickListener {

        Product product;
        int type;

        ClickHandler(Product product, int type) {
            this.product = product;
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            if (type == PROFILE_LINK) {
                profileClick(v);
            }
            if (type == PRODUCT_LINK) {
                productClick(v);
            }
            if (type == MESSAGE_LINK) {
                messageClick(v);
            }
        }

        public void profileClick(View v) {
            Intent intent = new Intent(v.getContext(), ProfileActivity.class);
            intent.putExtra("contributor", product.getContributor());
            intent.putExtra("profilePicId", product.getContributor().getProfileIcon());
            intent.putExtra("bio", product.getContributor().getBio());
            v.getContext().startActivity(intent);
        }

        public void productClick(View v) {
            Intent intent = new Intent(v.getContext(), ProductPageActivity.class);
            intent.putExtra("product", product);
            intent.putExtra("contributor", product.getContributor());
            intent.putExtra("profilePicId", product.getContributor().getProfileIcon());
            intent.putExtra("productPicId", (ArrayList<Integer>) product.getImages());
            v.getContext().startActivity(intent);
        }

        public void messageClick(View v) {
            Intent intent = new Intent(v.getContext(), MessagingActivity.class);
            intent.putExtra("product", product);
            intent.putExtra("contributor", product.getContributor());
            intent.putExtra("profilePicId", product.getContributor().getProfileIcon());
            intent.putExtra("user", ExampleData.getUsers().get(0));
            v.getContext().startActivity(intent);
        }
    }
}
