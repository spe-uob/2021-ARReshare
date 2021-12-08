package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {
    private Button btM;
    private Button btS;
    private ImageButton btB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        User S = new User("Godblessme", "12345", 1);
        TextView name = findViewById(R.id.username);
        name.setText(S.getName());

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
    }
}