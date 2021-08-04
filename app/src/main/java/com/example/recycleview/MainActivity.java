package com.example.recycleview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    EditText mail, password;
    TextView register, forget;
    Button button;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore flogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        mail = findViewById(R.id.editTextmail);
        password = findViewById(R.id.editTextpass);
        register = findViewById(R.id.Register);
        forget = findViewById(R.id.textView2);
        firebaseAuth = FirebaseAuth.getInstance();
        flogin = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
                finish();
            }
        });


        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Reset.class);
                startActivity(intent);
                finish();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _email = mail.getText().toString().trim();
                String _password = password.getText().toString().trim();

                if (TextUtils.isEmpty(_email)) {
                    mail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(_password)) {
                    password.setError("Password is required");
                    return;
                }


                firebaseAuth.signInWithEmailAndPassword(_email, _password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.setTitle("Logging In");
                        progressDialog.setMessage("Please wait ");
                        progressDialog.show();
                        checkUser(authResult.getUser().getUid());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }


            private void checkUser(String uid) {
                DocumentReference df = flogin.collection("Students").document(uid);
                //extract data from document
                df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d("TAG", "onSuccess" + documentSnapshot.getData());
                        //identify the user
                        if (documentSnapshot.getString("Student") != null) {
                            startActivity(new Intent(getApplicationContext(), Student.class));
                            finish();
                        }
                        if (documentSnapshot.getString("Teacher") != null) {
                            startActivity(new Intent(getApplicationContext(), Teacher.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
           /*  startActivity(new Intent(getApplicationContext(),Student.class));
            finish();*/
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DocumentReference df = FirebaseFirestore.getInstance().collection("Students").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("Please wait ");
                    progressDialog.show();
                    if (documentSnapshot.getString("Teacher") != null) {
                        startActivity(new Intent(getApplicationContext(), Teacher.class));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        },1000);
                        finish();
                    }
                    if (documentSnapshot.getString("Student") != null) {
                        startActivity(new Intent(getApplicationContext(), Student.class));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        },1000);
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}