 package com.example.recycleview;

 import android.content.Intent;
 import android.os.Bundle;
 import android.text.TextUtils;
 import android.view.View;
 import android.widget.Button;
 import android.widget.CheckBox;
 import android.widget.CompoundButton;
 import android.widget.EditText;
 import android.widget.ProgressBar;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;

 import com.google.android.gms.tasks.OnFailureListener;
 import com.google.android.gms.tasks.Task;
 import com.google.firebase.auth.AuthResult;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;
 import com.google.firebase.firestore.DocumentReference;
 import com.google.firebase.firestore.FirebaseFirestore;

 import java.util.HashMap;
 import java.util.Map;

 public class Register extends AppCompatActivity {

     EditText name, email, pass, phone;
     Button button1;
     FirebaseAuth firebaseAuth;
     FirebaseFirestore firestore;
     ProgressBar progressBar;
     CheckBox student,teacher;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_register);
         name = findViewById(R.id.editTextname);
         email = findViewById(R.id.editTextmail);
         pass = findViewById(R.id.editTextpass);
         phone = findViewById(R.id.editTextphone);
         firebaseAuth = FirebaseAuth.getInstance();
         firestore= FirebaseFirestore.getInstance();
         button1 = findViewById(R.id.button1);
         progressBar=findViewById(R.id.progressBar);
         teacher=findViewById(R.id.Teacher);
         student=findViewById(R.id.Student);
         //student.setChecked(false);

         //checkbox choose one

         student.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(buttonView.isChecked()){
                     teacher.setChecked(false);
                 }
             }
         });
         teacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(buttonView.isChecked()){
                    // student.setChecked(false);
                 }
             }
         });

         button1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                 String _name = name.getText().toString().trim();
                 String _emailr = email.getText().toString().trim();
                 String password = pass.getText().toString().trim();
                 String Phone = phone.getText().toString().trim();
                 if(TextUtils.isEmpty(_name)){
                     name.setError("Email is required");
                     return;
                 }
                 if(TextUtils.isEmpty(_emailr)){
                     email.setError("Email is required");
                     return;
                 }
                 if(TextUtils.isEmpty(Phone)){
                     phone.setError("Phone no. is required");
                     return;
                 }
                 if (TextUtils.isEmpty(password))
                 {
                     pass.setError("Password is required");
                     return;
                 }
                 if (password.length()<6)
                 {
                     pass.setError("Password length is less than 6");
                     return;
                 }


                 //checkbox  validation
                 if(!(teacher.isChecked()||student.isChecked()))
                 {
                    Toast.makeText(getApplicationContext(),"Select the Account",Toast.LENGTH_SHORT).show();
                    return;
                 }
                     firebaseAuth.createUserWithEmailAndPassword(_emailr, password)
                             .addOnCompleteListener(com.example.recycleview.Register.this, (Task<AuthResult> task) -> {
                                 if (task.isSuccessful()) {
                                     progressBar.setVisibility(View.VISIBLE);
                                     FirebaseUser user =firebaseAuth.getCurrentUser();
                                     //save to database
                                     if(teacher.isChecked()){
                                         DocumentReference df=firestore.collection("Teachers").document(user.getUid());
                                         Map<String,Object> userInfo = new HashMap<>();
                                         userInfo.put("Name",_name);
                                         userInfo.put("Email id",_emailr);
                                         userInfo.put("Phone no",Phone);
                                         //userInfo.put("Email ID",user.getUid());

                                         df.set(userInfo);
                                      /*   FirebaseDatabase database = FirebaseDatabase.getInstance();
                                         DatabaseReference node = database.getReference("Teacher");
                                         model obj = new model(_name, _emailr, password,Phone);
                                         node.child(_name).setValue(obj);*/
                                     }

                                          DocumentReference df=firestore.collection("Students").document(user.getUid());
                                         Map<String,Object> userInfo = new HashMap<>();
                                         userInfo.put("Name",_name);
                                         userInfo.put("Email id",_emailr);
                                         userInfo.put("Phone no",Phone);
                                     /*    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                         model obj = new model(_name, _emailr, password,Phone);
                                         DatabaseReference node = database.getReference("Student");
                                         node.child(_name).setValue(obj);*/

                                     Toast.makeText(com.example.recycleview.Register.this, "Authentication done", Toast.LENGTH_SHORT).show();
                                     // Sign in success, update UI with the signed-in user's information

                                     startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                     // specify access level
                                     if (teacher.isChecked())
                                     {
                                         userInfo.put("Teacher","1");
                                     }
                                     if (student.isChecked())
                                     {
                                         userInfo.put("Student","1");
                                     }

                                     df.set(userInfo);

                                     if (student.isChecked())
                                     {
                                         startActivity(new Intent(getApplicationContext(), Student.class));
                                     finish();
                                     }
                                     if (teacher.isChecked())
                                     {
                                         startActivity(new Intent(getApplicationContext(), Teacher.class));
                                         finish();
                                     }
                                     progressBar.setVisibility(View.INVISIBLE);
                                     email.setText("");
                                     pass.setText("");
                                     phone.setText("");
                                     name.setText("");

                                     }
                               /* if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                     Toast.makeText(Register.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                 }*/
                             }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(com.example.recycleview.Register.this, "Authentication Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                         }
                     });
                 }
         });
     }

 }
