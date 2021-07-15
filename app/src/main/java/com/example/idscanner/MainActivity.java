package com.example.idscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide the title bar and give fullscreen appearance
        getSupportActionBar().hide();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(MainActivity.this, login.class));
            }
        }, 2000);
    }
}