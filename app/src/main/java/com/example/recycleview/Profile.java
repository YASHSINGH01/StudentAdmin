package com.example.recycleview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Profile extends androidx.fragment.app.Fragment {


    Button button;
    ImageView profileimage,navimage;
    FirebaseAuth muth;
    FirebaseFirestore firestore;
    FirebaseUser user;
    String userId;

    EditText name ,father,email,mother;
    StorageReference storageReference;
    private ProgressDialog progressDialog;

    public android.view.View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater,
                                          ViewGroup container, android.os.Bundle savedInstanceState) {

        android.view.View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final android.widget.TextView textView = root.findViewById(R.id.text_home);
        button=root.findViewById(R.id.button3);
        name=root.findViewById(R.id.name);
        father=root.findViewById(R.id.Fathername);
        email=root.findViewById(R.id.account_no);
        mother=root.findViewById(R.id.mother);
        muth= FirebaseAuth.getInstance();
        navimage=root.findViewById(R.id.imageView);
        firestore= FirebaseFirestore.getInstance();
        user=muth.getCurrentUser();
        userId=muth.getCurrentUser().getUid();
        profileimage =root.findViewById(R.id.imageView3);
        storageReference= FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(getContext());

        StorageReference profile= storageReference.child("Teachers/"+userId+"/Profile.png");
        profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileimage);

            }
        });
        button.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                String image = profile.getDownloadUrl().toString();
                String username=name.getText().toString().trim();
                String fathername=father.getText().toString().trim();
                String mail=email.getText().toString().trim();
                String mothername=mother.getText().toString().trim();
                if(TextUtils.isEmpty(username)||TextUtils.isEmpty(fathername)||TextUtils.isEmpty(mail)||TextUtils.isEmpty(mothername)) {
                    name.setError("Please fill it");
                    return;
                }

                // profile.getDownloadUrl().getResult();
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().build();


                user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        DocumentReference df=firestore.collection("Teachers").document(user.getUid());
                        java.util.Map<String,Object> userInfo = new HashMap<>();
                        userInfo.put("Name",username);
                        // userInfo.put("Email id",mail);
                        userInfo.put("Father Name",fathername);
                        userInfo.put("Mother Name",mothername);
                        // userInfo.put("Imageuri",profileimage);
                        userInfo.put("Imageurl",image);
                        df.set(userInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(getContext(), "Authentication Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
               /* if(user!=null) {
                    muth.signOut();
                    user = null;
                    Intent intent=new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                }*/

        profileimage.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1000);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1000){
            if (resultCode== Activity.RESULT_OK){
                Uri imageuri = data.getData();
                //  profileimage.setImageURI(imageuri);

                uploadImage(imageuri);
            }
        }
    }

    private void uploadImage(Uri imageuri) {
        // FirebaseStorage storage =FirebaseStorage.getInstance();
        //  StorageReference reference=storage.getReference();
        StorageReference horef= storageReference.child("Teachers/"+userId+"/Profile.png");

        horef.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                horef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.setTitle("Uploading");
                        progressDialog.setMessage("Please wait ");
                        progressDialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        },2000);
                        Picasso.get().load(uri).into(profileimage);
                    }
                });


                //Toast.makeText(getContext(),"Sucess",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });



    }
/* @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            StorageReference filepath = storageReference.child("userProfilePics").child("photo" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                   // Glide.with(getTargetFragment()).load(downloadUrl).into(profileImage);
                    java.util.Map<String,Object> userProfileImage = new HashMap<>();
                    userProfileImage.put("profileImage",downloadUrl.toString());
                  //  userReference.update(userProfileImage);
                }
            });
            }
           /* if (resultCode== Activity.RESULT_OK){
                Uri image=data.getData();

         }*/

}