package com.example.recycleview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;


public class Assignment extends Fragment {
    ImageView imageView;
    Button upload;
    EditText edit;
    StorageReference storageReference;
    FirebaseAuth muth;
    FirebaseFirestore firestore;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String userId;
    private ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_assignment, container, false);
        imageView=root.findViewById(R.id.imageView5);
        storageReference=FirebaseStorage.getInstance().getReference("Teachers");
        muth= FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("upload");
       // edit=root.findViewById(R.id.editTextTextPersonName);
        firestore= FirebaseFirestore.getInstance();
        user=muth.getCurrentUser();
        userId=muth.getCurrentUser().getUid();
        upload=root.findViewById(R.id.upload);
        progressDialog = new ProgressDialog(getContext());

        imageView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                selectPdf();
            }
        });
        return  root;
    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"PDF FILE SELECT"),12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==12 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            upload.setEnabled(true);
           // edit.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));

            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPDF(data.getData());
                }
            });
            
        }
    }

    private void uploadPDF(Uri data) {
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        StorageReference reference= storageReference.child("Assignment"+System.currentTimeMillis()+".pdf");

        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri ui=uriTask.getResult();

                Note pdf= new Note(imageView.toString(),ui.toString());
                firestore.collection("Teacher").getId();
                databaseReference.child(databaseReference.push().getKey()).setValue(pdf);
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
               double progress=(100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("File Uploaded"+(int)progress+"%");
            }
        });
    }
}