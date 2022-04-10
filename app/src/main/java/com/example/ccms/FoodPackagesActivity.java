package com.example.ccms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FoodPackagesActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private ImageView package1_photo,package2_photo,package3_photo,package4_photo;
    private TextView package1_price,package2_price,package3_price,package4_price;
   private FirebaseAuth mAuth;
   private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_packages);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        package1_photo = (ImageView)findViewById(R.id.package1_photo);
        package2_photo = (ImageView)findViewById(R.id.package2_photo);
        package3_photo = (ImageView)findViewById(R.id.package3_photo);
        package4_photo = (ImageView)findViewById(R.id.package4_photo);

        package1_price = (TextView)findViewById(R.id.package1_price);
        package2_price = (TextView)findViewById(R.id.package2_price);
        package3_price = (TextView)findViewById(R.id.package3_price);
        package4_price = (TextView)findViewById(R.id.package4_price);


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("Food Packages");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(mAuth.getCurrentUser()!=null) {

            db.collection("packages").document("1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String p = null;
                    String price = documentSnapshot.getString("price");
                    p = price + " /=";
                    package1_price.setText(p);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FoodPackagesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            db.collection("packages").document("2").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String p = null;
                    String price = documentSnapshot.getString("price");
                    p = price + " /=";
                    package2_price.setText(p);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FoodPackagesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            db.collection("packages").document("3").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String p = null;
                    String price = documentSnapshot.getString("price");
                    p = price + " /=";
                    package3_price.setText(p);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FoodPackagesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            db.collection("packages").document("4").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String p=null;
                    String price = documentSnapshot.getString("price");
                    p = price + " /=";
                    package4_price.setText(p);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FoodPackagesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(FoodPackagesActivity.this,"To see price, please log in",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
