package com.example.ccms.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccms.LoadingDialog;
import com.example.ccms.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminReplyActivity extends AppCompatActivity implements ReplyAdapter.OnItemClickListener {
    private ActionBar actionBar;
    private ProgressBar progressBar;
    private ReplyAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<UserMessage> mUploads;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference messageRef = db.collection("messages");
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reply);
        progressBar=(ProgressBar)findViewById(R.id.progress_circle_admin_sms);
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("Reply Message");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {        // perfect kaj kore
                main();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view_admin_sms);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();
        //Three lines changed (position)
        mAdapter =new ReplyAdapter(AdminReplyActivity.this,mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(AdminReplyActivity.this);
        main();

    }
    public void main() {
        String a="Not Replied";
        messageRef.whereEqualTo("Status", a).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                mUploads.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    UserMessage upload = documentSnapshot.toObject(UserMessage.class);
                    upload.setKey(documentSnapshot.getId());
                    mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminReplyActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReplyClick(int position) {
        UserMessage selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

       final AlertDialog.Builder alert=new AlertDialog.Builder(AdminReplyActivity.this);
       View mView =getLayoutInflater().inflate(R.layout.reply_dialogue,null);
       final EditText reply_text = (EditText)mView.findViewById(R.id.et_reply_dialogue);
        Button cancel=(Button)mView.findViewById(R.id.cancel_dialogue);
        Button send=(Button)mView.findViewById(R.id.send_dialogue);

        alert.setView(mView);
        final AlertDialog alertDialog=alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message=reply_text.getText().toString();
                if (message.isEmpty()){
                    reply_text.setError("Write a message");
                    reply_text.requestFocus();
                }
                else{
                    alertDialog.dismiss();
                   final LoadingDialog loadingDialog=new LoadingDialog(AdminReplyActivity.this);
                    loadingDialog.startLoadingDialog();

                    String b="Replied";
                    Map<String,Object>reply=new HashMap<>();
                    reply.put("Status",b);
                    reply.put("Replied_Message",message);
                    messageRef.document(selectedKey).update(reply).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loadingDialog.dismissDialog();
                            StyleableToast.makeText(AdminReplyActivity.this,"Successfully replied",R.style.toast_sample).show();
                            main();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(AdminReplyActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        alertDialog.show();
    }

    @Override
    public void onDeleteClick(int position) {
        UserMessage selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminReplyActivity.this);
        builder1.setMessage("\nDo you want to delete without reply?");
        builder1.setCancelable(true);
        builder1.setTitle("Delete Message");
        builder1.setIcon(R.drawable.alert_icon);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final LoadingDialog loadingDialog=new LoadingDialog(AdminReplyActivity.this);
                loadingDialog.startLoadingDialog();

                messageRef.document(selectedKey).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialog.dismissDialog();
                        StyleableToast.makeText(AdminReplyActivity.this,"Message deleted without reply",R.style.toast_sample).show();
                        main();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(AdminReplyActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
