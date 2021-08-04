package com.example.recycleview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.recycleview.dummy.DummyContent;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.example.recycleview.Assignment;


public class download extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    TextView soo;
    ListView listView;
    StorageReference storageReference;
    FirebaseAuth muth;
    FirebaseFirestore firestore;
    FirebaseUser user;
    String userId;

    DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    List<Note> upload;


    // String url="https://console.firebase.google.com/project/studentadmin-c27d5/storage/studentadmin-c27d5.appspot.com/files";
    public download() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static download newInstance(int columnCount) {
        download fragment = new download();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        soo=view.findViewById(R.id.item_number);
        listView=view.findViewById(R.id.list);
        storageReference= FirebaseStorage.getInstance().getReference("Teachers");
        muth= FirebaseAuth.getInstance();
        firestore= FirebaseFirestore.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        user=muth.getCurrentUser();
        userId=muth.getCurrentUser().getUid();

        upload=new ArrayList<>();

        try {
            downloadpdf();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note uplod = upload.get(position);

                //Opening the upload file in browser using the upload url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uplod.getUrl()));
                startActivity(intent);
               // storageReference = FirebaseStorage.getInstance().getReference(Constants.EXTRA_RESULT_RECEIVER);
            }
        });
        return view;
    }

    private void downloadpdf() throws IOException {
        databaseReference=FirebaseDatabase.getInstance().getReference("upload");
     //   StorageReference reference= storageReference.child("Assignment.pdf");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren() ){
                    Note pdf= ds.getValue(com.example.recycleview.Note.class);
                    upload.add(pdf);
                }
                String[] uploadname= new String[upload.size()];
                for (int i=0;i<uploadname.length;i++)
                {
                    uploadname[i]=upload.get(i).getNamee();

                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1,uploadname)
                {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view=super.getView(position, convertView, parent);
                        soo.setTextColor(Color.BLACK);
                        soo.setTextSize(40);
                        return view;
                    }
                };
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}