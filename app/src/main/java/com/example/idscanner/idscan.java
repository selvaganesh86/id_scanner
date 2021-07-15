package com.example.idscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class idscan extends AppCompatActivity {

    private TextView locationtextview;
    private Button scanidbutton;
    private String loc_received;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idscan);

        // hide the title bar and give fullscreen appearance
        getSupportActionBar().hide();

        locationtextview=findViewById(R.id.textViewlocationshow);
        scanidbutton=findViewById(R.id.Buttonscanid);

        loc_received = getIntent().getStringExtra("loc_pass");

        locationtextview.setText("Scan Location: "+loc_received);

    }
}