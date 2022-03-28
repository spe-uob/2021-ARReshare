package com.example.ar_reshare;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        Button avatarButton = (Button) findViewById(R.id.avatar);
        avatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"Photos", "Camera"};
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(v.getContext()).setTitle("Choose a Photo")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog1.show();
            }
        });

        Button nicknameButton = (Button) findViewById(R.id.nickname);
        nicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog2 = new AlertDialog.Builder(v.getContext());
                dialog2.setIcon(R.drawable.setting_name);
                dialog2.setTitle("Please insert your new name");

                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.fullname_dialog, null);
                dialog2.setView(view);

                final EditText Firstname = (EditText) view.findViewById(R.id.Firstname);
                final EditText Lastname = (EditText) view.findViewById(R.id.Lastname);

                dialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = Firstname.getText().toString().trim();
                        String b = Lastname.getText().toString().trim();

                        Toast.makeText(v.getContext(), "New name:" + a +"_"+ b, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog2.show();
            }
        });

        Button birthdayButton = (Button) findViewById(R.id.birthday);
        birthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog3 = new AlertDialog.Builder(v.getContext());
                dialog3.setIcon(R.drawable.settings_birthday);
                dialog3.setTitle("Please insert your new birthday");

                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.brithday_dialog, null);
                dialog3.setView(view);

                final EditText Year = (EditText) view.findViewById(R.id.Year);
                final EditText Month = (EditText) view.findViewById(R.id.Month);
                final EditText Day = (EditText) view.findViewById(R.id.Day);

                dialog3.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = Year.getText().toString().trim();
                        String b = Month.getText().toString().trim();
                        String c = Day.getText().toString().trim();

                        Toast.makeText(v.getContext(), "New Birthday:" + a +"_"+ b + "_" + c, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog3.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog3.show();
            }
        });

        Button pwdButton = (Button) findViewById(R.id.pwd);
        pwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog4 = new AlertDialog.Builder(v.getContext());
                dialog4.setIcon(R.drawable.setting_pwd);
                dialog4.setTitle("Please insert your new Password");

                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.pwd_dialog, null);
                dialog4.setView(view);

                final EditText pwd = (EditText) view.findViewById(R.id.pwd);

                dialog4.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = pwd.getText().toString().trim();

                        Toast.makeText(v.getContext(), "New password:" + a, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog4.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog4.show();
            }
        });

        Button emailButton =(Button) findViewById(R.id.email);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog5 = new AlertDialog.Builder(v.getContext());
                dialog5.setIcon(R.drawable.setting_email);
                dialog5.setTitle("Please insert your new email address");

                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.email_dialog, null);
                dialog5.setView(view);

                final EditText email = (EditText) view.findViewById(R.id.email);

                dialog5.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = email.getText().toString().trim();

                        Toast.makeText(v.getContext(), "New Email Address:" + a, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog5.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog5.show();
            }
        });

       Button homeaddressButton = (Button) findViewById(R.id.homeaddress);
       homeaddressButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                AlertDialog.Builder dialog6 = new AlertDialog.Builder(v.getContext());
                dialog6.setIcon(R.drawable.setting_home);
                dialog6.setTitle("Please insert your new home address");

                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.homeaddress_dialog, null);
                dialog6.setView(view);

               final EditText Country = (EditText) view.findViewById(R.id.setting_Country);
               final EditText City = (EditText) view.findViewById(R.id.setting_City);
               final EditText Street = (EditText) view.findViewById(R.id.setting_Street);
               final EditText Postcode = (EditText) view.findViewById(R.id.setting_Postcode);

               dialog6.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String a = Country.getText().toString().trim();
                       String b = City.getText().toString().trim();
                       String c = Street.getText().toString().trim();
                       String d = Postcode.getText().toString().trim();

                       Toast.makeText(v.getContext(), "New Home Address:" + a + "," + b + "," + c + "," + d, Toast.LENGTH_SHORT).show();
                   }
               });
               dialog6.setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               });
                dialog6.show();
           }
       });

        Button logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog7 = new AlertDialog.Builder(v.getContext());
                dialog7.setTitle("Warning").setMessage("Are you sure you want to Log out");
                dialog7.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog7.setNegativeButton("No", null);
                dialog7.show() ;
            }
        });

        Button deleteButton = (Button) findViewById(R.id.detele);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog8 = new AlertDialog.Builder(v.getContext());
                dialog8.setTitle("Warning").setMessage("Are you sure you want to Delete your Account");
                dialog8.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog8.setNegativeButton("No", null);
                dialog8.show();
            }
        });
    }
}