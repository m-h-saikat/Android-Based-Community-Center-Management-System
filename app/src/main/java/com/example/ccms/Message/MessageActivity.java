package com.example.ccms.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ccms.LoadingDialog;
import com.example.ccms.MainActivity;
import com.example.ccms.R;
import com.example.ccms.login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity implements UserMessageAdapter.OnItemClickListener {
    private ActionBar actionBar;
    private EditText user_message;
    private Button send_button;
    private ProgressBar progressBar;
    private ProgressBar progressBar_recycle;
    private UserMessageAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<UserMessage> mUploads;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference messageRef = db.collection("messages");
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar)findViewById(R.id.message_progressBar);
        progressBar_recycle=(ProgressBar)findViewById(R.id.progress_circle_user_sms);
        user_message = (EditText)findViewById(R.id.user_send_message);
        send_button=(Button)findViewById(R.id.send_button);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("Message");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {        // perfect kaj kore
                main_method();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sms = user_message.getText().toString();

                if(mAuth.getCurrentUser()==null){
                    Toast.makeText(MessageActivity.this,"You need to log in to send a message",Toast.LENGTH_SHORT).show();
                    Intent i= new Intent(MessageActivity.this, login.class);
                    startActivity(i);
                    finish();
                }
                else if(sms.isEmpty()){
                    user_message.setError("Please write a message");
                    user_message.requestFocus();
                    return;
                }

                else{
                    progressBar.setVisibility(View.VISIBLE);

                    String email = mAuth.getCurrentUser().getEmail();
                    String status="Not Replied";
                    Map<String, Object> message = new HashMap<>();
                    message.put("Email", email);
                    message.put("User_Message", sms);
                    message.put("Status",status);
                    db.collection("messages").document().set(message)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            StyleableToast.makeText(MessageActivity.this,"Your message is successfully sent",R.style.toast_sample).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MessageActivity.this,"Error: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        mRecyclerView = findViewById(R.id.recycler_view_user_sms);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();
        mAdapter =new UserMessageAdapter(MessageActivity.this,mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(MessageActivity.this);

        if (mAuth.getCurrentUser()==null) {
            Toast.makeText(MessageActivity.this,"You need to log in",Toast.LENGTH_SHORT).show();
            Intent i= new Intent(MessageActivity.this, login.class);
            startActivity(i);
            finish();
        }
        else{
            main_method();
        }
    }

    public void main_method() {
        String a="Replied";
        String email=mAuth.getCurrentUser().getEmail();
        messageRef.whereEqualTo("Status", a)
                .whereEqualTo("Email",email)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                mUploads.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    UserMessage upload = documentSnapshot.toObject(UserMessage.class);
                    upload.setKey(documentSnapshot.getId());
                    mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                progressBar_recycle.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        UserMessage selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(MessageActivity.this);
        builder1.setMessage("\nDo you want to delete history?");
        //builder1.setTitle("Delete History");
       // builder1.setIcon(R.drawable.alert_icon);
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final LoadingDialog loadingDialog=new LoadingDialog(MessageActivity.this);
                loadingDialog.startLoadingDialog();

                messageRef.document(selectedKey).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialog.dismissDialog();
                        StyleableToast.makeText(MessageActivity.this,"Deleted",R.style.toast_sample).show();
                        main_method();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(MessageActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
