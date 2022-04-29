package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Fragment {
    public int profilePicId;
    private Product currentProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        Intent i = getActivity().getIntent();
        User contributor = i.getParcelableExtra("contributor");
        String bio;
        if (contributor == null) {
            contributor = ExampleData.getUsers().get(0);
            profilePicId = contributor.getProfileIcon();
            currentProduct = ExampleData.getProducts().get(0);
        } else {
            profilePicId = i.getIntExtra("profilePicId", 0);
            contributor.setProfileIcon(profilePicId);
            bio = i.getStringExtra("bio");
            contributor.setBio(bio);
        }

        List<Product> products = ExampleData.getProducts();

        TextView name = view.findViewById(R.id.username);
        name.setText(contributor.getName());

        TextView bioText = view.findViewById(R.id.description);
        bioText.setText(contributor.getBio());

        ImageView profileIcon = view.findViewById(R.id.avatar);
        profileIcon.setImageResource(contributor.getProfileIcon());

        ImageButton productImage = view.findViewById(R.id.shared1);
        for (Product product : products) {
            if(product.getContributor().getName().equals(contributor.getName())){
                currentProduct = product;
            }
        }
        //productImage.setImageResource(currentProduct.getImages().get(0));

        Button settingButton = (Button) view.findViewById(R.id.btS);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        Button messageButton = (Button) view.findViewById(R.id.btM);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatListActivity.class);
                intent.putExtra("product", currentProduct);
                intent.putExtra("contributor", currentProduct.getContributor());
                intent.putExtra("profilePicId", currentProduct.getContributor().getProfileIcon());
                intent.putExtra("user", ExampleData.getUsers().get(0));
                v.getContext().startActivity(intent);
            }
        });

        Button profileaddButton = (Button) view.findViewById(R.id.profileadd);
        profileaddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddProduct.class);
                startActivity(intent);
            }
        });

        if(contributor.getName() == "John"){
            profilePicId = contributor.getProfileIcon();
        } else {
            messageButton.setVisibility(View.GONE);
            settingButton.setVisibility(View.GONE);
            profileaddButton.setVisibility(View.GONE);
        }

        ImageButton backButton = (ImageButton) view.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductPageActivity.class);
                intent.putExtra("product", currentProduct);
                intent.putExtra("contributor", currentProduct.getContributor());
                intent.putExtra("profilePicId", currentProduct.getContributor().getProfileIcon());
                intent.putExtra("productPicId", (ArrayList<Integer>) currentProduct.getImages());
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile);


    }

//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//    }
}