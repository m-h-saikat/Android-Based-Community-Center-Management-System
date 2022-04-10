package com.example.ccms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {
    private TextView email;
    private Button select_photo,update;
   private EditText name,mobile;
   //private ActionBar actionBar;
   private ProgressBar progressBar;
   FirebaseAuth mAuth;
   FirebaseFirestore db;
   private String user_id,user_email;
   private String updated_name=null;
   private String  updated_mobile=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        /*actionBar=getSupportActionBar();
        actionBar.setTitle("");*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        email=(TextView)findViewById(R.id.update_profile_email);
        name=(EditText)findViewById(R.id.update_profile_name);
        mobile=(EditText)findViewById(R.id.update_profile_mobile);
        select_photo=(Button)findViewById(R.id.button_user_image);
        update=(Button)findViewById(R.id.btn_update);
        progressBar=(ProgressBar)findViewById(R.id.update_progressBar);

        if(mAuth.getCurrentUser()!=null){
            String user_email=mAuth.getCurrentUser().getEmail();
            email.setText(user_email);

            String uid = mAuth.getCurrentUser().getUid();
            db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String user_name=documentSnapshot.getString("Name");
                    String user_mobile=documentSnapshot.getString("Mobile");
                    name.setText(user_name);
                    mobile.setText(user_mobile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateProfileActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()!=null){
                update_profile();
                }
                else{
                    Toast.makeText(UpdateProfileActivity.this,"Please Log in",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                }
            }
        });

    }

    public void update_profile(){
        updated_name=name.getText().toString();
        updated_mobile=mobile.getText().toString().trim();
        user_email=mAuth.getCurrentUser().getEmail();

        if(updated_name.isEmpty()){
            name.setError("Enter your name please");
            name.requestFocus();
            return;
        }
        if(updated_mobile.isEmpty()){
            mobile.setError("Enter your mobile number");
            mobile.requestFocus();
            return;
        }
        if(mobile.length()>11||mobile.length()<11 ) {
            mobile.setError("Enter a valid mobile number,Example:01551111111");
            mobile.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        user_id = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("users").document(user_id);
        Map<String, Object> user = new HashMap<>();
        user.put("Mobile",updated_mobile);
        user.put("Name", updated_name);

        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateProfileActivity.this,"Successfully updated",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateProfileActivity.this,MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfileActivity.this,"Error: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

}
