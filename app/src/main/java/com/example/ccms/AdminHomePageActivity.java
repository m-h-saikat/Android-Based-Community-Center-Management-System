package com.example.ccms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.ccms.AdminBooking.AdminCheckBookingActivity;
import com.example.ccms.AdminBookingRequest.AdminConfirmedActivity;
import com.example.ccms.AdminBookingRequest.AdminPendingRequestActivity;
import com.example.ccms.FAQ.AdminFaqActivity;
import com.example.ccms.Gallery.AdminDeletePhotoActivity;
import com.example.ccms.Gallery.AdminUploadPhotoActivity;
import com.example.ccms.Message.AdminReplyActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomePageActivity extends AppCompatActivity {
    private CardView admin_cd1,admin_cd2,admin_cd3,admin_cd4,admin_cd5,admin_cd0,admin_card01,admin_card02,
    admin_card_new13,admin_card_new21;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        admin_cd1 = (CardView)findViewById(R.id.admin_card1);
        admin_cd2 = (CardView)findViewById(R.id.admin_card2);
        admin_cd3 = (CardView)findViewById(R.id.admin_card3);
        admin_cd4 = (CardView)findViewById(R.id.admin_card4);
        admin_cd0= (CardView)findViewById(R.id.admin_card0);
        admin_card01=(CardView)findViewById(R.id.admin_card01);
        admin_card02=(CardView)findViewById(R.id.admin_card02);
        admin_cd5 = (CardView)findViewById(R.id.admin_card5);
        admin_card_new13=(CardView)findViewById(R.id.admin_card_new13);
        admin_card_new21=(CardView)findViewById(R.id.admin_card_new21);

        admin_cd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomePageActivity.this,AdminEditPackagesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        admin_cd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomePageActivity.this, AdminCheckBookingActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        admin_cd0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomePageActivity.this, AdminDeletePhotoActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        admin_card01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomePageActivity.this, AdminFaqActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        admin_card02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomePageActivity.this, AdminRentalActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        admin_cd5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()!=null){
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(AdminHomePageActivity.this,"Admin Log out successful",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminHomePageActivity.this,MainActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error" , Toast.LENGTH_SHORT).show();
                }
            }
        });

        admin_cd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomePageActivity.this, AdminReplyActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        admin_cd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomePageActivity.this, AdminUploadPhotoActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        admin_card_new13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomePageActivity.this, AdminPendingRequestActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        admin_card_new21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomePageActivity.this, AdminConfirmedActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }
}
