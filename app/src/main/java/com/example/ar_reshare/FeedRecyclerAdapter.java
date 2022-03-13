package com.example.ar_reshare;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {

    private static final int PROFILE_LINK = 0;
    private static final int PRODUCT_LINK = 1;
    private static final int MESSAGE_LINK = 2;

    private final ArrayList<Product> arrayList;

    public List<ViewHolder> cards = new ArrayList<>();

    private Location userLocation;
    private boolean locationReady = false;

    public FeedRecyclerAdapter(ArrayList<Product> arrayList, Location userLocation){
        this.arrayList = arrayList;
        this.userLocation = userLocation;
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
        // Add the card to the list of cards
        cards.add(holder);

        Product product = arrayList.get(position);
        holder.profileIcon.setImageResource(product.getContributor().getProfileIcon());
        holder.categoryIcon.setImageResource(product.getCategory().getCategoryIcon());
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
        holder.productDescription.setOnClickListener(productClickHandler);

        ClickHandler messageClickHandler = new ClickHandler(product, MESSAGE_LINK);
        holder.messageButton.setOnClickListener(messageClickHandler);

        // Find and display distance to product
        if (locationReady) {
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(product.getLocation().latitude);
            productLocation.setLongitude(product.getLocation().longitude);
            float dist = userLocation.distanceTo(productLocation);
            int roundedDist = Math.round(dist);
            holder.location.setText(roundedDist + " metres away");
        } else {
            holder.location.setText("Calculating distance");
        }

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

    public void updateDistances(Location location) {
        // Update the location text of already created cards
        userLocation = location;
        for (int i=0; i < cards.size(); i++) {
            ViewHolder card = cards.get(i);
            Product product = arrayList.get(i);
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(product.getLocation().latitude);
            productLocation.setLongitude(product.getLocation().longitude);
            float dist = userLocation.distanceTo(productLocation);
            int roundedDist = Math.round(dist);
            card.location.setText(roundedDist + " metres away");
        }

        // Set userLocation for the remaining cards
        locationReady = true;
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
        ImageView categoryIcon;
        TextView location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIcon = itemView.findViewById(R.id.profileIcon);
            contributor = itemView.findViewById(R.id.contributor);
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productDescription = itemView.findViewById(R.id.productDescription);
            messageButton = itemView.findViewById(R.id.messageButton);
            bookmarkButton = itemView.findViewById(R.id.bookmarkButton);
            categoryIcon = itemView.findViewById(R.id.category);
            location = itemView.findViewById(R.id.location);
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
