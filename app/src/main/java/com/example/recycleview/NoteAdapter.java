package com.example.recycleview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note,NoteAdapter.NoteHolder>
{

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull Note model) {

        holder.name.setText(model.getTitle());
        //holder.mail.setText(model.getMail());
       // holder.number.setText(model.getPriority());
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
        return new NoteHolder(view);
    }

    class NoteHolder extends RecyclerView.ViewHolder
    {

        TextView name,mail,number;
        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.t1);
            mail=itemView.findViewById(R.id.t2);
            number=itemView.findViewById(R.id.number);
        }
    }
}
