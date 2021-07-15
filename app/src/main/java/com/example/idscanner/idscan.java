package com.example.idscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class idscan extends AppCompatActivity {

    private TextView locationtextview;
    private Button scanidbutton;
    public String loc_received;
    private String scan_key ="9951970";
    private String scan_user="selva86.junk@gmail.com";

    private TextView fnametextview;
    private TextView lnametextview;
    private TextView validtilltextview;
    private ImageView userphotoimageview;
    private ProgressBar progressbar;
    private CardView cardview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idscan);

        // hide the title bar and give fullscreen appearance
        getSupportActionBar().hide();

        locationtextview=findViewById(R.id.textViewlocationshow);
        scanidbutton=findViewById(R.id.Buttonscanid);

        fnametextview = findViewById(R.id.textViewdatafname);
        lnametextview = findViewById(R.id.textViewdatalname);
        validtilltextview = findViewById(R.id.textViewdatavalid);
        userphotoimageview = findViewById(R.id.imageviewuserphoto);
        progressbar = findViewById(R.id.progressBar);
        cardview = findViewById(R.id.cview);

        loc_received = getIntent().getStringExtra("loc_pass");

        locationtextview.setText("Scan Location: "+loc_received);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        scandetails sdetail = new scandetails();


        scanidbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);

                sdetail.setLocation(loc_received);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH-mm-ss");
                String timestampformat = simpleDateFormat.format(new Date());
                sdetail.setTimestamp(timestampformat);

                db.collection("users").document("selva86.junk@gmail.com")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    Log.d("user_read", "onComplete: users document read from firestore");
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()){
                                        Log.d("scan_check", "onComplete: "+scan_key+" "+documentSnapshot.get("temp_key"));
                                        if (documentSnapshot.get("temp_key").toString().equals(scan_key)){
                                            Log.d("scan_check", "verification success: "+documentSnapshot.get("temp_key"));
                                            sdetail.setResult("Success");

                                            // write details into firestore
                                            db.collection("entry_log/"+scan_user+"/logs")
                                                    .document()
                                                    .set(sdetail);

                                            fnametextview.setText(documentSnapshot.get("fname").toString());
                                            lnametextview.setText(documentSnapshot.get("lname").toString());
                                            validtilltextview.setText(documentSnapshot.get("valid_till").toString());

                                            Picasso.get().load(documentSnapshot.get("photo_loc").toString())
                                                    .into(userphotoimageview, new Callback() {
                                                        @Override
                                                        public void onSuccess() {
                                                            //update UI to stop progressbar and show the cardview
                                                            Log.d("picasso", "onSuccess: ");
                                                            progressbar.setVisibility(View.GONE);
                                                            cardview.setVisibility(View.VISIBLE);
                                                        }

                                                        @Override
                                                        public void onError(Exception e) {
                                                            Log.d("user_photo", "onError: "+e.toString());
                                                            progressbar.setVisibility(View.GONE);
                                                        }
                                                    }
                                        );}
                                        else {
                                            sdetail.setResult("Failure");

                                            // write into firestore
                                            db.collection("entry_log/"+scan_user+"/logs")
                                                    .document()
                                                    .set(sdetail);
                                            progressbar.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("read_user", "onFailure: user key not read from firestore");
                        progressbar.setVisibility(View.GONE);
                    }
                });



            }
        });

    }
}
