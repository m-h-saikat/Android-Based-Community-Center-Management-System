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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminEditPackagesActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private ImageView package1_photo,package2_photo,package3_photo,package4_photo;
    private TextView package1_price,package2_price,package3_price,package4_price;
    private EditText pk1,pk2,pk3,pk4;
    private Button update1,update2,update3,update4;
    private ProgressBar progressBar1,progressBar2,progressBar3,progressBar4;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_packages);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar1 = (ProgressBar)findViewById(R.id.pk1_pro);
        progressBar2 = (ProgressBar)findViewById(R.id.pk2_pro);
        progressBar3 = (ProgressBar)findViewById(R.id.pk3_pro);
        progressBar4 = (ProgressBar)findViewById(R.id.pk4_pro);

        pk1 = (EditText)findViewById(R.id.pk1_price_admin);
        pk2 = (EditText)findViewById(R.id.pk2_price_admin);
        pk3 = (EditText)findViewById(R.id.pk3_price_admin);
        pk4 = (EditText)findViewById(R.id.pk4_price_admin);

        update1 = (Button)findViewById(R.id.btn_pk1_update);
        update2 = (Button)findViewById(R.id.btn_pk2_update);
        update3 = (Button)findViewById(R.id.btn_pk3_update);
        update4 = (Button)findViewById(R.id.btn_pk4_update);


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("Edit Packages");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(mAuth.getCurrentUser()!=null){
            et_price_show();

        }
        else{
            Toast.makeText(AdminEditPackagesActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
        }

        update1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price1 = pk1.getText().toString().trim();
                if(price1.isEmpty()) {
                    pk1.setError("Please enter a price");
                    pk1.requestFocus();
                    return;
                }else{
                    progressBar1.setVisibility(View.VISIBLE);
                    DocumentReference documentReference=db.collection("packages").document("1");
                    Map<String, Object> pk = new HashMap<>();
                    pk.put("price",price1);
                        documentReference.update(pk).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar1.setVisibility(View.GONE);
                            Toast.makeText(AdminEditPackagesActivity.this,"Successfully updated ",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar1.setVisibility(View.GONE);
                            Toast.makeText(AdminEditPackagesActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        update2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price2 = pk2.getText().toString().trim();
                if(price2.isEmpty()) {
                    pk2.setError("Please enter a price");
                    pk2.requestFocus();
                    return;
                }else{
                    progressBar2.setVisibility(View.VISIBLE);
                    DocumentReference documentReference=db.collection("packages").document("2");
                    Map<String, Object> pk = new HashMap<>();
                    pk.put("price",price2);
                    documentReference.update(pk).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar2.setVisibility(View.GONE);
                            Toast.makeText(AdminEditPackagesActivity.this,"Successfully updated ",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar2.setVisibility(View.GONE);
                            Toast.makeText(AdminEditPackagesActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

  update3.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          String price3 = pk3.getText().toString().trim();
          if(price3.isEmpty()) {
              pk3.setError("Please enter a price");
              pk3.requestFocus();
              return;
          }else{
              progressBar3.setVisibility(View.VISIBLE);
              DocumentReference documentReference=db.collection("packages").document("3");
              Map<String, Object> pk = new HashMap<>();
              pk.put("price",price3);
              documentReference.update(pk).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                      progressBar3.setVisibility(View.GONE);
                      Toast.makeText(AdminEditPackagesActivity.this,"Successfully updated ",Toast.LENGTH_SHORT).show();
                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      progressBar3.setVisibility(View.GONE);
                      Toast.makeText(AdminEditPackagesActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                  }
              });
          }
      }
  });

  update4.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          String price4 = pk4.getText().toString().trim();
          if(price4.isEmpty()) {
              pk4.setError("Please enter a price");
              pk4.requestFocus();
              return;
          }else{
              progressBar4.setVisibility(View.VISIBLE);
              DocumentReference documentReference=db.collection("packages").document("4");
              Map<String, Object> pk = new HashMap<>();
              pk.put("price",price4);
              documentReference.update(pk).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                      progressBar4.setVisibility(View.GONE);
                      Toast.makeText(AdminEditPackagesActivity.this,"Successfully updated ",Toast.LENGTH_SHORT).show();

                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      progressBar4.setVisibility(View.GONE);
                      Toast.makeText(AdminEditPackagesActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                  }
              });
          }
      }
  });


    }

    
    public void et_price_show(){

        db.collection("packages").document("1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String p = null;
                String price = documentSnapshot.getString("price");
                p = price;
                pk1.setText(p);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminEditPackagesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        db.collection("packages").document("2").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String p = null;
                String price = documentSnapshot.getString("price");
                p = price;
                pk2.setText(p);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminEditPackagesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        db.collection("packages").document("3").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String p = null;
                String price = documentSnapshot.getString("price");
                p = price;
                pk3.setText(p);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminEditPackagesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        db.collection("packages").document("4").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String p=null;
                String price = documentSnapshot.getString("price");
                p = price;
                pk4.setText(p);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminEditPackagesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

}
