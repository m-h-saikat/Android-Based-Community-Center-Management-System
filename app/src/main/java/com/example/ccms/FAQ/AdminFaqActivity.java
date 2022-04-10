package com.example.ccms.FAQ;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ccms.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.HashMap;
import java.util.Map;

public class AdminFaqActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private ProgressBar add_progressBar,update_progressBar,delete_progressBar;
    private Button faq_add,faq_update,faq_get,faq_delete;
    private EditText id_add,id_update,id_delete,add_ques,add_ans,update_ques,update_ans;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ref = db.collection("questions");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_faq);

        add_progressBar=(ProgressBar)findViewById(R.id.add_faq_progressbar);
        update_progressBar=(ProgressBar)findViewById(R.id.update_faq_progressbar);
        delete_progressBar=(ProgressBar)findViewById(R.id.delete_faq_progressbar);
        faq_add=(Button)findViewById(R.id.btn_faq_add);
        faq_get=(Button)findViewById(R.id.btn_faq_get);
        faq_update=(Button)findViewById(R.id.btn_faq_update);
        faq_delete=(Button)findViewById(R.id.btn_faq_delete);
        id_add=(EditText)findViewById(R.id.doc_id_add);
        id_update=(EditText)findViewById(R.id.doc_id_update);
        id_delete=(EditText)findViewById(R.id.doc_id_delete);
        add_ques=(EditText)findViewById(R.id.add_faq_Q);
        add_ans=(EditText)findViewById(R.id.add_faq_ans);
        update_ques=(EditText)findViewById(R.id.update_faq_Q);
        update_ans=(EditText)findViewById(R.id.update_faq_ans);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("Manage FAQ");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        faq_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String doc_id=id_add.getText().toString().trim();
                String ques=add_ques.getText().toString();
                String ans=add_ans.getText().toString();
                if (doc_id.isEmpty()){
                    id_add.setError("Enter a document Id");
                    id_add.requestFocus();
                    return;
                }
                else if (ques.isEmpty()){
                    add_ques.setError("Write a question");
                    add_ques.requestFocus();
                    return;
                }
                else if(ans.isEmpty()){
                    add_ans.setError("Write an answer");
                    add_ans.requestFocus();
                    return;
                }else {
                    add_faq_method();
                }
            }
        });
        faq_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = id_update.getText().toString().trim();
                if (id.isEmpty()) {
                    id_update.setError("Enter a document id");
                    id_update.requestFocus();
                    return;
                }
                else {
                    get_faq_method();
                }
            }
        });


        faq_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=id_update.getText().toString().trim();
                String ques=update_ques.getText().toString();
                String ans=update_ans.getText().toString();
                if(id.isEmpty()){
                    id_update.setError("Enter a document id");
                    id_update.requestFocus();
                    return;
                }
                else if(ques.isEmpty()){
                    update_ques.setError("Write a question");
                    update_ques.requestFocus();
                    return;
                }
                else if(ans.isEmpty()){
                    update_ans.setError("Write an answer");
                    update_ans.requestFocus();
                    return;
                }
                else{
                    update_faq_method();
                }
            }
        });

        faq_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String doc_id=id_delete.getText().toString().trim();
                if(doc_id.isEmpty()){
                    id_delete.setError("Enter document Id to be deleted, Example:3");
                    id_delete.requestFocus();
                    return;
                }
                else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminFaqActivity.this);
                    builder1.setMessage("\nDo you want to delete?");
                    builder1.setCancelable(true);
                    builder1.setTitle("Document Delete");
                    builder1.setIcon(R.drawable.alert_icon);

                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            delete_progressBar.setVisibility(View.VISIBLE);
                            ref.document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot documentSnapshot =task.getResult();
                                    if(task.isSuccessful()){
                                        if (documentSnapshot.exists()) {
                                            //document id exists
                                            ref.document(doc_id).delete();
                                            delete_progressBar.setVisibility(View.GONE);
                                            StyleableToast.makeText(AdminFaqActivity.this,"Successfully deleted",R.style.toast_sample).show();
                                        }
                                        else{
                                            //document doesn't exist
                                            delete_progressBar.setVisibility(View.GONE);
                                            id_delete.setError("Document doesn't exist");
                                            id_delete.requestFocus();
                                            return;
                                        }
                                    }
                                    else{
                                        delete_progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AdminFaqActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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

    public void add_faq_method(){
        final String id=id_add.getText().toString().trim();
        final String ques=add_ques.getText().toString();
        final String ans=add_ans.getText().toString();
        add_progressBar.setVisibility(View.VISIBLE);

        ref.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot =task.getResult();
                if(task.isSuccessful()){
                    if (documentSnapshot.exists()) {
                        //document id exists
                        add_progressBar.setVisibility(View.GONE);
                        id_add.setError("This document Id already exists, Try another");
                        id_add.requestFocus();
                        return;
                    }
                    else{
                        //document doesn't exist
                        Map<String, Object> add = new HashMap<>();
                        add.put("question",ques);
                        add.put("answer",ans);
                       ref.document(id).set(add).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               add_progressBar.setVisibility(View.GONE);
                               StyleableToast.makeText(AdminFaqActivity.this,"Successfully added",R.style.toast_sample).show();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               add_progressBar.setVisibility(View.GONE);
                               Toast.makeText(AdminFaqActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                           }
                       });
                    }
                }
                else{
                    add_progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminFaqActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void get_faq_method(){
        final String id = id_update.getText().toString().trim();


        ref.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot =task.getResult();
                if(task.isSuccessful()){
                    if (documentSnapshot.exists()) {
                        //document id exists
                        ref.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String ques=documentSnapshot.getString("question");
                                String ans =documentSnapshot.getString("answer");
                                update_ques.setText(ques);
                                update_ans.setText(ans);
                            }
                        });

                    }
                    else{
                        //document doesn't exist
                        id_update.setError("Document doesn't exist, Enter an existing document Id");
                        id_update.requestFocus();
                        return;
                    }
                }
                else{
                    Toast.makeText(AdminFaqActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void update_faq_method(){
        update_progressBar.setVisibility(View.VISIBLE);
        String id=id_update.getText().toString().trim();
        String ques=update_ques.getText().toString();
        String ans=update_ans.getText().toString();
        Map<String, Object> updateDoc = new HashMap<>();
        updateDoc.put("question",ques);
        updateDoc.put("answer",ans);
        ref.document(id).update(updateDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                update_progressBar.setVisibility(View.GONE);
                StyleableToast.makeText(AdminFaqActivity.this,"Successfully updated",R.style.toast_sample).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                update_progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminFaqActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
