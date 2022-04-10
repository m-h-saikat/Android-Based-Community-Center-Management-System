package com.example.ccms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.HashMap;
import java.util.Map;

public class AdminRentalActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private RadioGroup radio_shift;
    private RadioButton radioButton;
    private String selected_shift=null; private String a="Evening"; private String b="Day";
    private TextView currentRent; private EditText edt_price; private Button update;

    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference ref_evening=db.collection("evening");
    private CollectionReference ref_day= db.collection("day");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_rental);

        radio_shift=(RadioGroup)findViewById(R.id.radio_shift_manual);
        currentRent=(TextView)findViewById(R.id.current_rent);
        edt_price=(EditText)findViewById(R.id.edt_rent);
        update=(Button)findViewById(R.id.rent_update_button);

        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("Rental Information");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edt=edt_price.getText().toString().trim();
                if (selected_shift==null){
                    StyleableToast.makeText(AdminRentalActivity.this,"Select shift",R.style.toast_alert).show();
                }
                else if (edt.isEmpty()){
                    edt_price.setError("Set a new price");
                    edt_price.requestFocus();
                }
                else{
                    update_method();
                }
            }
        });
    }

    public void onRadioButtonClicked(View v){
        int selectedRadioId = radio_shift.getCheckedRadioButtonId();
        radioButton=findViewById(selectedRadioId);
        selected_shift = radioButton.getText().toString().trim();
        Toast.makeText(AdminRentalActivity.this,selected_shift+" is selected",Toast.LENGTH_SHORT).show();
        currentRentMethod();
    }
    //Find current rent
    public void currentRentMethod(){

        if (a.equals(selected_shift)){
            ref_evening.document("1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String current_price=documentSnapshot.getString("price");
                    currentRent.setText(current_price+" BDT");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminRentalActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (b.equals(selected_shift)){
            ref_day.document("1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String current_price=documentSnapshot.getString("price");
                    currentRent.setText(current_price+" BDT");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminRentalActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            StyleableToast.makeText(AdminRentalActivity.this,"Select shift",R.style.toast_alert).show();
        }
    }

    //price update
    public void update_method(){
        final LoadingDialog loadingDialog=new LoadingDialog(AdminRentalActivity.this);

        final String price=edt_price.getText().toString().trim();
        Map<String,Object>update_price=new HashMap<>();
        update_price.put("price",price);
        if (a.equals(selected_shift)){
            loadingDialog.startLoadingDialog();
            ref_evening.document("1").update(update_price).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loadingDialog.dismissDialog();
                    StyleableToast.makeText(AdminRentalActivity.this,"Updated",R.style.toast_sample).show();
                    currentRent.setText(price);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(AdminRentalActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (b.equals(selected_shift)){
            loadingDialog.startLoadingDialog();
                ref_day.document("1").update(update_price).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialog.dismissDialog();
                        StyleableToast.makeText(AdminRentalActivity.this,"Updated",R.style.toast_sample).show();
                        currentRent.setText(price);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(AdminRentalActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            StyleableToast.makeText(AdminRentalActivity.this,"Select shift",R.style.toast_alert).show();
        }

    }


    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
