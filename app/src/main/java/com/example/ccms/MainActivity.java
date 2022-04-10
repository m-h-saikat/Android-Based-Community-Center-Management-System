package com.example.ccms;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.example.ccms.FAQ.LastActivity;
import com.example.ccms.Gallery.GalleryActivity;
import com.example.ccms.ImageSlider.ImageModel;
import com.example.ccms.ImageSlider.SlidingImage_Adapter;
import com.example.ccms.Message.MessageActivity;
import com.example.ccms.Rating.RatingActivity;
import com.example.ccms.UserBooking.BookingActivity;
import com.example.ccms.UserBooking.BookingHistoryActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.viewpagerindicator.CirclePageIndicator;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ActionBar actionBar;
    private Button login,register,log_out,update;
    private CardView card1,card2,card3,card4,card5,card6,card7,card8;
    private TextView name,email;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String admin = "admin@gmail.com";

    //for ImageSlider
    private CirclePageIndicator indicator;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;
    private int[] myImageList = new int[]{R.drawable.ccms_photo, R.drawable.slide0,
            R.drawable.slide1,R.drawable.slide2,R.drawable.slide3,R.drawable.slide4,R.drawable.slide5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar=getSupportActionBar();
        actionBar.setTitle("Home");

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        //for sliding image
        mPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        //end

        name=(TextView)findViewById(R.id.name);
        login=(Button)findViewById(R.id.signin_button);
        register=(Button)findViewById(R.id.signup_button);
        card1 = (CardView)findViewById(R.id.card1);
        card2 = (CardView)findViewById(R.id.card2);
        card3=(CardView)findViewById(R.id.card3);
        card4=(CardView)findViewById(R.id.card4);
        card5=(CardView)findViewById(R.id.card5);
        card6=(CardView)findViewById(R.id.card6);
        card8=(CardView)findViewById(R.id.card8);


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        View headerView=navigationView.getHeaderView(0);
        name=(TextView)headerView.findViewById(R.id.name);
        email=(TextView)headerView.findViewById(R.id.email);
        log_out=(Button)headerView.findViewById(R.id.button_log_out);
        update=(Button)headerView.findViewById(R.id.button_update_profile);


        if (mAuth.getCurrentUser() != null) {
            String user_email = mAuth.getCurrentUser().getEmail();
            email.setText(user_email);

            String uid = mAuth.getCurrentUser().getUid();
            db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String user_name=documentSnapshot.getString("Name");
                    name.setText(user_name);
                }
            });

        }


       if(mAuth.getCurrentUser()!=null && !admin.equals(mAuth.getCurrentUser().getEmail()) ){

           update.setVisibility(View.VISIBLE);
           log_out.setVisibility(View.VISIBLE);

           update.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent i= new Intent(MainActivity.this,UpdateProfileActivity.class);
                   startActivity(i);
                   overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
               }
           });

           log_out.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   FirebaseAuth.getInstance().signOut();
                   Toast.makeText(MainActivity.this,"Log out successfully",Toast.LENGTH_SHORT).show();
                   drawer.closeDrawer(GravityCompat.START);
                   update.setVisibility(View.GONE);
                   log_out.setVisibility(View.GONE);
                   name.setText(" ");
                   email.setText(" ");
               }
           });
       }


      login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,login.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
      register.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent i = new Intent(MainActivity.this,SignupActivity.class);
              startActivity(i);
              overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
          }
      });
      card1.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i = new Intent(MainActivity.this,FoodPackagesActivity.class);
              startActivity(i);
              overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
          }
      });
      card2.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(MainActivity.this, BookingActivity.class));
              overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
          }
      });
      card3.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(MainActivity.this,BookingInfoActivity.class));
              overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
          }
      });

      card4.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i = new Intent(MainActivity.this, MessageActivity.class);
              startActivity(i);
              overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
          }
      });
      card5.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i = new Intent(MainActivity.this, RatingActivity.class);
              startActivity(i);
              overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
          }
      });

      card6.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent i = new Intent(MainActivity.this,ContactUsActivity.class);
              startActivity(i);
              overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
          }
      });
      card8.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i = new Intent(MainActivity.this, LastActivity.class);
              startActivity(i);
              overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
          }
      });

      //for Image Slider
        imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();
        init();
    }

    //code for Image Slider starts here
    private ArrayList<ImageModel> populateList(){

        ArrayList<ImageModel> list = new ArrayList<>();

        for(int i = 0; i < 7; i++){
            ImageModel imageModel = new ImageModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }
    private void init() {
        //mPager = (ViewPager) findViewById(R.id.pager);
       mPager.setAdapter(new SlidingImage_Adapter(MainActivity.this,imageModelArrayList));

       // CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES =imageModelArrayList.size();

        // Auto start of viewpager

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 5000, 5000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });
    }
// Code for Image Slider ends here



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void exit_button() {
         AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       //MainActivity.super.onBackPressed();
                       finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog =builder.create();
        alertDialog.show();
    }


   /* public void onBackPressed(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog =builder.create();
        alertDialog.show();

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id== R.id.app_exit){
            exit_button();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_booking_history){
            Intent i = new Intent(MainActivity.this, BookingHistoryActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        else if (id == R.id.nav_admin) {
                Intent i = new Intent(MainActivity.this, AdminLoginActivity.class);
                startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_gallery) {

            Intent i = new Intent(MainActivity.this, GalleryActivity.class);
            startActivity(i);

        } else if (id == R.id.about) {
            startActivity(new Intent(MainActivity.this,AboutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if (id == R.id.nav_share) {
            Intent myintent = new Intent(Intent.ACTION_SEND);
            myintent.setType("text/plain");
            String shareBody="https://grade-conversion-tool.en.aptoide.com/";
            myintent.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(myintent,"Share using"));
        }
        else if(id==R.id.rate_app){
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://en.aptoide.com/store/mahmudul-rumc")));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://grade-conversion-tool.en.aptoide.com/")));
            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
