package com.example.ccms.Rating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccms.LoadingDialog;
import com.example.ccms.R;
import com.example.ccms.UserBooking.BookingInfo;
import com.example.ccms.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingActivity extends AppCompatActivity implements RatingAdapter.OnItemClickListener {
    private TextView textView;
    private ActionBar actionBar;
    String current_date; String a="Evening",b="Day";
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference ref_evening=db.collection("evening");
    private CollectionReference ref_day= db.collection("day");
    private CollectionReference refRating=db.collection("rating");
    private List<BookingInfo> mUploads;
    private RatingAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(RatingActivity.this, "You need to log in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RatingActivity.this, login.class));
            finish();
        }else {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setOnNavigationItemSelectedListener(navListener);


            textView = (TextView) findViewById(R.id.text_view);

            actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
            actionBar.setTitle("Service Rating");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);


            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            current_date = simpleDateFormat.format(calendar.getTime());

            mRecyclerView = findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mUploads = new ArrayList<>();
            mAdapter = new RatingAdapter(RatingActivity.this, mUploads);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(RatingActivity.this);

            evening_method();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_evening:
                    evening_method();
                    break;
                case R.id.nav_day:
                    day_method();
                    break;
            }
            return true;
        }
    };

    public void evening_method(){
        final LoadingDialog loadingDialog=new LoadingDialog(RatingActivity.this);
        loadingDialog.startLoadingDialog();
        String status="Confirmed";
        String email=mAuth.getCurrentUser().getEmail();
        ref_evening.whereEqualTo("email",email)
                .whereEqualTo("booking_status",status)
                .orderBy("format_booked_date", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    loadingDialog.dismissDialog();
                    textView.setText("You've no Evening Shift booking history to rate");
                }
                else {
                    mUploads.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String b_date=documentSnapshot.getId();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                        Date date=null;
                        try {
                            date=sdf.parse(b_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date2 = null;
                        try {
                            date2 = sdf.parse(current_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        assert date != null;
                        if (date.before(date2)){
                            BookingInfo upload = documentSnapshot.toObject(BookingInfo.class);
                            upload.setKey(documentSnapshot.getId());
                            mUploads.add(upload);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    loadingDialog.dismissDialog();
                    if (mUploads==null || mUploads.isEmpty()){
                        textView.setText("You've no Evening Shift booking history to rate");
                    }
                    else {
                        textView.setText(""); }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismissDialog();
                Toast.makeText(RatingActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
 public void day_method(){
     final LoadingDialog loadingDialog=new LoadingDialog(RatingActivity.this);
     loadingDialog.startLoadingDialog();
     String status="Confirmed";
     String email=mAuth.getCurrentUser().getEmail();
     ref_day.whereEqualTo("email",email)
             .whereEqualTo("booking_status",status)
             .orderBy("format_booked_date", Query.Direction.DESCENDING)
             .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
         @Override
         public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
             if (queryDocumentSnapshots.isEmpty()) {
                 loadingDialog.dismissDialog();
                 textView.setText("You've no Day Shift booking history to rate");
             }
             else {
                 mUploads.clear();
                 for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                     String b_date=documentSnapshot.getId();
                     SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                     Date date=null;
                     try {
                         date=sdf.parse(b_date);
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                     Date date2 = null;
                     try {
                         date2 = sdf.parse(current_date);
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                     assert date != null;
                     if (date.before(date2)){
                         BookingInfo upload = documentSnapshot.toObject(BookingInfo.class);
                         upload.setKey(documentSnapshot.getId());
                         mUploads.add(upload);
                     }
                 }
                 mAdapter.notifyDataSetChanged();
                 loadingDialog.dismissDialog();
                 if (mUploads==null || mUploads.isEmpty()){
                     textView.setText("You've no Day Shift booking history to rate");
                 }
                 else {
                     textView.setText(""); }
             }
         }
     }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             loadingDialog.dismissDialog();
             Toast.makeText(RatingActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
         }
     });

 }

    @Override
    public void onItemClick(int position) {

        final LoadingDialog loadingDialog=new LoadingDialog(RatingActivity.this);
        BookingInfo selectedItem = mUploads.get(position);
        final String bookingId=selectedItem.getBooking_Id();
        final String email=mAuth.getCurrentUser().getEmail();
        final String shift=selectedItem.getShift();

        refRating.document(bookingId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        double r_is=document.getDouble("rating");
                        Toast.makeText(RatingActivity.this, "Already rated "+r_is, Toast.LENGTH_SHORT).show();
                    } else {
                        //document doesn't exist

                        AlertDialog.Builder alert=new AlertDialog.Builder(RatingActivity.this);
                        View mView =getLayoutInflater().inflate(R.layout.rating_dialog,null);

                        final RatingBar ratingBar=mView.findViewById(R.id.rating);
                        Button submit=mView.findViewById(R.id.submit);

                        alert.setView(mView);
                        final AlertDialog alertDialog=alert.create();
                        //alertDialog.setCanceledOnTouchOutside(false);

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                double r = ratingBar.getRating();
                                if (r == 0) {
                                    StyleableToast.makeText(RatingActivity.this, "Enter a minimum value", R.style.toast_alert).show();
                                }
                                else{
                                    loadingDialog.startLoadingDialog();
                                Map<String, Object> rating = new HashMap<>();
                                rating.put("email", email);
                                rating.put("booking_Id", bookingId);
                                rating.put("shift", shift);
                                rating.put("rating", r);
                                refRating.document(bookingId).set(rating).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        loadingDialog.dismissDialog();
                                        StyleableToast.makeText(RatingActivity.this, "Service rating submitted", R.style.toast_sample).show();
                                        alertDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.dismissDialog();
                                        Toast.makeText(RatingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                               }
                            }
                        });

                        alertDialog.show();

                    }
                }
                 else{
                   String e = task.getException().getMessage();
                    Toast.makeText(RatingActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                    }
            }
            });

    }


    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}

