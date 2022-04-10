package com.example.ccms.AdminBookingRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccms.AdminLoginActivity;
import com.example.ccms.LoadingDialog;
import com.example.ccms.R;
import com.example.ccms.UserBooking.BookingInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

public class AdminPendingRequestActivity extends AppCompatActivity implements AdminBookingAdapter.OnItemClickListener {
    private TextView textView;
    private ActionBar actionBar;
    private AdminBookingAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference ref_evening=db.collection("evening");
    private CollectionReference ref_day= db.collection("day");
    private List<BookingInfo> mUploads;
    String current_date;
    String a="Evening",b="Day";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pending_request);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        textView=(TextView)findViewById(R.id.text_view_no_booking_admin);

        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("Pending Booking Request");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (mAuth.getCurrentUser()==null){
            Toast.makeText(AdminPendingRequestActivity.this,"You need to log in",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AdminPendingRequestActivity.this, AdminLoginActivity.class));
            finish();
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        current_date = simpleDateFormat.format(calendar.getTime());


        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();
        mAdapter = new AdminBookingAdapter(AdminPendingRequestActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(AdminPendingRequestActivity.this);

        evening_sortByBookedDate();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_evening:

                            AlertDialog.Builder alert=new AlertDialog.Builder(AdminPendingRequestActivity.this);
                            View mView =getLayoutInflater().inflate(R.layout.sort_dialog,null);
                            Button bookedDate=(Button)mView.findViewById(R.id.sort1);
                            Button requestDate=(Button)mView.findViewById(R.id.sort2);

                            alert.setView(mView);
                            final AlertDialog alertDialog=alert.create();
                            alertDialog.setCanceledOnTouchOutside(false);

                            bookedDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    evening_sortByBookedDate();
                                }
                            });
                            requestDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    evening_sortByRequestDate();
                                }
                            });
                            alertDialog.show();
                            break;

                        case R.id.nav_day:
                            AlertDialog.Builder alert2=new AlertDialog.Builder(AdminPendingRequestActivity.this);
                            View mView2 =getLayoutInflater().inflate(R.layout.sort_dialog,null);
                            Button bookedDate2=(Button)mView2.findViewById(R.id.sort1);
                            Button requestDate2=(Button)mView2.findViewById(R.id.sort2);

                            alert2.setView(mView2);
                            final AlertDialog alertDialog2=alert2.create();
                            alertDialog2.setCanceledOnTouchOutside(false);

                            bookedDate2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog2.dismiss();
                                    day_sortByBookedDate();
                                }
                            });
                            requestDate2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog2.dismiss();
                                    day_sortByRequestDate();
                                }
                            });
                            alertDialog2.show();
                            break;
                    }

                    return true;
                }
            };

public void evening_sortByBookedDate(){
    final LoadingDialog loadingDialog=new LoadingDialog(AdminPendingRequestActivity.this);
    loadingDialog.startLoadingDialog();
    String checker="Not Confirmed";
    ref_evening.whereEqualTo("booking_status",checker)
            .orderBy("format_booked_date", Query.Direction.ASCENDING)
            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if (queryDocumentSnapshots.isEmpty()) {
                loadingDialog.dismissDialog();
                textView.setText("No pending booking request found for Evening shift");
            } else {
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
                    if (date.after(date2)){
                        BookingInfo upload = documentSnapshot.toObject(BookingInfo.class);
                        upload.setKey(documentSnapshot.getId());
                        mUploads.add(upload);
                    }
                }
                mAdapter.notifyDataSetChanged();
                loadingDialog.dismissDialog();
                if (mUploads==null || mUploads.isEmpty()){
                    textView.setText("No pending booking request found for Evening shift");
                }
                else {
                    textView.setText(""); }
                //progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            //progressBar.setVisibility(View.GONE);
            loadingDialog.dismissDialog();
            Toast.makeText(AdminPendingRequestActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    });
}

public void day_sortByBookedDate(){
    final LoadingDialog loadingDialog=new LoadingDialog(AdminPendingRequestActivity.this);
    loadingDialog.startLoadingDialog();
    String checker="Not Confirmed";
    ref_day.whereEqualTo("booking_status",checker)
            .orderBy("format_booked_date", Query.Direction.ASCENDING)
            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if (queryDocumentSnapshots.isEmpty()) {
                loadingDialog.dismissDialog();
                textView.setText("No pending booking request found for Day shift");
            } else {
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
                    if (date.after(date2)){
                        BookingInfo upload = documentSnapshot.toObject(BookingInfo.class);
                        upload.setKey(documentSnapshot.getId());
                        mUploads.add(upload);
                    }
                }
                mAdapter.notifyDataSetChanged();
                loadingDialog.dismissDialog();
                if (mUploads==null || mUploads.isEmpty()){
                    textView.setText("No pending booking request found for Day shift");
                }
                else {
                    textView.setText(""); }
                //progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            //progressBar.setVisibility(View.GONE);
            loadingDialog.dismissDialog();
            Toast.makeText(AdminPendingRequestActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    });

}

public void evening_sortByRequestDate(){
    final LoadingDialog loadingDialog=new LoadingDialog(AdminPendingRequestActivity.this);
    loadingDialog.startLoadingDialog();
    String checker="Not Confirmed";
    ref_evening.whereEqualTo("booking_status",checker)
            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if (queryDocumentSnapshots.isEmpty()) {
                loadingDialog.dismissDialog();
                textView.setText("No pending booking request found");
            } else {
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
                    if (date.after(date2)){
                        BookingInfo upload = documentSnapshot.toObject(BookingInfo.class);
                        upload.setKey(documentSnapshot.getId());
                        mUploads.add(upload);
                    }
                }
                mAdapter.notifyDataSetChanged();
                loadingDialog.dismissDialog();
                if (mUploads==null || mUploads.isEmpty()){
                    textView.setText("No pending booking request found for Evening shift");
                }
                else {
                    textView.setText(""); }
                //progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            //progressBar.setVisibility(View.GONE);
            loadingDialog.dismissDialog();
            Toast.makeText(AdminPendingRequestActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    });
}

public void day_sortByRequestDate(){
    final LoadingDialog loadingDialog=new LoadingDialog(AdminPendingRequestActivity.this);
    loadingDialog.startLoadingDialog();
    String checker="Not Confirmed";
    ref_day.whereEqualTo("booking_status",checker)
            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if (queryDocumentSnapshots.isEmpty()) {
                loadingDialog.dismissDialog();
                textView.setText("No pending booking request found");
            } else {
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
                    if (date.after(date2)){
                        BookingInfo upload = documentSnapshot.toObject(BookingInfo.class);
                        upload.setKey(documentSnapshot.getId());
                        mUploads.add(upload);
                    }
                }
                mAdapter.notifyDataSetChanged();
                loadingDialog.dismissDialog();
                if (mUploads==null || mUploads.isEmpty()){
                    textView.setText("No pending booking request found for Day shift");
                }
                else {
                    textView.setText(""); }
                //progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            //progressBar.setVisibility(View.GONE);
            loadingDialog.dismissDialog();
            Toast.makeText(AdminPendingRequestActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    });

}


    @Override
    public void onItemClick(int position) {
        BookingInfo selectedItem = mUploads.get(position);
        // Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alert=new AlertDialog.Builder(AdminPendingRequestActivity.this);
        View mView =getLayoutInflater().inflate(R.layout.booking_info_dialogue,null);

        ImageView cancel=(ImageView)mView.findViewById(R.id.icon_cancel);
        Button close_btn=(Button)mView.findViewById(R.id.close_button);

        TextView booking_Id=(TextView)mView.findViewById(R.id.booking_id);
        booking_Id.setText(selectedItem.getBooking_Id());
        TextView shift=(TextView)mView.findViewById(R.id.shift);
        shift.setText(selectedItem.getShift());
        TextView booked_date=(TextView)mView.findViewById(R.id.bookedDate);
        booked_date.setText(selectedItem.getBooked_date());
        TextView request_date=(TextView)mView.findViewById(R.id.requestDate);
        String d=selectedItem.getDate_of_booking_request()+" "+selectedItem.getBooking_time();
        request_date.setText(d);
        TextView event_type=(TextView)mView.findViewById(R.id.eventType);
        event_type.setText(selectedItem.getEvent_type());
        TextView guests=(TextView)mView.findViewById(R.id.guests);
        guests.setText(selectedItem.getNumber_of_guests());
        TextView cost=(TextView)mView.findViewById(R.id.cost);
        cost.setText(selectedItem.getHall_rental_price());
        TextView payment=(TextView)mView.findViewById(R.id.paymentStatus);
        payment.setText(selectedItem.getPayment_status());
        TextView booking_status=(TextView)mView.findViewById(R.id.bookingStatus);
        booking_status.setText(selectedItem.getBooking_status());
        TextView email=(TextView)mView.findViewById(R.id.Email);
        email.setText(selectedItem.getEmail());
        TextView name=(TextView)mView.findViewById(R.id.Name);
        name.setText(selectedItem.getName());
        TextView mobile=(TextView)mView.findViewById(R.id.Mobile);
        mobile.setText(selectedItem.getMobile());
        TextView add_mobile=(TextView)mView.findViewById(R.id.AddMobile);
        add_mobile.setText(selectedItem.getAdditional_mobile());
        TextView address=(TextView)mView.findViewById(R.id.Address);
        address.setText(selectedItem.getAddress());


        alert.setView(mView);
        final AlertDialog alertDialog=alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }


    @Override
    public void onCancelClick(final int position){
        final BookingInfo selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminPendingRequestActivity.this);
        builder1.setMessage("\nDo you want to cancel the booking request");
        builder1.setCancelable(true);
        builder1.setIcon(R.drawable.alert_icon);
        builder1.setTitle("Booking Cancellation");

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final LoadingDialog loadingDialog=new LoadingDialog(AdminPendingRequestActivity.this);
                loadingDialog.startLoadingDialog();

                String shift=selectedItem.getShift();

                if (shift.equals(a)){
                    ref_evening.document(selectedKey).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loadingDialog.dismissDialog();
                            StyleableToast.makeText(AdminPendingRequestActivity.this,"Booking Cancelled",R.style.toast_sample).show();
                            mUploads.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(AdminPendingRequestActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if (shift.equals(b)){
                    ref_day.document(selectedKey).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loadingDialog.dismissDialog();
                            StyleableToast.makeText(AdminPendingRequestActivity.this,"Booking Cancelled",R.style.toast_sample).show();
                            mUploads.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(AdminPendingRequestActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    loadingDialog.dismissDialog();
                    Toast.makeText(AdminPendingRequestActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();


    }



    @Override
    public void onConfirmClick(final int position){
       final BookingInfo selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminPendingRequestActivity.this);
        builder1.setMessage("\nDo you want to confirm the booking");
        builder1.setCancelable(true);
        builder1.setTitle("Booking Confirmation");
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                final LoadingDialog loadingDialog=new LoadingDialog(AdminPendingRequestActivity.this);
                loadingDialog.startLoadingDialog();

                String shift=selectedItem.getShift();
                String status="Confirmed";
                Map<String,Object>confirm=new HashMap<>();
                confirm.put("payment_status",status);
                confirm.put("booking_status",status);
                if (shift.equals(a)){
                    ref_evening.document(selectedKey).update(confirm).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loadingDialog.dismissDialog();
                            StyleableToast.makeText(AdminPendingRequestActivity.this,"Booking Confirmed",R.style.toast_sample).show();
                            mUploads.remove(position);
                            mAdapter.notifyItemRemoved(position);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(AdminPendingRequestActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if (shift.equals(b)){
                    ref_day.document(selectedKey).update(confirm).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loadingDialog.dismissDialog();
                            StyleableToast.makeText(AdminPendingRequestActivity.this,"Booking Confirmed",R.style.toast_sample).show();
                            mUploads.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(AdminPendingRequestActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    loadingDialog.dismissDialog();
                    Toast.makeText(AdminPendingRequestActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
