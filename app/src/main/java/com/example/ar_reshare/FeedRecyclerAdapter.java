package com.example.ar_reshare;

import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {

    private static final int PROFILE_LINK = 0;
    private static final int PRODUCT_LINK = 1;
    private static final int MESSAGE_LINK = 2;

    private final List<Product> productList;
    public ArrayList<ViewHolder> cards = new ArrayList<>();

    private int maxDistanceRange;
    private Set<Category> categoriesSelected;
    private boolean filterReady = false;

    private Location userLocation;
    private boolean locationReady = false;

    public FeedRecyclerAdapter(List<Product> productList){
        this.productList = productList;
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
        // Assigns current product
        Product product = productList.get(position);

        // Removes current product if not fit in criteria
        if (!(filterHelper(product))) return;

        // Add the product card to the list of product cards
        cards.add(holder);

        // Set values to various resources depending on the product
        productValueHelper(holder, product);

        // Handle clicks to go to the contributor's profile page
        ClickHandler profileClickHandler = new ClickHandler(product, PROFILE_LINK);
        holder.profileIcon.setOnClickListener(profileClickHandler);
        holder.contributor.setOnClickListener(profileClickHandler);

        // Handle clicks to go to the product page
        ClickHandler productClickHandler = new ClickHandler(product, PRODUCT_LINK);
        holder.productImage.setOnClickListener(productClickHandler);
        holder.productTitle.setOnClickListener(productClickHandler);
        holder.productDescription.setOnClickListener(productClickHandler);

        // Handle click to message the contributor
        ClickHandler messageClickHandler = new ClickHandler(product, MESSAGE_LINK);
        holder.messageButton.setOnClickListener(messageClickHandler);

        // Find and display distance to product to be created
        distanceValueHelper(holder, product);

        // Bookmark button logic
        bookmarkToggleHelper(holder);
    }

    public void productValueHelper(ViewHolder holder, Product product) {
        holder.profileIcon.setImageResource(product.getContributor().getProfileIcon());
        holder.categoryIcon.setImageResource(product.getCategory().getCategoryIcon());
        holder.contributor.setText(product.getContributor().getName());
        holder.productImage.setImageResource(product.getImages().get(0));
        holder.productTitle.setText(product.getName());
        holder.productDescription.setText(product.getDescription());
    }

    public void distanceValueHelper(ViewHolder holder, Product product) {
        if (locationReady) {
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(product.getLocation().latitude);
            productLocation.setLongitude(product.getLocation().longitude);
            float dist = userLocation.distanceTo(productLocation);
            int roundedDist = Math.round(dist);
            holder.location.setText(MessageFormat.format("{0}m", roundedDist));
        } else {
            holder.location.setText(R.string.calc_distance);
        }
    }

    public boolean filterHelper(Product product) {
        if (filterReady) {
            Category productCategory = product.getCategory();
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(product.getLocation().latitude);
            productLocation.setLongitude(product.getLocation().longitude);
            float dist = userLocation.distanceTo(productLocation);
            return (!(dist > maxDistanceRange)) && !categoriesSelected.contains(productCategory);
        } else return true;
    }

    public void updateFilter(Location location, int maxDistance, Set<Category>catSelected) {
        for (int i=0; i < cards.size(); i++) {
            ViewHolder card = cards.get(i);
            Product product = productList.get(i);
            Category productCategory = product.getCategory();
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(product.getLocation().latitude);
            productLocation.setLongitude(product.getLocation().longitude);
            float dist = location.distanceTo(productLocation);
            if ((dist > maxDistance) || catSelected.contains(productCategory)) {
                cards.remove(card);
                productList.remove(product);
            }
        }

        userLocation = location;
        maxDistanceRange = maxDistance;
        categoriesSelected = catSelected;
        filterReady = true;
    }

    // Grabs userLocation from FeedActivity and uses it to show distance to products created
    public void updateDistances(Location location) {
        // Update the location text of already created cards
        for (int i=0; i < cards.size(); i++) {
            ViewHolder card = cards.get(i);
            Product product = productList.get(i);
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(product.getLocation().latitude);
            productLocation.setLongitude(product.getLocation().longitude);
            float dist = location.distanceTo(productLocation);
            int roundedDist = Math.round(dist);
            card.location.setText(MessageFormat.format("{0}m", roundedDist));
        }

        // Set userLocation for the remaining cards
        userLocation = location;
        locationReady = true;
    }

    public void bookmarkToggleHelper(ViewHolder holder) {
        holder.bookmarkButton.setTag(0);
        holder.bookmarkButton.setOnClickListener(v -> {
            if (holder.bookmarkButton.getTag().equals(0)) {
                holder.bookmarkButton.setImageResource(R.drawable.filled_white_bookmark);
                holder.bookmarkButton.setTag(1);
            } else {
                holder.bookmarkButton.setImageResource(R.drawable.white_bookmark);
                holder.bookmarkButton.setTag(0);
            }
        });
    }

    // Returns the amount of live products
    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Inner class to set id's to the various parts of a card
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

    // Helps transfer information to different pages that are going to follow
    // after a click
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

        // Sends information to the profile page
        public void profileClick(View v) {
            Intent intent = new Intent(v.getContext(), ProfileActivity.class);
            intent.putExtra("contributor", product.getContributor());
            intent.putExtra("profilePicId", product.getContributor().getProfileIcon());
            intent.putExtra("bio", product.getContributor().getBio());
            v.getContext().startActivity(intent);
        }

        // Sends information to the product page
        public void productClick(View v) {
            Intent intent = new Intent(v.getContext(), ProductPageActivity.class);
            intent.putExtra("product", product);
            intent.putExtra("contributor", product.getContributor());
            intent.putExtra("profilePicId", product.getContributor().getProfileIcon());
            intent.putExtra("productPicId", (ArrayList<Integer>) product.getImages());
            v.getContext().startActivity(intent);
        }

        // Sends information to the messaging page
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
