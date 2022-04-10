package com.example.ccms;

import androidx.annotation.ColorRes;
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
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
private TextView register,forgot_password;
private ActionBar actionBar;
private EditText user_email,user_pass;
private Button user_login_button;
private FirebaseAuth mAuth;
private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth=FirebaseAuth.getInstance();

        user_email=(EditText)findViewById(R.id.user_email);
        user_pass=(EditText)findViewById(R.id.user_password);
        user_login_button=(Button)findViewById(R.id.user_login_button);
        progressBar=(ProgressBar)findViewById(R.id.login_progressBar);
        register=(TextView)findViewById(R.id.register_text);
        forgot_password=(TextView)findViewById(R.id.forgot_pass);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register.setPaintFlags(register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                Intent i=new Intent(login.this,SignupActivity.class);
                startActivity(i);
            }
        });


        forgot_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                forgot_password.setPaintFlags(forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                String email = user_email.getText().toString().trim();

                if (mAuth.getCurrentUser() != null) {
                    Toast.makeText(getApplicationContext(), "You are already logged in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                else {

                    if (email.isEmpty()) {
                        user_email.setError("Enter your email address then click on CLICK HERE");
                        user_email.requestFocus();
                        return;
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        user_email.setError("Enter a valid email address then click on CLICK HERE");
                        user_email.requestFocus();
                        return;
                    }
                    if (!email.isEmpty()) {
                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(login.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(login.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(login.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });


        user_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    String email, pass;
                    email = user_email.getText().toString().trim();
                    pass = user_pass.getText().toString().trim();

                   if(mAuth.getCurrentUser()==null) {
                       if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {

                           progressBar.setVisibility(View.VISIBLE);
                           mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   progressBar.setVisibility(View.GONE);
                                   if (task.isSuccessful()) {
                                       Toast.makeText(getApplicationContext(), "Log In Successfully", Toast.LENGTH_SHORT).show();
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
                       else {
                           Toast.makeText(getApplicationContext(), "Please fill all the fiels", Toast.LENGTH_SHORT).show();
                           return;
                       }
                   }
                   else{
                       Toast.makeText(getApplicationContext(), "You are already logged in", Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(getApplicationContext(),MainActivity.class));
                   }
            }
        });
    }
}
