package com.example.ar_reshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {

    Map<Integer, Category> intToCat = new HashMap<>();

    private static final int PROFILE_LINK = 100;
    private static final int PRODUCT_LINK = 101;
    private static final int MESSAGE_LINK = 102;

    private final List<Product> productList;
    public ArrayList<ViewHolder> cards = new ArrayList<>();

    private Location userLocation;
    private boolean locationReady = false;

    private Context context;

    public FeedRecyclerAdapter(List<Product> productList){
        System.out.println("ADAPTER CREATED");
        System.out.println("SIZE OF THE LIST" + productList.size());
        this.productList = productList;
        intToCat.put(1, Category.OTHER);
        intToCat.put(2, Category.CLOTHING);
        intToCat.put(3, Category.ACCESSORIES);
        intToCat.put(4, Category.ELECTRONICS);
        intToCat.put(5, Category.BOOKS);
        intToCat.put(6, Category.HOUSEHOLD);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_view,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Assigns current product
        Product product = productList.get(position);

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
        bookmarkToggleHelper(holder, product);
    }



    public void productValueHelper(ViewHolder holder, Product product) {
        BackendController.getProfileByID(0, 100,
                product.getContributorID(), (success, userProfile) -> {
                    if (success) {
                        ((Activity) context).runOnUiThread(() -> {
                            if (userProfile.getProfilePic() == null) {
                                holder.profileIcon.setImageResource(R.mipmap.ic_launcher_round);
                            } else {
                                holder.profileIcon.setImageBitmap(userProfile.getProfilePic());
                            }
                            holder.contributor.setText(userProfile.getName());
                        });
                    }
                    else {
                        System.out.println("getProfileByID callback failed");
                    }
                });
        holder.categoryIcon.setImageResource(Objects.requireNonNull(
                intToCat.get(product.getCategoryID())).getCategoryIcon());
        holder.productImage.setImageBitmap(product.getMainPic());
        holder.productTitle.setText(product.getName());
        holder.productDescription.setText(product.getDescription());
    }

    public void distanceValueHelper(ViewHolder holder, Product product) {
        if (locationReady) {
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(product.getCoordinates().latitude);
            productLocation.setLongitude(product.getCoordinates().longitude);
            float dist = userLocation.distanceTo(productLocation);
            int roundedDist = Math.round(dist);
            holder.location.setText(MessageFormat.format("{0}m", roundedDist));
        } else {
            holder.location.setText(R.string.calc_distance);
        }
    }

    // Grabs userLocation from FeedActivity and uses it to show distance to products created
    public void updateDistances(Location location) {
        System.out.println(location);
        // Update the location text of already created cards
        for (int i=0; i < cards.size(); i++) {
            ViewHolder card = cards.get(i);
            Product product = productList.get(i);
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(product.getCoordinates().latitude);
            productLocation.setLongitude(product.getCoordinates().longitude);
            float dist = location.distanceTo(productLocation);
            int roundedDist = Math.round(dist);
            card.location.setText(MessageFormat.format("{0}m", roundedDist));
        }

        // Set userLocation for the remaining cards
        userLocation = location;
        locationReady = true;
    }

    public void bookmarkToggleHelper(ViewHolder holder, Product product) {
        if (product.isSavedByUser()) {
            System.out.println("This product has been saved by the user");
            holder.bookmarkButton.setTag(1);
            holder.bookmarkButton.setImageResource(R.drawable.filled_white_bookmark);
        } else {
            holder.bookmarkButton.setTag(0);
            holder.bookmarkButton.setImageResource(R.drawable.white_bookmark);
        }
        holder.bookmarkButton.setOnClickListener(v -> {
            System.out.println("The tag is " + holder.bookmarkButton.getTag());
            if (holder.bookmarkButton.getTag().equals(0)) {
                try {
                    BackendController.createSavedListing(product.getId(), (success, message) -> {
                        System.out.println(message);
                        if (success) {
                            System.out.println("createSavedListing callback success");
                        } else {
                            System.out.println("createSavedListing callback failed");
                        }
                        holder.bookmarkButton.setImageResource(R.drawable.filled_white_bookmark);
                        holder.bookmarkButton.setTag(1);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    BackendController.deleteSavedListing(product.getId(), (success, message) -> {
                        System.out.println(message);
                        if (success) {
                            System.out.println("deleteSavedListing callback success");
                        } else {
                            System.out.println("deleteSavedListing callback failed");
                        }
                        holder.bookmarkButton.setImageResource(R.drawable.white_bookmark);
                        holder.bookmarkButton.setTag(0);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                try {
                    messageClick(v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        // Sends information to the profile page
        public void profileClick(View v) {
            Intent intent = new Intent(v.getContext(), ProfileActivity.class);
            intent.putExtra("userID", product.getContributorID());
            v.getContext().startActivity(intent);
        }

        // Sends information to the product page
        public void productClick(View v) {
            Intent intent = new Intent(v.getContext(), ProductPageActivity.class);
            intent.putExtra("product", product);
            intent.putExtra("contributorID", product.getContributorID());
            intent.putExtra("productID",product.getId());
            intent.putExtra("lat", product.getCoordinates().latitude);
            intent.putExtra("lng",product.getCoordinates().longitude);
            intent.putExtra("categoryID",product.getCategoryID());
            intent.putExtra("postcode",product.getPostcode());
            v.getContext().startActivity(intent);
        }

        // Sends information to the messaging page
        public void messageClick(View v) throws JSONException {
            Intent intent = new Intent(v.getContext(), MessagingActivity.class);
            BackendController.createConversation(product.getId(), (success, message) -> {
                if (success) {
                    System.out.println("conversation created");
                    Integer conversationId = Integer.valueOf(message);
                    intent.putExtra("conversationId", conversationId);
                    intent.putExtra("listingId", product.getId());
                    intent.putExtra("currentUserId", BackendController.getLoggedInUserID());
                    intent.putExtra("contributorId", product.getContributorID());
                    v.getContext().startActivity(intent);
                } else {
                    System.out.println(message);
                    System.out.println("conversation creation failed");
                }
            });
        }
    }
}
