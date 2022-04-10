package com.example.ccms.AdminBooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccms.LoadingDialog;
import com.example.ccms.R;
import com.example.ccms.UserBooking.BookingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminConfirmCancelBookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private ActionBar actionBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView id,booked_date,request_date,shift,email,name,mobile,add_mobile,event,payment,booking_status,booked_by;
    private Button check_button,cancel,confirm,search; private ProgressBar progressBar,last_progressBar;
    private ImageButton date_picker;private TextView dateText;
    private String selected_date = null; EditText bookingID;
    private RadioGroup radio_shift;private String selected_shift;
    private RadioButton radioButton;
    private String shift_to_delete_or_confirm=null,booked_date_to_delete_or_confirm=null;
    private String a="Evening"; private String b="Day";
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference ref_evening=db.collection("evening");
    private CollectionReference ref_day= db.collection("day");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_confirm_cancel_booking);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {        // do anything
                id.setText("");
                shift.setText("");
                booked_date.setText("");
                email.setText("");
                name.setText("");
                mobile.setText("");
                add_mobile.setText("");
                request_date.setText("");
                event.setText("");
                payment.setText("");
                booking_status.setText("");
                booked_by.setText("");
                selected_date=null;
                dateText.setText("");
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        id=(TextView)findViewById(R.id.booking_id);
        shift=(TextView)findViewById(R.id.shift);
        booked_date=(TextView)findViewById(R.id.booked_date);
        request_date=(TextView)findViewById(R.id.request_date);
        email=(TextView)findViewById(R.id.email);
        name=(TextView)findViewById(R.id.name);
        mobile=(TextView)findViewById(R.id.mobile);
        add_mobile=(TextView)findViewById(R.id.a_mobile);
        event=(TextView)findViewById(R.id.event_type);
        payment=(TextView)findViewById(R.id.payment);
        booking_status=(TextView)findViewById(R.id.booking_status);
        booked_by=(TextView)findViewById(R.id.booked_by);

        bookingID=(EditText)findViewById(R.id.bookingID);
        search=(Button)findViewById(R.id.search);

        date_picker = (ImageButton) findViewById(R.id.date_pick);
        radio_shift=(RadioGroup)findViewById(R.id.radio_shift);
        dateText=(TextView)findViewById(R.id.date_text);
        check_button=(Button)findViewById(R.id.check_btn);
        cancel=(Button)findViewById(R.id.cancel);
        confirm=(Button)findViewById(R.id.confirm);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        last_progressBar=(ProgressBar)findViewById(R.id.last_progressBar);


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("Confirm/Cancel Booking");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (selected_shift==null) {
                    Toast.makeText(AdminConfirmCancelBookingActivity.this, " select shift", Toast.LENGTH_SHORT).show();
                } else if (selected_date==null) {
                    Toast.makeText(AdminConfirmCancelBookingActivity.this, " select date", Toast.LENGTH_SHORT).show();
                }
                 else{
                     display_booking_status();
                 }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID=bookingID.getText().toString().trim();
                if (ID.isEmpty()|| ID==null){
                    bookingID.setError("Enter a booking Id");
                    bookingID.requestFocus();
                    return;
                }
                char firstCharacter=ID.charAt(0);

                if (ID.charAt(1)!='#'){
                    bookingID.setError("Invalid booking Id");
                    bookingID.requestFocus();
                    return;
                }
               else if (firstCharacter=='E'){
                    show_booking_info_eve();
                }
                else if (firstCharacter=='D'){
                    show_booking_info_day();
                }
                else {
                    bookingID.setError("Invalid booking Id");
                    bookingID.requestFocus();
                    return;
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shift_to_delete_or_confirm==null || booked_date_to_delete_or_confirm==null) {
                    StyleableToast.makeText(AdminConfirmCancelBookingActivity.this, "Booking request to cancel is null", R.style.toast_alert).show();
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminConfirmCancelBookingActivity.this);
                    builder1.setMessage("\nDo you want to cancel the booking request? It'll delete this booking info from database");
                    builder1.setCancelable(true);
                    builder1.setTitle("Booking Cancellation");
                    builder1.setIcon(R.drawable.alert_icon);

                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                last_progressBar.setVisibility(View.VISIBLE);
                                delete_booking_info();
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
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shift_to_delete_or_confirm==null || booked_date_to_delete_or_confirm==null) {
                    StyleableToast.makeText(AdminConfirmCancelBookingActivity.this, "Booking status to to confirm is null", R.style.toast_alert).show();
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminConfirmCancelBookingActivity.this);
                    builder1.setMessage("\nDo you want to confirm the booking status?");
                    builder1.setCancelable(true);
                    builder1.setTitle("Booking status confirmation");

                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                last_progressBar.setVisibility(View.VISIBLE);
                                confirm_booking_info();
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
            }
        });


    } //on create end

        public void showDatePickerDialog () {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    this,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

           // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
        @Override
        public void onDateSet (DatePicker view,int year, int month, int dayOfMonth){
            String d=""+ dayOfMonth;
            String m=""+ (month+1);
            String date;
            if(dayOfMonth<10){
                d ="0"+dayOfMonth;
            }
            if((month+1)<10){
                m ="0"+(month+1);
            }
            if(dayOfMonth<10 || (month+1)<10){
                date = d + "." + m + "." + year;
            }
            else{
                date =dayOfMonth + "." + (month+1)  + "." +  year;
            }
            selected_date=date.trim();
            check_availability();
        }
    public void onRadioButtonClicked(View v){
        int selectedRadioId = radio_shift.getCheckedRadioButtonId();
        radioButton=findViewById(selectedRadioId);
        selected_shift = radioButton.getText().toString().trim();
        Toast.makeText(AdminConfirmCancelBookingActivity.this,selected_shift+" is selected",Toast.LENGTH_SHORT).show();
    }


    // check whether date is available or reserved
    public void check_availability() {
        if (a.equals(selected_shift)) {
            ref_evening.document(selected_date).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            dateText.setText(selected_date);
                            // book.setEnabled(false);
                        }
                        else{
                            //document doesn't exist
                            StyleableToast.makeText(AdminConfirmCancelBookingActivity.this,"Document doesn't exist, choose another date",R.style.toast_alert).show();
                            dateText.setText("");
                            selected_date=null;
                        }
                    } else {
                        Toast.makeText(AdminConfirmCancelBookingActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
        else if(b.equals(selected_shift)){
            ref_day.document(selected_date).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            dateText.setText(selected_date);
                        }
                        else{
                            //document doesn't exist
                            StyleableToast.makeText(AdminConfirmCancelBookingActivity.this,"Document doesn't exist, choose another date",R.style.toast_alert).show();
                            dateText.setText("");
                            selected_date=null;
                        }
                    }
                    else{
                        Toast.makeText(AdminConfirmCancelBookingActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
        else if(selected_shift==null){
            Toast.makeText(AdminConfirmCancelBookingActivity.this, " select shift", Toast.LENGTH_SHORT).show();
        }
    }

    //after check button click; display booking info
    public void display_booking_status(){
        progressBar.setVisibility(View.VISIBLE);
        if(a.equals(selected_shift)){
            ref_evening.document(selected_date).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    String name_=documentSnapshot.getString("name");
                    String mobile_=documentSnapshot.getString("mobile");
                    String add_mobile_=documentSnapshot.getString("additional_mobile");
                    String email_=documentSnapshot.getString("email");
                    String booking_id=documentSnapshot.getString("booking_Id");
                    String shift_=documentSnapshot.getString("shift");
                    String event_=documentSnapshot.getString("event_type");
                    String booked_date_=documentSnapshot.getString("booked_date");
                    String date_request=documentSnapshot.getString("date_of_booking_request");
                    String payment_=documentSnapshot.getString("payment_status");
                    String booking_status_=documentSnapshot.getString("booking_status");
                    String by=documentSnapshot.getString("booked_by");

                    id.setText("Booking Id: "+booking_id);
                    shift.setText("Shift: "+shift_);
                    booked_date.setText("Booked Date: "+booked_date_);
                    email.setText("User Email: "+email_);
                    name.setText("Name: "+name_);
                    mobile.setText("Mobile No: "+mobile_);
                    add_mobile.setText("Additional Mobile No: "+add_mobile_);
                    request_date.setText("Date of booking request: "+date_request);
                    event.setText("Event Type: "+event_);
                    payment.setText("Payment Status: "+payment_);
                    booking_status.setText("Booking Status: "+booking_status_);
                    booked_by.setText("Booked by: "+by);

                    shift_to_delete_or_confirm=shift_;
                    booked_date_to_delete_or_confirm=booked_date_;


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminConfirmCancelBookingActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(b.equals(selected_shift)){
           ref_day.document(selected_date).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                   progressBar.setVisibility(View.GONE);
                   String name_=documentSnapshot.getString("name");
                   String mobile_=documentSnapshot.getString("mobile");
                   String add_mobile_=documentSnapshot.getString("additional_mobile");
                   String email_=documentSnapshot.getString("email");
                   String booking_id=documentSnapshot.getString("booking_Id");
                   String shift_=documentSnapshot.getString("shift");
                   String event_=documentSnapshot.getString("event_type");
                   String booked_date_=documentSnapshot.getString("booked_date");
                   String date_request=documentSnapshot.getString("date_of_booking_request");
                   String payment_=documentSnapshot.getString("payment_status");
                   String booking_status_=documentSnapshot.getString("booking_status");
                   String by=documentSnapshot.getString("booked_by");

                   id.setText("Booking Id: "+booking_id);
                   shift.setText("Shift: "+shift_);
                   booked_date.setText("Booked Date: "+booked_date_);
                   email.setText("User Email: "+email_);
                   name.setText("Name: "+name_);
                   mobile.setText("Mobile No: "+mobile_);
                   add_mobile.setText("Additional Mobile No: "+add_mobile_);
                   request_date.setText("Date of booking request: "+date_request);
                   event.setText("Event Type: "+event_);
                   payment.setText("Payment Status: "+payment_);
                   booking_status.setText("Booking Status: "+booking_status_);
                   booked_by.setText("Booked by: "+by);

                   shift_to_delete_or_confirm=shift_;
                   booked_date_to_delete_or_confirm=booked_date_;

               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   progressBar.setVisibility(View.GONE);
                   Toast.makeText(AdminConfirmCancelBookingActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
               }
           });
        }
    }
    //booking info search by id(Evening)
    public void show_booking_info_eve(){
        final LoadingDialog loadingDialog=new LoadingDialog(AdminConfirmCancelBookingActivity.this);
        loadingDialog.startLoadingDialog();
        final String ID=bookingID.getText().toString().trim();
        ref_evening.whereEqualTo("booking_Id",ID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    loadingDialog.dismissDialog();
                    StyleableToast.makeText(AdminConfirmCancelBookingActivity.this,"Booking Id "+ID+" doesn't exist",R.style.toast_alert).show();
                }
                else {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        loadingDialog.dismissDialog();
                        String name_=documentSnapshot.getString("name");
                        String mobile_=documentSnapshot.getString("mobile");
                        String add_mobile_=documentSnapshot.getString("additional_mobile");
                        String email_=documentSnapshot.getString("email");
                        String booking_id=documentSnapshot.getString("booking_Id");
                        String shift_=documentSnapshot.getString("shift");
                        String event_=documentSnapshot.getString("event_type");
                        String booked_date_=documentSnapshot.getString("booked_date");
                        String date_request=documentSnapshot.getString("date_of_booking_request");
                        String payment_=documentSnapshot.getString("payment_status");
                        String booking_status_=documentSnapshot.getString("booking_status");
                        String by=documentSnapshot.getString("booked_by");

                        id.setText("Booking Id: "+booking_id);
                        shift.setText("Shift: "+shift_);
                        booked_date.setText("Booked Date: "+booked_date_);
                        email.setText("User Email: "+email_);
                        name.setText("Name: "+name_);
                        mobile.setText("Mobile No: "+mobile_);
                        add_mobile.setText("Additional Mobile No: "+add_mobile_);
                        request_date.setText("Date of booking request: "+date_request);
                        event.setText("Event Type: "+event_);
                        payment.setText("Payment Status: "+payment_);
                        booking_status.setText("Booking Status: "+booking_status_);
                        booked_by.setText("Booked by: "+by);

                        shift_to_delete_or_confirm=shift_;
                        booked_date_to_delete_or_confirm=booked_date_;
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismissDialog();
                Toast.makeText(AdminConfirmCancelBookingActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    //booking info search by id(Evening)
   public void show_booking_info_day() {
       final LoadingDialog loadingDialog=new LoadingDialog(AdminConfirmCancelBookingActivity.this);
       loadingDialog.startLoadingDialog();
       final String ID=bookingID.getText().toString().trim();
       ref_day.whereEqualTo("booking_Id",ID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               if (queryDocumentSnapshots.isEmpty()){
                   loadingDialog.dismissDialog();
                   StyleableToast.makeText(AdminConfirmCancelBookingActivity.this,"Booking Id "+ID+" doesn't exist",R.style.toast_alert).show();
               }
               else {
                   for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                       loadingDialog.dismissDialog();
                       String name_=documentSnapshot.getString("name");
                       String mobile_=documentSnapshot.getString("mobile");
                       String add_mobile_=documentSnapshot.getString("additional_mobile");
                       String email_=documentSnapshot.getString("email");
                       String booking_id=documentSnapshot.getString("booking_Id");
                       String shift_=documentSnapshot.getString("shift");
                       String event_=documentSnapshot.getString("event_type");
                       String booked_date_=documentSnapshot.getString("booked_date");
                       String date_request=documentSnapshot.getString("date_of_booking_request");
                       String payment_=documentSnapshot.getString("payment_status");
                       String booking_status_=documentSnapshot.getString("booking_status");
                       String by=documentSnapshot.getString("booked_by");

                       id.setText("Booking Id: "+booking_id);
                       shift.setText("Shift: "+shift_);
                       booked_date.setText("Booked Date: "+booked_date_);
                       email.setText("User Email: "+email_);
                       name.setText("Name: "+name_);
                       mobile.setText("Mobile No: "+mobile_);
                       add_mobile.setText("Additional Mobile No: "+add_mobile_);
                       request_date.setText("Date of booking request: "+date_request);
                       event.setText("Event Type: "+event_);
                       payment.setText("Payment Status: "+payment_);
                       booking_status.setText("Booking Status: "+booking_status_);
                       booked_by.setText("Booked by: "+by);

                       shift_to_delete_or_confirm=shift_;
                       booked_date_to_delete_or_confirm=booked_date_;
                   }
               }
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               loadingDialog.dismissDialog();
               Toast.makeText(AdminConfirmCancelBookingActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
           }
       });
   }

    //booking info delete or cancel booking request
    public void delete_booking_info(){
        if (shift_to_delete_or_confirm.equals(a)){
            ref_evening.document(booked_date_to_delete_or_confirm).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    last_progressBar.setVisibility(View.GONE);
                    StyleableToast.makeText(AdminConfirmCancelBookingActivity.this,"Booking request Cancellation Successful",R.style.toast_sample).show();
                    recreate();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    last_progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminConfirmCancelBookingActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (b.equals(shift_to_delete_or_confirm)){
            ref_day.document(booked_date_to_delete_or_confirm).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    last_progressBar.setVisibility(View.GONE);
                    StyleableToast.makeText(AdminConfirmCancelBookingActivity.this,"Booking request Cancellation Successful",R.style.toast_sample).show();
                    recreate();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    last_progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminConfirmCancelBookingActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //booking status confirmation
    public  void confirm_booking_info(){
        String p_status="Paid";
        String b_status="Confirmed";

        if (shift_to_delete_or_confirm.equals(a)){
            Map<String, Object> booking_info = new HashMap<>();
            booking_info.put("payment_status",p_status);
            booking_info.put("booking_status",b_status);
            ref_evening.document(booked_date_to_delete_or_confirm).update(booking_info).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    last_progressBar.setVisibility(View.GONE);
                    StyleableToast.makeText(AdminConfirmCancelBookingActivity.this,"Booking status updated",R.style.toast_sample).show();
                    booking_status.setText("Booking Status: Confirmed");
                    payment.setText("Payment Status: Paid");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    last_progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminConfirmCancelBookingActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (b.equals(shift_to_delete_or_confirm)){
            Map<String, Object> booking_info = new HashMap<>();
            booking_info.put("payment_status",p_status);
            booking_info.put("booking_status",b_status);
            ref_day.document(booked_date_to_delete_or_confirm).update(booking_info).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    last_progressBar.setVisibility(View.GONE);
                    StyleableToast.makeText(AdminConfirmCancelBookingActivity.this,"Booking status updated",R.style.toast_sample).show();
                    booking_status.setText("Booking Status: Confirmed");
                    payment.setText("Payment Status: Paid");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    last_progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminConfirmCancelBookingActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        //here...
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

}
