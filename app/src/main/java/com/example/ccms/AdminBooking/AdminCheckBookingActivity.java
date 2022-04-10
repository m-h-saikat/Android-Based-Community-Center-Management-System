package com.example.ccms.AdminBooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccms.MainActivity;
import com.example.ccms.R;
import com.example.ccms.UserBooking.BookingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muddzdev.styleabletoast.StyleableToast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminCheckBookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private TextView date_today,dateText;
    private ActionBar actionBar;
    private Button btn_reserve;
    private CardView btn1,btn2;
    private ProgressBar progressBar; private String a="Evening"; private String b="Day";
    private String currentDate,currentTime;
    private String selected_date = null; private String selected_year=null;
    private String selected_month=null; private double eve; double day;
    private ImageButton date_picker;
    private RadioGroup radio_shift;
    private RadioButton radioButton;
    private String selected_shift;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference ref_evening=db.collection("evening");
    private CollectionReference ref_day= db.collection("day");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_booking);

        btn1=(CardView) findViewById(R.id.admin_manual1);
        btn2=(CardView) findViewById(R.id.admin_manual2);
        btn_reserve=(Button)findViewById(R.id.admin_reserve_btn);
        progressBar=(ProgressBar)findViewById(R.id.admin_reserve_progressBar);
        date_today=(TextView)findViewById(R.id.date_today);
        date_picker=(ImageButton)findViewById(R.id.date_pick_manual);
        radio_shift=(RadioGroup)findViewById(R.id.radio_shift_manual);
        dateText=(TextView)findViewById(R.id.date_text_manual);

       btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(new Intent(AdminCheckBookingActivity.this,AdminConfirmCancelBookingActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminCheckBookingActivity.this,AdminDeleteHistoryActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
      btn_reserve.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             // check_availability();
              if (selected_shift==null) {
                  Toast.makeText(AdminCheckBookingActivity.this, " select shift", Toast.LENGTH_SHORT).show();
              } else if (selected_date==null) {
                  Toast.makeText(AdminCheckBookingActivity.this, " select date", Toast.LENGTH_SHORT).show();
              }
              else {
                  btn_reserve.setTextColor(Color.WHITE);
                  //Confirmation dialogue
                  AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminCheckBookingActivity.this);
                  builder1.setMessage("\nDo you want to book this date?");
                  builder1.setCancelable(true);
                  builder1.setTitle("Booking Confirmation");
                  builder1.setIcon(R.drawable.booking_icon);

                  builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                          //  book.setBackgroundColor(Color.GREEN);
                          progressBar.setVisibility(View.VISIBLE);
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

        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



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

        check_availability();
    }
    public void onRadioButtonClicked(View v){
        int selectedRadioId = radio_shift.getCheckedRadioButtonId();
        radioButton=findViewById(selectedRadioId);
        selected_shift = radioButton.getText().toString().trim();
        Toast.makeText(AdminCheckBookingActivity.this,selected_shift+" is selected",Toast.LENGTH_SHORT).show();
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
                            StyleableToast.makeText(AdminCheckBookingActivity.this,"Slot is already booked, choose another date",R.style.toast_alert).show();
                            dateText.setText("");
                            selected_date=null;
                            // book.setEnabled(false);
                        }
                        else{
                            //document doesn't exist
                            dateText.setText(selected_date);
                        }
                    } else {
                        Toast.makeText(AdminCheckBookingActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
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
                            StyleableToast.makeText(AdminCheckBookingActivity.this,"Slot is already booked, choose another date",R.style.toast_alert).show();
                            dateText.setText("");
                            selected_date=null;
                            // book.setEnabled(false);
                        }
                        else{
                            dateText.setText(selected_date);
                        }
                    }
                    else{
                        Toast.makeText(AdminCheckBookingActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
        else if(selected_shift==null){
            Toast.makeText(AdminCheckBookingActivity.this, " select shift", Toast.LENGTH_SHORT).show();
        }

    }

    //getting booking Id
    public void get_booking_ID()
    {
        if(a.equals(selected_shift)){
            ref_evening.document("1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    eve=documentSnapshot.getDouble("e");
                    booking_process_eve();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminCheckBookingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        else if (b.equals(selected_shift)) {
            ref_day.document("1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    day = documentSnapshot.getDouble("d");
                    booking_process_day();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminCheckBookingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //booking process evening
    public void booking_process_eve(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("h:mm a");
        currentTime=time.format(calendar.getTime());
        String booking_type="Booked by admin";
        String mail=mAuth.getCurrentUser().getEmail();
        String shift = selected_shift.trim();
        String today = currentDate.trim();
        String c_time = currentTime.trim();
        int e_id=(int)eve;
        String booking_Id = "E#"+e_id;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date_format = null;
        try {
            date_format = sdf.parse(selected_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String b_status="Confirmed";
        String p_status="Paid";
        Map<String, Object> book = new HashMap<>();
        book.put("booking_Id", booking_Id);
        book.put("booked_date", selected_date);
        book.put("email",mail);
        book.put("month_of_booked_date",selected_month);
        book.put("year_of_booked_date",selected_year);
        book.put("shift", shift);
        book.put("date_of_booking_request", today);
        book.put("booking_time", c_time);
        book.put("booking_status",b_status);
        book.put("payment_status",p_status);
        book.put("booking_type",booking_type);
        book.put("format_booked_date",date_format);

        ref_evening.document(selected_date).set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    StyleableToast.makeText(AdminCheckBookingActivity.this,"Booking Successful",R.style.toast_sample).show();
                    selected_date=null;
                    dateText.setText("");
                    store_eve_variable();
                    // Toast.makeText(BookingActivity.this, "Booking successful", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    String e = task.getException().getMessage();
                    Toast.makeText(AdminCheckBookingActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //booking process day
    public void booking_process_day(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("h:mm a");
        currentTime=time.format(calendar.getTime());
        String booking_type="Booked by admin";
        String mail=mAuth.getCurrentUser().getEmail();
        String shift = selected_shift.trim();
        String today = currentDate.trim();
        String c_time = currentTime.trim();
        int a=(int)day;
        String booking_Id = "D#" + a;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date_format = null;
        try {
            date_format = sdf.parse(selected_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String b_status="Confirmed";
        String p_status="Paid";
        Map<String, Object> book = new HashMap<>();
        book.put("booking_Id", booking_Id);
        book.put("booked_date", selected_date);
        book.put("email",mail);
        book.put("month_of_booked_date",selected_month);
        book.put("year_of_booked_date",selected_year);
        book.put("shift", shift);
        book.put("date_of_booking_request", today);
        book.put("booking_time",c_time);
        book.put("booking_status",b_status);
        book.put("payment_status",p_status);
        book.put("booked_by",booking_type);
        book.put("format_booked_date",date_format);

        ref_day.document(selected_date).set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    StyleableToast.makeText(AdminCheckBookingActivity.this,"Booking Successful",R.style.toast_sample).show();
                    selected_date=null;
                    dateText.setText("");
                    store_day_variable();
                    // Toast.makeText(BookingActivity.this, "Booking successful", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    String e = task.getException().getMessage();
                    Toast.makeText(AdminCheckBookingActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
