package com.example.idscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class idscan extends AppCompatActivity {

    private TextView locationtextview;
    private TextView scannedtextview;
    private Button scanidbutton;
    public String loc_received;
    private IntentIntegrator qrScan;
    //private String scan_key ="9951970";
    //private String scan_user="selva86.junk@gmail.com";
    private String scan_key ;
    private String scan_user;
    private TextView fnametextview;
    private TextView lnametextview;
    private TextView validtilltextview;
    private ImageView userphotoimageview;
    private ProgressBar progressbar;
    private CardView cardview;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    scandetails sdetail = new scandetails();
//tg_edit_begin
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

    if (result != null) {
        //if qrcode has nothing in it
        if (result.getContents() == null) {
            Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_LONG).show();
            //scannedtextview.setText("Invalid QR Code");
        } else {
            //if qr contains data
            try {

                String scannedString = new String(result.getContents());
                String[] separatedScannedString = scannedString. split("#");//note if document  path to firebase goes NULL - means this delim was not found
                scan_key=separatedScannedString[1];
                scan_user=separatedScannedString[0];
                Log.i("scanned_user_details", "while scanning: " + this.scan_user);
                //scannedtextview.setText("Key-> " +separated[0]+ "  User-> "+separated[1]);//no need to show scanned text errors are shown via toast//tg_edit
                //scannedtextview.setText(scan_user);//no need to show scanned text errors are shown via toast//tg_edit
                //Toast.makeText(this, scannedString, Toast.LENGTH_LONG).show();
                //firestore_connect_begin
                db.collection("users").document(scan_user)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("user_read", "onComplete: users document read from firestore");
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        Log.d("scan_check", "onComplete: " + scan_key + " " + documentSnapshot.get("temp_key"));
                                        if (documentSnapshot.get("temp_key").toString().equals(scan_key)) {
                                            Log.d("scan_check", "verification success: " + documentSnapshot.get("temp_key"));
                                            sdetail.setResult("Success");

                                            // write details into firestore
                                            db.collection("entry_log/" + scan_user + "/logs")
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
                                                                    Log.d("user_photo", "onError: " + e.toString());
                                                                    progressbar.setVisibility(View.GONE);
                                                                }
                                                            }
                                                    );
                                        } else {
                                            sdetail.setResult("Failure");
                                            // write into firestore
                                            db.collection("entry_log/" + scan_user + "/logs")
                                                    .document()
                                                    .set(sdetail);
                                            progressbar.setVisibility(View.GONE);
                                            Toast.makeText(idscan.this, "Invalid QR Code", Toast.LENGTH_LONG).show();

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

//firestore_connect_end

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            }
        }
    } else {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
//tg_edit_end
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idscan);

        // hide the title bar and give fullscreen appearance
        getSupportActionBar().hide();

        locationtextview=findViewById(R.id.textViewlocationshow);
        scanidbutton=findViewById(R.id.Buttonscanid);
        //scannedtextview = (TextView) findViewById(R.id.scannedtextview);//no need to show scanned text errors are shown via toast//tg_edit
        fnametextview = findViewById(R.id.textViewdatafname);
        lnametextview = findViewById(R.id.textViewdatalname);
        validtilltextview = findViewById(R.id.textViewdatavalid);
        userphotoimageview = findViewById(R.id.imageviewuserphoto);
        progressbar = findViewById(R.id.progressBar);
        cardview = findViewById(R.id.cview);
        qrScan = new IntentIntegrator(this);
        loc_received = getIntent().getStringExtra("loc_pass");

        locationtextview.setText("Scan Location: "+loc_received);
        //test_1_begin

        //tset1-end






        scanidbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);

                sdetail.setLocation(loc_received);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH-mm-ss");
                String timestampformat = simpleDateFormat.format(new Date());
                sdetail.setTimestamp(timestampformat);
                //tg_edit_begin

                qrScan.initiateScan();

                //tg_edit_end++need to remove below 2 Lines once scanning actualCodes
                //scan_key ="9951970";
                //scan_user="selva86.junk@gmail.com";
                //scan_user= (String) scannedtextview.getText();
                Log.i("scanned_user_details", "while entering FIREBASE: " + scan_user);
                //tg_changing the-below to allow string input

            }
        });

    }


}
