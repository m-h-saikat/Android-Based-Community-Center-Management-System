package com.example.ccms.UserBooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.ccms.AdminBooking.AdminCheckBookingActivity;
import com.example.ccms.MainActivity;
import com.example.ccms.R;
import com.example.ccms.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private ActionBar actionBar;
    private ProgressBar booking_progressBar;
    private TextView dateText,b_name,b_mobile,b_mail,date_today;
    private EditText add_mobile,event_type,guests,address;
    private Button book;private RadioGroup radio_shift;
    private ImageButton date_picker;private RadioButton radioButton;
    private String rental_price=null;
    private String selected_shift,user_name,user_mobile;
    private String selected_date = null; private String selected_year=null;
    private String selected_month=null; //private String event=null,guest=null;
    private String a="Evening"; private String b="Day";
    private double eve; double day;
    private String currentDate,currentTime;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference ref_evening=db.collection("evening");
    private CollectionReference ref_day= db.collection("day");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        booking_progressBar=(ProgressBar)findViewById(R.id.booking_progressBar);
        radio_shift=(RadioGroup)findViewById(R.id.radio_shift);
        dateText = (TextView)findViewById(R.id.date_text);
        b_mail=(TextView)findViewById(R.id.b_email);
        b_name=(TextView)findViewById(R.id.b_name);
        b_mobile=(TextView)findViewById(R.id.b_mobile);
        book = (Button)findViewById(R.id.book);
        date_picker = (ImageButton)findViewById(R.id.date_pick);
        date_today=(TextView)findViewById(R.id.date_today);
        add_mobile=(EditText)findViewById(R.id.additional_mobile);
        event_type=(EditText)findViewById(R.id.booking_event_type);
        guests=(EditText)findViewById(R.id.booking_guests_no);
        address=(EditText)findViewById(R.id.booking_address);


        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("Booking Page");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(mAuth.getCurrentUser()==null){
            Toast.makeText(BookingActivity.this,"You need to log in",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(BookingActivity.this, login.class));
            finish();
        }
       else if(mAuth.getCurrentUser()!=null){
            b_mail.setText(mAuth.getCurrentUser().getEmail());
            String uid = mAuth.getCurrentUser().getUid();
            db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String name=documentSnapshot.getString("Name");
                    String mobile=documentSnapshot.getString("Mobile");
                    user_name=name;
                    user_mobile=mobile;
                    b_name.setText(name);
                    b_mobile.setText(mobile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(BookingActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }


        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();

            }
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        currentDate = simpleDateFormat.format(calendar.getTime());
        date_today.setText(currentDate);

// booking button
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e_t = event_type.getText().toString().trim();
                String guests_total = guests.getText().toString().trim();
                String applicant_address = address.getText().toString();

                if(applicant_address.isEmpty()){
                    address.setError("Enter your address");
                    address.requestFocus();
                    return;
                }
                else if(e_t.isEmpty()){
                    event_type.setError("Enter an event type. Example: Marriage");
                    event_type.requestFocus();
                    return;
                }
                else if(guests_total.isEmpty()){
                    guests.setError("Enter total number of guest");
                    guests.requestFocus();
                    return;
                }
                else if (selected_shift==null) {
                    Toast.makeText(BookingActivity.this, " select shift", Toast.LENGTH_SHORT).show();
                } else if (selected_date==null) {
                    Toast.makeText(BookingActivity.this, " select date", Toast.LENGTH_SHORT).show();
                }
                else {

                    //Confirmation dialogue
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(BookingActivity.this);
                    builder1.setMessage("\nDo you want to book this date?");
                    builder1.setCancelable(true);
                    builder1.setTitle("Booking Confirmation");
                    builder1.setIcon(R.drawable.booking_icon);

                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                          //  book.setBackgroundColor(Color.GREEN);
                            booking_progressBar.setVisibility(View.VISIBLE);
                            get_booking_ID();
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
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
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
        selected_year =String.valueOf(year);

        if(month==0){selected_month="January";}else if(month==1){selected_month="February";}
        else if(month==2){selected_month="March";}else if(month==3){selected_month="April";}
        else if(month==4){selected_month="May";}else if(month==5){selected_month="June";}
        else if(month==6){selected_month="July";}else if(month==7){selected_month="August";}
        else if(month==8){selected_month="September";}else if(month==9){selected_month="October";}
        else if(month==10){selected_month="November";}else if(month==11){selected_month="December";}

        // Toast.makeText(BookingActivity.this,"Selected month is "+selected_month,Toast.LENGTH_SHORT).show();
        //  Toast.makeText(BookingActivity.this,"Selected year is "+selected_year,Toast.LENGTH_SHORT).show();

        check_availability();
    }

    public void onRadioButtonClicked(View v){
        int selectedRadioId = radio_shift.getCheckedRadioButtonId();
        radioButton=findViewById(selectedRadioId);
        selected_shift = radioButton.getText().toString().trim();
        Toast.makeText(BookingActivity.this,selected_shift+" is selected",Toast.LENGTH_SHORT).show();
    }

    //getting booking Id
    public void get_booking_ID()
    {
        if(a.equals(selected_shift)){
            ref_evening.document("1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    eve=documentSnapshot.getDouble("e");
                    rental_price=documentSnapshot.getString("price");
                    booking_process_eve();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    booking_progressBar.setVisibility(View.GONE);
                    Toast.makeText(BookingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        else if (b.equals(selected_shift)) {
            ref_day.document("1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    day = documentSnapshot.getDouble("d");
                    rental_price=documentSnapshot.getString("price");
                    booking_process_day();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    booking_progressBar.setVisibility(View.GONE);
                    Toast.makeText(BookingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //booking process method
    public void booking_process_eve() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("h:mm a");
        currentTime=time.format(calendar.getTime());

        String add_applicant=address.getText().toString();
        String add_m=add_mobile.getText().toString().trim();
        if (add_m.isEmpty()) {
            add_m = "Empty";
        }
        String total_guests = guests.getText().toString().trim();
        String e_type = event_type.getText().toString().trim();
        String email = mAuth.getCurrentUser().getEmail();
        String shift = selected_shift.trim();
        String today = currentDate.trim();
        String c_time = currentTime.trim();
        String payment = "Pending";
        String b_status = "Not Confirmed";
        int e_id=(int)eve;
        String booking_Id = "E#"+e_id;
        String booking_type="Booked by user";

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date_format = null;
        try {
            date_format = sdf.parse(selected_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Map<String, Object> book = new HashMap<>();
        book.put("booking_Id", booking_Id);
        book.put("email", email);
        book.put("name",user_name);
        book.put("mobile",user_mobile);
        book.put("additional_mobile",add_m);
        book.put("address",add_applicant);
        book.put("event_type",e_type);
        book.put("number_of_guests",total_guests);
        book.put("booked_date", selected_date);
        book.put("month_of_booked_date",selected_month);
        book.put("year_of_booked_date",selected_year);
        book.put("shift", shift);
        book.put("date_of_booking_request", today);
        book.put("booking_time", c_time);
        book.put("payment_status",payment);
        book.put("booking_status",b_status);
        book.put("booked_by",booking_type);
        book.put("hall_rental_price",rental_price);
        book.put("format_booked_date",date_format);

        ref_evening.document(selected_date).set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    booking_progressBar.setVisibility(View.GONE);
                    StyleableToast.makeText(BookingActivity.this,"Booking Successful",R.style.toast_sample).show();
                    store_eve_variable();
                   // Toast.makeText(BookingActivity.this, "Booking successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BookingActivity.this, MainActivity.class));
                    finish();
                } else {
                    booking_progressBar.setVisibility(View.GONE);
                    String e = task.getException().getMessage();
                    Toast.makeText(BookingActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void booking_process_day() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("h:mm a");
        currentTime=time.format(calendar.getTime());

        String add_applicant=address.getText().toString();
        String add_m=add_mobile.getText().toString().trim();
        if (add_m.isEmpty()) {
            add_m = "Empty";
        }
        String total_guests = guests.getText().toString().trim();
        String e_type = event_type.getText().toString().trim();
        String email = mAuth.getCurrentUser().getEmail();
        String shift = selected_shift.trim();
        String today = currentDate.trim();
        String c_time = currentTime.trim();
        String payment = "Pending";
        String b_status = "Not Confirmed";
        int a=(int)day;
        String booking_Id = "D#" + a;
        String booking_type="Booked by user";

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date_format = null;
        try {
            date_format = sdf.parse(selected_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Map<String, Object> book = new HashMap<>();
        book.put("booking_Id", booking_Id);
        book.put("email", email);
        book.put("name",user_name);
        book.put("mobile",user_mobile);
        book.put("additional_mobile",add_m);
        book.put("address",add_applicant);
        book.put("event_type",e_type);
        book.put("number_of_guests",total_guests);
        book.put("booked_date", selected_date);
        book.put("month_of_booked_date",selected_month);
        book.put("year_of_booked_date",selected_year);
        book.put("shift", shift);
        book.put("date_of_booking_request", today);
        book.put("booking_time",c_time);
        book.put("payment_status",payment);
        book.put("booking_status",b_status);
        book.put("booked_by",booking_type);
        book.put("hall_rental_price",rental_price);
        book.put("format_booked_date",date_format);

        ref_day.document(selected_date).set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    booking_progressBar.setVisibility(View.GONE);
                    StyleableToast.makeText(BookingActivity.this,"Booking Successful",R.style.toast_sample).show();
                    store_day_variable();
                   // Toast.makeText(BookingActivity.this, "Booking successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BookingActivity.this, MainActivity.class));
                    finish();
                } else {
                    booking_progressBar.setVisibility(View.GONE);
                    String e = task.getException().getMessage();
                    Toast.makeText(BookingActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                            StyleableToast.makeText(BookingActivity.this,"Slot is already booked, choose another date",R.style.toast_alert).show();
                            dateText.setText("");
                            selected_date=null;
                            // book.setEnabled(false);
                        }
                        else{
                            //document doesn't exist
                            dateText.setText(selected_date);
                        }
                    } else {
                        Toast.makeText(BookingActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
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
                            StyleableToast.makeText(BookingActivity.this,"Slot is already booked, choose another date",R.style.toast_alert).show();
                            dateText.setText("");
                            selected_date=null;
                            // book.setEnabled(false);
                        }
                        else{
                            dateText.setText(selected_date);
                        }
                    }
                    else{
                        Toast.makeText(BookingActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
        else if(selected_shift==null){
            Toast.makeText(BookingActivity.this, " select shift", Toast.LENGTH_SHORT).show();
        }

    }


    //change the variable stored in database
    public void store_eve_variable(){
        double e_varibale =eve+1;
        Map<String, Object> v = new HashMap<>();
        v.put("e", e_varibale);
        ref_evening.document("1").update(v).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    //change the variable stored in database
    public void store_day_variable(){
        double d_varibale =day+1;
        Map<String, Object> v = new HashMap<>();
        v.put("d", d_varibale);
        ref_day.document("1").update(v).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

}

