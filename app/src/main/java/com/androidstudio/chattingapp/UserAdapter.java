package com.androidstudio.chattingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<UserDetailwithUrl> {

    private final Context context;
    ImageView iv;
    FirebaseDatabase database;
    private ArrayList<UserDetailwithUrl> list;

    public interface itemSelected
    {
        public void onItemSelected(int index);
    }

    itemSelected Activity;

    public UserAdapter(@NonNull Context context, ArrayList<UserDetailwithUrl>list) {
        super(context, R.layout.chats_listlayout,list);

        this.context = context;
        this.list = list;
        Activity = (itemSelected) context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v= inflater.inflate(R.layout.chats_listlayout,parent,false);
        iv=v.findViewById(R.id.imageView);

        final ImageView ivStatus = v.findViewById(R.id.ivStatus);
        TextView tvMessageNum = v.findViewById(R.id.tvMessageNum);

        database=FirebaseDatabase.getInstance();
        DatabaseReference dbreference=database.getReference();
        Log.d("tag",list.get(position).getUrl());

        if(list.get(position).getUrl().equals("null"))
        {
            iv.setImageResource(R.drawable.person);
        }
        else
        {
            Glide.with(context).load(list.get(position).getUrl()).into(iv);}


        dbreference.child("UserStatus").child(Check(list.get(position).getPh_number())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    if (dataSnapshot.getValue(String.class).equals("online"))
                        ivStatus.setVisibility(View.VISIBLE);
                    else
                        ivStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        TextView tvUserName= v.findViewById(R.id.tv_username);
        tvUserName.setText(list.get(position).getuID());

        if(list.get(position).getMessagenum() > 2)
        {
            tvMessageNum.setText(list.get(position).getMessagenum()-2);
            tvMessageNum.setVisibility(View.VISIBLE);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity.onItemSelected(position);
            }
        });

        return v;
    }

    public String Check(String phone)
    {
        String newPhone;
        if(!phone.substring(0,3).equals("+91"))
        {
            newPhone = "+91".concat(phone);
            return newPhone;
        }
        else
            return phone;

    }

}
