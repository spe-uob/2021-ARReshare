package com.example.ar_reshare;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private Button btM;
    private Button btS;
    private ImageButton btB;
    private ImageButton btImto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        User A = ExampleData.getUsers().get(1);
        Product B = ExampleData.getProducts().get(1);

        TextView name = findViewById(R.id.username);
        name.setText(A.getName());

        TextView bio = findViewById(R.id.description);
        bio.setText(A.getBio());

        ImageView avator1 = findViewById(R.id.avator);
        avator1.setImageResource(A.getProfileIcon());

        ImageButton product1 = findViewById(R.id.shared1);
        product1.setImageResource(B.getImages().get(0));

        btM = (Button)findViewById(R.id.btM);
        btM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MessagingActivity.class);
                startActivity(intent);
            }
        });

        btB = (ImageButton)findViewById(R.id.back);
        btB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btImto = (ImageButton) findViewById(R.id.shared1);
        btImto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProductPageActivity.class);
                intent.putExtra("name", A.getName());
                intent.putExtra("product", B);
                intent.putExtra("contributor", B.getContributor());
                intent.putExtra("profilePicId", B.getImages().get(0));
                intent.putIntegerArrayListExtra("productPicId", (ArrayList<Integer>) B.getImages());

                startActivity(intent);
            }
        });
    }
}