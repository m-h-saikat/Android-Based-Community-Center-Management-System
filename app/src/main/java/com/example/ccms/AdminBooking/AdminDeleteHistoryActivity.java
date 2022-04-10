package com.example.ccms.AdminBooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccms.R;
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

import java.util.Calendar;

public class AdminDeleteHistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private ActionBar actionBar;
    private Spinner spinner;
    private static final String[] paths={"January","February","March","April","May","June","July","August","September",
            "October","November","December"};
    private ProgressBar progressBar1,progressBar2;
    private Button delete1,delete2; private EditText year;
    private String selected_date = null; private String chosen_month=null;
    private ImageButton date_picker1;private TextView dateText1;
    private RadioButton radioButton,radioButton2;private RadioGroup radio_shift,radio_shift2;
    private String selected_shift=null,selected_shift2=null;
    private String a="Evening"; private String b="Day";
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference ref_evening=db.collection("evening");
    private CollectionReference ref_day= db.collection("day");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_history);

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminDeleteHistoryActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        chosen_month="January";
                        break;
                    case 1:
                        chosen_month="February";
                        break;
                    case 2:
                        chosen_month="March";
                        break;
                    case 3:
                        chosen_month="April";
                        break;
                    case 4:
                        chosen_month="May";
                        break;
                    case 5:
                        chosen_month="June";
                        break;
                    case 6:
                        chosen_month="July";
                        break;
                    case 7:
                        chosen_month="August";
                        break;
                    case 8:
                        chosen_month="September";
                        break;
                    case 9:
                        chosen_month="October";
                        break;
                    case 10:
                        chosen_month="November";
                        break;
                    case 11:
                        chosen_month="December";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chosen_month=null;
            }
        });


        delete1=(Button)findViewById(R.id.delete_btn1);
        delete2=(Button)findViewById(R.id.delete_month);
        date_picker1=(ImageButton)findViewById(R.id.date_picker_1);
        dateText1=(TextView)findViewById(R.id.date_text_1);
        radio_shift=(RadioGroup)findViewById(R.id.radio_shift_1);
        radio_shift2=(RadioGroup)findViewById(R.id.radio_shift_2);
        progressBar1=(ProgressBar)findViewById(R.id.progressBar1);
        progressBar2=(ProgressBar)findViewById(R.id.progressBar2);
        year=(EditText)findViewById(R.id.year);


        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_shift==null ||selected_date==null) {
                    StyleableToast.makeText(AdminDeleteHistoryActivity.this, "Select both shift and date", R.style.toast_alert).show();
                }
                else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminDeleteHistoryActivity.this);
                    builder1.setMessage("\nDo you want to delete? It'll delete the booking info from database");
                    builder1.setCancelable(true);
                    builder1.setTitle("Booking Info Deletion");
                    builder1.setIcon(R.drawable.alert_icon);

                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            progressBar1.setVisibility(View.VISIBLE);
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
        delete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String y=year.getText().toString().trim();
                if (selected_shift2==null ||chosen_month==null||y.isEmpty()) {
                    StyleableToast.makeText(AdminDeleteHistoryActivity.this, "Select shift, month and year", R.style.toast_alert).show();
                }
                else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminDeleteHistoryActivity.this);
                    builder1.setMessage("\nDo you want to delete? It'll delete the whole booking info of selected month");
                    builder1.setCancelable(true);
                    builder1.setTitle("Booking history Deletion");
                    builder1.setIcon(R.drawable.alert_icon);

                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            progressBar2.setVisibility(View.VISIBLE);
                            delete_booking_info_month();
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


        date_picker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

    }
    public void showDatePickerDialog () {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    @Override
    public void onDateSet (DatePicker view, int year, int month, int dayOfMonth){
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
        Toast.makeText(AdminDeleteHistoryActivity.this,selected_shift+" is selected",Toast.LENGTH_SHORT).show();
    }
    public void onRadioButtonClicked2(View v){
        int selectedRadioId = radio_shift2.getCheckedRadioButtonId();
        radioButton2=findViewById(selectedRadioId);
        selected_shift2 = radioButton2.getText().toString().trim();
        Toast.makeText(AdminDeleteHistoryActivity.this,selected_shift2+" is selected",Toast.LENGTH_SHORT).show();
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
                            dateText1.setText(selected_date);
                        }
                        else{
                            //document doesn't exist
                            StyleableToast.makeText(AdminDeleteHistoryActivity.this,"Document doesn't exist, choose another date",R.style.toast_alert).show();
                            dateText1.setText("");
                            selected_date=null;
                        }
                    } else {
                        Toast.makeText(AdminDeleteHistoryActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
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
                            dateText1.setText(selected_date);
                        }
                        else{
                            //document doesn't exist
                            StyleableToast.makeText(AdminDeleteHistoryActivity.this,"Document doesn't exist, choose another date",R.style.toast_alert).show();
                            dateText1.setText("");
                            selected_date=null;
                        }
                    }
                    else{
                        Toast.makeText(AdminDeleteHistoryActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
        else if(selected_shift==null){
            Toast.makeText(AdminDeleteHistoryActivity.this, " select shift", Toast.LENGTH_SHORT).show();
        }
    }

    //booking info delete single
    public void delete_booking_info(){
        if (selected_shift.equals(a)){
            ref_evening.document(selected_date).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressBar1.setVisibility(View.GONE);
                    StyleableToast.makeText(AdminDeleteHistoryActivity.this,"Booking history deleted Successful",R.style.toast_sample).show();
                   // recreate();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar1.setVisibility(View.GONE);
                    Toast.makeText(AdminDeleteHistoryActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (b.equals(selected_shift)){
            ref_day.document(selected_date).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressBar1.setVisibility(View.GONE);
                    StyleableToast.makeText(AdminDeleteHistoryActivity.this,"Booking history deleted Successful",R.style.toast_sample).show();
                    //recreate();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar1.setVisibility(View.GONE);
                    Toast.makeText(AdminDeleteHistoryActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

// booking info delete month wise
    public void delete_booking_info_month(){
        String y=year.getText().toString().trim();
        if (a.equals(selected_shift2)){
            ref_evening.whereEqualTo("month_of_booked_date",chosen_month)
                    .whereEqualTo("year_of_booked_date",y)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    //It works fine. I,Mahmudul, have tested it
                    if (queryDocumentSnapshots.isEmpty()) {
                        progressBar2.setVisibility(View.GONE);
                        Toast.makeText(AdminDeleteHistoryActivity.this, "No document found", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getReference().delete();
                        }
                        progressBar2.setVisibility(View.GONE);
                        StyleableToast.makeText(AdminDeleteHistoryActivity.this, "History successfully deleted", R.style.toast_sample).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar2.setVisibility(View.GONE);
                    Toast.makeText(AdminDeleteHistoryActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
        else if(b.equals(selected_shift2)){

            ref_day.whereEqualTo("month_of_booked_date",chosen_month)
                    .whereEqualTo("year_of_booked_date",y)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    //It works fine. I,Mahmudul,have tested it
                    if (queryDocumentSnapshots.isEmpty()) {
                        progressBar2.setVisibility(View.GONE);
                        Toast.makeText(AdminDeleteHistoryActivity.this, "No document found", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getReference().delete();
                        }
                        progressBar2.setVisibility(View.GONE);
                        StyleableToast.makeText(AdminDeleteHistoryActivity.this, "History successfully deleted", R.style.toast_sample).show();
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar2.setVisibility(View.GONE);
                    Toast.makeText(AdminDeleteHistoryActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
