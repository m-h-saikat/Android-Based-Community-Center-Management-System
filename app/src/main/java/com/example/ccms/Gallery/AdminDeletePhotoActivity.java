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

import com.example.ccms.AdminLoginActivity;
import com.example.ccms.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminDeletePhotoActivity extends AppCompatActivity implements AdminImageAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private AdminImageAdapter mAdapter;
    private ActionBar actionBar;
    private ProgressBar mProgressCircle;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();

    private List<Upload> mUploads;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference uploadRef = db.collection("uploads");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_photo);


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        actionBar.setTitle("Delete Photo");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(AdminDeletePhotoActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AdminDeletePhotoActivity.this, AdminLoginActivity.class));
            finish();
        }

        else {

            mRecyclerView = findViewById(R.id.recycler_view_admin);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mProgressCircle = findViewById(R.id.progress_circle_admin);
            mUploads = new ArrayList<>();
          //Three lines changed (position)
           mAdapter =new AdminImageAdapter(AdminDeletePhotoActivity.this,mUploads);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(AdminDeletePhotoActivity.this);

            uploadRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    //extra line added
                   mUploads.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Upload upload = documentSnapshot.toObject(Upload.class);
                        upload.setKey(documentSnapshot.getId());
                        mUploads.add(upload);
                    }
                  /*mAdapter =new AdminImageAdapter(AdminDeletePhotoActivity.this,mUploads);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(AdminDeletePhotoActivity.this);*/
                  //etra line added below
                    mAdapter.notifyDataSetChanged();

                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminDeletePhotoActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position+"\nLong tab to delete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(final int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               db.collection("uploads").document(selectedKey).delete();
                Toast.makeText(AdminDeletePhotoActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                mUploads.remove(position);
                mAdapter.notifyItemRemoved(position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminDeletePhotoActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
