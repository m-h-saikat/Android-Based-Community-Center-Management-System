package com.example.ccms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private TextView signin_text;
    private ActionBar actionBar;
    EditText signup_user_name,signup_user_email,signup_user_mobile,signup_user_pass,signup_user_confirm_pass;
    private Button register_button;
    String userID;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        signin_text=(TextView)findViewById(R.id.signin_text);
        signup_user_name=(EditText)findViewById(R.id.signup_user_name);
        signup_user_email=(EditText)findViewById(R.id.signup_user_email);
        signup_user_mobile=(EditText)findViewById(R.id.signup_user_mobile);
        signup_user_pass=(EditText)findViewById(R.id.signup_user_password);
        signup_user_confirm_pass=(EditText)findViewById(R.id.signup_user_confirm_password);
        register_button=(Button)findViewById(R.id.register_button);
        progressBar=(ProgressBar)findViewById(R.id.signup_user_progressBar);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();


        signin_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin_text.setPaintFlags(signin_text.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                Intent i = new Intent(SignupActivity.this,login.class);
                startActivity(i);
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String MobilePattern="[0-9]{10}";
                final String email,pass,confirm_pass,mobile,name;
                email=signup_user_email.getText().toString().trim();
                pass=signup_user_pass.getText().toString().trim();
                confirm_pass=signup_user_confirm_pass.getText().toString().trim();
                mobile=signup_user_mobile.getText().toString().trim();
                name=signup_user_name.getText().toString();

                 if(name.isEmpty()&&email.isEmpty()&&mobile.isEmpty()&&pass.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                 if(name.isEmpty()){
                     signup_user_name.setError("Enter your name please");
                     signup_user_name.requestFocus();
                     return;
                 }
                if(mobile.isEmpty()){
                    signup_user_mobile.setError("Enter your mobile number");
                    signup_user_mobile.requestFocus();
                    return;
                }
                if(mobile.length()>11||mobile.length()<11 ) {
                    signup_user_mobile.setError("Enter a valid mobile number,Example:01551111111");
                    signup_user_mobile.requestFocus();
                    return;
                }
                if(email.isEmpty()){
                    signup_user_email.setError("You must enter an email address");
                    signup_user_email.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    signup_user_email.setError("Enter a valid email address");
                    signup_user_email.requestFocus();
                    return;
                }

                if(!pass.equals(confirm_pass)){
                    Toast.makeText(getApplicationContext(),"Password and Confirm Password must be same",Toast.LENGTH_SHORT).show();
                    signup_user_pass.requestFocus();
                    signup_user_confirm_pass.requestFocus();
                    return;
                }
                if(pass.length()<6){
                    signup_user_pass.setError("Minimum password length is 6");
                    signup_user_pass.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    userID = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = db.collection("users").document(userID);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("Mobile", mobile);
                                    user.put("Email", email);
                                    user.put("Name", name);
                                    documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                                else {
                                    String e = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
