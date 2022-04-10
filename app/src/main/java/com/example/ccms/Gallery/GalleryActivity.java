package com.example.ccms.Gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ccms.R;
import com.example.ccms.login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ActionBar actionBar;
    private ProgressBar mProgressCircle;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();


    private List<Upload> mUploads;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference uploadRef = db.collection("uploads");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("Gallery");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(GalleryActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(GalleryActivity.this, login.class));
            finish();
        } else {


            mRecyclerView = findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mProgressCircle = findViewById(R.id.progress_circle);
            mUploads = new ArrayList<>();
            //Two lines below changed
            mAdapter = new ImageAdapter(GalleryActivity.this, mUploads);
            mRecyclerView.setAdapter(mAdapter);

            uploadRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    //one new line added
                    mUploads.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Upload upload = documentSnapshot.toObject(Upload.class);
                        mUploads.add(upload);
                    }
                  // mAdapter = new ImageAdapter(GalleryActivity.this, mUploads);
                   // mRecyclerView.setAdapter(mAdapter);
                    //extra line added below
                   mAdapter.notifyDataSetChanged();

                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GalleryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }
    }
}
