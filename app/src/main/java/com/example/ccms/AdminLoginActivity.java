package com.example.ccms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText admin_email,admin_pass;
    private Button admin_login_btn;
    private ProgressBar progressBar;
    private ActionBar actionBar;
    private FirebaseAuth mAuth;
    private String admin = "admin@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mAuth = FirebaseAuth.getInstance();

        admin_email=(EditText)findViewById(R.id.admin_login_email);
        admin_pass=(EditText)findViewById(R.id.admin_login_pass);
        admin_login_btn=(Button) findViewById(R.id.admin_login_button);
        progressBar=(ProgressBar)findViewById(R.id.admin_login_progressBar);

        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        admin_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = admin_email.getText().toString().trim();
                String pass = admin_pass.getText().toString().trim();

                if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail().equals(admin)) {
                    startActivity(new Intent(AdminLoginActivity.this, AdminHomePageActivity.class));
                    finish();
                }
                else if (mAuth.getCurrentUser()!=null && !admin.equals(mAuth.getCurrentUser().getEmail())){
                    Toast.makeText(AdminLoginActivity.this, "You are already logged in as user", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminLoginActivity.this, MainActivity.class));
                    finish();
                }
                else if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(AdminLoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {

                    if (!email.equals(admin)) {
                        admin_email.setError("You are not an admin, check your email again");
                        admin_email.requestFocus();
                        return;
                    }

                    if (mAuth.getCurrentUser() == null && email.equals(admin)) {
                        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                            progressBar.setVisibility(View.VISIBLE);
                            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AdminLoginActivity.this, "Admin Log In Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AdminLoginActivity.this, AdminHomePageActivity.class));
                                        finish();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        String e = task.getException().getMessage();
                                        Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(AdminLoginActivity.this, "You are already logged in as user", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
