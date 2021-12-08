package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class FeedActivity extends AppCompatActivity {

    ArrayList<Product> arrayList = new ArrayList<>();
    List<Product> productsList = ExampleData.getProducts();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // This needs to be done temporarily since not all users have icons, and not all products have images
        arrayList.add(productsList.get(1)); // Artur's product
        arrayList.add(productsList.get(5)); // Arafat's product

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void changeBookmark(View view) {
        ImageView bookmark = findViewById(R.id.bookmarkButton);
        bookmark.setTag(0);
        bookmark.setOnClickListener(v -> {
            if(bookmark.getTag().equals(0)) {
                bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);
                bookmark.setTag(1);
            } else {
                bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                bookmark.setTag(0);
            }
        });
    }

    public void goToMessages(View view) {
        ImageView message = findViewById(R.id.messageButton);
        message.setOnClickListener(v -> {
            v.setTag(arrayList.get(0));
            Intent intent = new Intent(FeedActivity.this, MessagingActivity.class);
            intent.putExtra("product", (Product) v.getTag());
            intent.putExtra("contributor", ((Product) v.getTag()).getContributor());
            intent.putExtra("user", ExampleData.getUsers().get(0));
            startActivity(intent);
        });
    }

    public void goToProductsTitle(View view) {
        TextView productTitle = findViewById(R.id.productTitle);
        productTitle.setOnClickListener(v -> {
            for (Product product : arrayList) {
                if (product.getName() == productTitle.getText()) {
                    v.setTag(product);
                }
            }
            Intent intent = new Intent(FeedActivity.this, ProductPageActivity.class);
            intent.putExtra("product", (Product) v.getTag());
            intent.putExtra("contributor", ((Product) v.getTag()).getContributor());
            intent.putExtra("profilePicId", ((Product) v.getTag()).getContributor().getProfileIcon());
            startActivity(intent);
        });
    }

    public void goToProductsDescription(View view) {
        TextView productDescription = findViewById(R.id.productDescription);
        productDescription.setOnClickListener(v -> {
            for (Product product : arrayList) {
                if (product.getDescription() == productDescription.getText()) {
                    v.setTag(product);
                }
            }
            Intent intent = new Intent(FeedActivity.this, ProductPageActivity.class);
            intent.putExtra("product", (Product) v.getTag());
            intent.putExtra("contributor", ((Product) v.getTag()).getContributor());
            intent.putExtra("profilePicId", ((Product) v.getTag()).getContributor().getProfileIcon());
            startActivity(intent);
        });
    }

    public void goToProductsImage(View view) {
        ImageView productImage = findViewById(R.id.productImage);
        productImage.setOnClickListener(v -> {
            v.setTag(arrayList.get(0));
            Intent intent = new Intent(FeedActivity.this, ProductPageActivity.class);
            intent.putExtra("product", (Product) v.getTag());
            intent.putExtra("contributor", ((Product) v.getTag()).getContributor());
            intent.putExtra("profilePicId", ((Product) v.getTag()).getContributor().getProfileIcon());
            startActivity(intent);
        });
    }


}