package com.example.idscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {

    private EditText locationedittext;
    private Button savelocationbutton;
    private String loc_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // hide the title bar and give fullscreen appearance
        getSupportActionBar().hide();

        locationedittext=findViewById(R.id.editTextlocation);
        savelocationbutton=findViewById(R.id.buttonSave);

        savelocationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loc_data = locationedittext.getText().toString();

                //saving the string in shared preferences for later use
                SharedPreferences sharedPreferences = getSharedPreferences("loc_setup",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("loc_saved",loc_data);
                editor.apply();

                //pass the loc_data entered into the idscan activity

                Intent movetoscan = new Intent(login.this, idscan.class);
                movetoscan.putExtra("loc_pass", loc_data);
                startActivity(movetoscan);

            }
        });


    }
}