package com.androidstudio.chattingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<UserDetail> {

    private final Context context;
    ImageView iv;
    FirebaseDatabase database;
    DatabaseReference reference;
    private ArrayList<UserDetail> list;

    public interface itemSelected
    {
        public void onItemSelected(int index);
    }

    itemSelected Activity;

    public UserAdapter(@NonNull Context context, ArrayList<UserDetail> list) {
        super(context, R.layout.chats_list_layout,list);
        this.context = context;
        this.list = list;
        Activity = (itemSelected) context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v= inflater.inflate(R.layout.chats_list_layout,parent,false);
        iv=v.findViewById(R.id.imageView);
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();

        reference.child("users").child(list.get(position).getPh_number()).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.getKey().equals("profile"))
                        {
                            Glide.with(context)
                                    .load(dataSnapshot.getValue())
                                    .into(iv);
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        TextView tvUserName= v.findViewById(R.id.tv_username);
        tvUserName.setText(list.get(position).getuID());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity.onItemSelected(position);
            }
        });

        return v;
    }

}
