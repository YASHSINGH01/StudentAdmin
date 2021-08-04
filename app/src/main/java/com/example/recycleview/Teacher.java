package com.example.recycleview;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Teacher extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    FirebaseAuth firestudent;
    FirebaseFirestore fistudent;
    FirebaseUser user;
    Intent data;
    Uri imageuri,uri;
    String userId;
    StorageReference storageReference;
    AlertDialog.Builder alertDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_layout);
        drawerLayout=findViewById(R.id.drawerlayout);
        navigationView=findViewById(R.id.navigation);
        firestudent=FirebaseAuth.getInstance();
        //profileimage =findViewById(R.id.imageView);
        fistudent=FirebaseFirestore.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        userId=firestudent.getCurrentUser().getUid();
        storageReference= FirebaseStorage.getInstance().getReference();
        alertDialog = new AlertDialog.Builder(this);

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);


       // Uri imaguri=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user!=null)
                {
                    alertDialog.setTitle("Log Out");
                    alertDialog.setMessage("Are you sure!");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user=null;
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            firestudent.signOut();
                            finish();
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                        }
                    });
                    alertDialog.create();
                    alertDialog.show();
                    // Snackbar.make(view, "Logging Out", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }


            }
        });
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,new Payment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);

        NavigationView navigationView=findViewById(R.id.navigation);
        Menu menu =navigationView.getMenu();
        MenuItem item= menu.findItem(R.id.nav);
        item.setVisible(false);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            Fragment temp;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        temp= new Profile();
                        break;

                    case R.id.nav_atten:
                        temp= new Assignment();
                        break;

                    case R.id.item_pay:
                        temp= new Payment();
                        break;
                }
               getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,temp).commit();

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        View headerview=navigationView.getHeaderView(0);
        TextView navname= headerview.findViewById(R.id.textView);
        TextView navphone=headerview.findViewById(R.id.textView5);
        ImageView imageView= headerview.findViewById(R.id.imageView);

        DocumentReference df=fistudent.collection("Students").document(userId);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot!=null)
                {
//                    String s3=documentSnapshot.get("Imageurl").toString();
                    String s=documentSnapshot.getString("Name");
                    String s1=documentSnapshot.getString("Phone no");
                   //Picasso.get().load(s3).into(imageView);
                    navname.setText(s);
                    navphone.setText(s1);
                    ;

                }
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}